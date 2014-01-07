/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import org.apache.commons.io.FileUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO;

/**
 *
 * @author sasvara
 */
public class DomainContext {
    private BackingBean backingBean;
    private DomainHandler domainHandler;
    private String newDomain;
    private String newSubdomains;
    private Object selectedDomain;
    
    private DomainContext(BackingBean backingBean) {
        this.backingBean = backingBean;
        init();
    }
    
    
    
    public static DomainContext create(BackingBean backingBean) {
        return new DomainContext(backingBean);
    }

    /**
     * @return the selectedDomain
     */
    public Object getSelectedDomain() {
        return selectedDomain;
    }

    /**
     * @param selectedDomain the selectedDomain to set
     */
    public void setSelectedDomain(Object selectedDomain) {
        this.selectedDomain = selectedDomain;
    }

    private boolean isDomainUsed(String domainStr) {
        List<String> dependantApplications = getApplicationsUsingDomainOrSubdomain(domainStr);
        if (!dependantApplications.isEmpty()) {
            backingBean.addMessage(
                    null, 
                    FacesMessage.SEVERITY_ERROR, 
                    String.format("Cannot delete '%s'. "
                    + "Following application(s) depend(s) on it: %s", 
                    domainStr, dependantApplications.toString()), null);
            return true;
        }
        return false;
    }

    private void deleteDomain(String domainStr, Object domain) {
        for (String sd : domainHandler.extractSubdomains(domainStr)) {
            backingBean.addMessage(
                    null, 
                    FacesMessage.SEVERITY_INFO, 
                    String.format("Subdomain deleted: '%s'", domainStr), null);
        }
        
        domainHandler.removeDomain((String) domain);            
        
        backingBean.addMessage(
            null, 
            FacesMessage.SEVERITY_INFO, 
            String.format("Domain deleted: '%s'", (String) domain), null);
    }

    private void deleteSubdomains(Object domain) {
        Subdomain sd = (Subdomain) domain;
        domainHandler.removeSubdomain(sd.domain, sd.subdomain);
        backingBean.addMessage(
            null, 
            FacesMessage.SEVERITY_INFO, 
            String.format("Subdomain deleted: '%s'", (String) sd.subdomain), null);
    }
    
    class Subdomain extends Object {
        String domain;
        String subdomain;
        public Subdomain(String d, String s) {
            domain = d;
            subdomain = s;
        }
        
        @Override
        public String toString() {
            return subdomain;
        }
    }        

    private void init() {
        File domainConfig = new File("/srv/shiwa/domains.json");
        if (!domainConfig.exists()) {
            try {
                FileUtils.copyFile(new File(BackingBean.class.getClassLoader().getResource("META-INF/domains.json").getFile()), domainConfig);
            } catch (IOException ex) {
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        domainHandler = DomainHandler.create(domainConfig);
    }

    public DomainHandler getDomainHandler() {
        return domainHandler;
    }        
    
    public List<String> getApplicationsUsingDomainOrSubdomain(String domain) {
        List<String> dependantApplications = new ArrayList<String>();
        for (WorkflowSummary wf : backingBean.getWorkflowSummaries()) {
            if (wf.getDomain().equals(domain)
                    || (wf.getSubdomain() != null && wf.getSubdomain().equals(domain))) {
                dependantApplications.add(wf.getWorkflowName());
            }                        
        }
        return dependantApplications;
    }
    
    public void deleteDomain(Object domain) {
        String domainStr = domain.toString();
        if (isDomainUsed(domainStr)) return;
        
        if (domain instanceof Subdomain) {
            deleteSubdomains(domain);
        } else {                                  
            deleteDomain(domainStr, domain);
        }
        domainHandler.persist();
        AppAttrTree.refreshDomains();        
    }

    /**
     * @param newDomain the newDomain to set
     */
    public void setNewDomain(String newDomain) {
        this.newDomain = newDomain;
    }

    /**
     * @return the newSubdomains
     */
    public String getNewSubdomains() {
        return newSubdomains;
    }

    public void createNewDomain() {
        if (newDomain.isEmpty()) {
            return;
        }
        domainHandler.addDomain(newDomain);
        backingBean.addMessage(null, FacesMessage.SEVERITY_INFO, String.format("Domain created: '%s'", newDomain), null);
        domainHandler.persist();
        AppAttrTree.refreshDomains();
    }

    /**
     * @return the newDomain
     */
    public String getNewDomain() {
        return newDomain;
    }

    /**
     * @param newSubdomains the newSubdomains to set
     */
    public void setNewSubdomains(String newSubdomains) {
        this.newSubdomains = newSubdomains;
    }

    public void createNewSubdomains() {
        if (newDomain.isEmpty()) {
            return;
        }
        if (!domainHandler.extractDomains().contains(newDomain)) {
            createNewDomain();
        }
        if (newSubdomains.isEmpty() || newSubdomains.matches("\\s+")) {
            backingBean.addMessage(null, FacesMessage.SEVERITY_ERROR, String.format("Subdomain shall not be empty"), null);
            return;
        }
        for (String s : newSubdomains.split("\\s*[;,]\\s*")) {
            domainHandler.addSubdomain(newDomain, s);
            backingBean.addMessage(null, FacesMessage.SEVERITY_INFO, String.format("Subdomain created: '%s'", s), null);
        }
        domainHandler.persist();
        domainHandler.extractDomainsAndSubdomains(AppAttrTree.domains, AppAttrTree.subDomains);
        AppAttrTree.refreshDomains();
    }

    public TreeNode getDomainsTreeNode() {
        TreeNode root = new DefaultTreeNode("root", null);
        for (String d : domainHandler.extractDomains()) {
            TreeNode domainNode = new DefaultTreeNode(d, d, root);
            for (String sd : domainHandler.extractSubdomains(d)) {
                TreeNode subdomainNode = new DefaultTreeNode(sd, new Subdomain(d, sd), domainNode);
            }
        }
        return root;
    }
    
}
