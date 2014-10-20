/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import com.ibm.icu.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.shiwa.repository.toolkit.transferobjects.ConfigurationRTO;
import org.shiwa.repository.toolkit.transferobjects.PortRTO;
import uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO;

/**
 *
 * @author kukla
 */
public class WorkflowSummary implements Comparable<WorkflowSummary>{
    int workflowId;
    String workflowName;
    String description;
    String status;
    String keywords;
    String domain;
    String linkAppDesc;
    private String subdomain;
    String application;
    String owner;
    String group;
    Date created;
    Date updated;
    List<PortRTO> inputs = new ArrayList<PortRTO>();
    List<PortRTO> outputs = new ArrayList<PortRTO>();
    List<ConfigurationRTO> confs = new ArrayList<ConfigurationRTO>();
    ApplicationTO to;
   
    @Override
    public int compareTo(WorkflowSummary w) {
        return workflowName.compareTo(w.workflowName);
    }
    
    List<ImplementationSummary> imps = null;


    public WorkflowSummary(int workflowId, String workflowName, String description, String status, Date created, Date updated, String keywords, String domain, String application, String owner, String group, List<PortRTO> inputs, List<PortRTO> outputs, List<ConfigurationRTO> confs,  List<ImplementationSummary> imps, ApplicationTO to, String linkAppDesc) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.description = description;
        this.status = status;
        this.created = created;
        this.updated = updated;
        this.keywords = keywords;
        this.domain = domain;
        this.application = application;
        this.owner = owner;
        this.group = group;
        this.inputs = inputs;
        this.outputs = outputs;
        this.confs = confs;
        this.imps = imps;
        this.to = to;
        this.linkAppDesc = linkAppDesc;
    }
    
    public WorkflowSummary(int workflowId, String workflowName, String description, String status, Date created, Date updated, String keywords, String domain, String application, String owner, String group, List<PortRTO> inputs, List<PortRTO> outputs, List<ConfigurationRTO> confs, ApplicationTO to, String linkAppDesc) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.description = description;
        this.status = status;
        this.created = created;
        this.updated = updated;
        this.keywords = keywords;
        this.domain = domain;
        this.application = application;
        this.owner = owner;
        this.group = group;
        this.inputs = inputs;
        this.outputs = outputs;
        this.confs = confs;
        this.to = to;
        this.linkAppDesc = linkAppDesc;
    }
    
    
    public WorkflowSummary() {
    }
    
    public boolean find(String searchStr) {
        String[] searchArray = searchStr.split(" ");
        for(String str: searchArray){
            if(!Pattern.compile(Pattern.quote(str), Pattern.CASE_INSENSITIVE).matcher(this.toString()).find())
            if(!this.toString().contains(str)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkflowSummary{" + "workflowId=" + workflowId + ", workflowName=" + workflowName + ", description=" + description + ", status=" + status + ", keywords=" + keywords + ", domain=" + domain + ", application=" + application + ", owner=" + owner + ", group=" + group + ", inputs=" + inputs + ", outputs=" + outputs + ", confs=" + confs + ", to=" + to + ", imps=" + imps + '}';
    }

    public Date getCreated() {
        return created;
    }

    public String getCreatedShort() {
        if(created==null) {
            return "not available";
        }
        Locale locUK = new Locale ("uk", "UK");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locUK);
        return df.format(created);
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }
    
    public Date getUpdated() {
        return updated;
    }

    public String getUpdatedShort() {
        if(updated==null) {
            return "not available";
        }
        Locale locUK = new Locale ("uk", "UK");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locUK);    
        return df.format(updated);
    }       
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getPortName(String portId){
        for(PortRTO port: inputs){
            if(port.getName().equals(portId)){
                return port.getValue();
            }
        }
        for(PortRTO port: outputs){
            if(port.getName().equals(portId)){
                return port.getValue();
            }
        }
        return portId;
    }
    
    public String getPortDescription(String portId){
        for(PortRTO port: inputs){
            if(port.getName().equals(portId)){
                return port.getDescription();
            }
        }
        for(PortRTO port: outputs){
            if(port.getName().equals(portId)){
                return port.getDescription();
            }
        }
        return "";
    }

    public boolean isStringRemote(String str){
        return str.startsWith("http");
    }    
    
    public boolean isDescriptionRemote(){
        return description.startsWith("http");
    }
    
    public boolean isPortInput(String portId){
        for(PortRTO port: inputs){
            if(port.getName().equals(portId)){
                return true;
            }
        }
        return false;
    }
    
    
    public ApplicationTO getTo() {
        return to;
    }

    public void setTo(ApplicationTO to) {
        this.to = to;
    }


    
    public boolean hasDomain() {
        return !domain.isEmpty();
    }    
    
    public boolean hasInputs() {
        return !inputs.isEmpty();
    }

    public boolean hasOutputs() {
        return !outputs.isEmpty();
    }
    
    public boolean hasConfs() {
        return !confs.isEmpty();
    }    
    
    public boolean hasMultipleImps() {
        return imps.size()>1;
    }

    public boolean hasMultipleConfs() {
        return confs.size()>1;
    }    
    
    public boolean hasImps() {
        return imps.size()>0;
    }
    
    

    public List<ConfigurationRTO> getConfs() {
        return confs;
    }
    
    public void setConfs(List<ConfigurationRTO> confs) {
        this.confs = confs;
    }
    
    public List<PortRTO> getInputs() {
        return inputs;
    }

    public void setInputs(List<PortRTO> inputs) {
        this.inputs = inputs;
    }

    public List<PortRTO> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<PortRTO> outputs) {
        this.outputs = outputs;
    }
    
    public String getApplication() {
        return application;
    }

    public List<ImplementationSummary> getImps() {
        return imps;
    }

    public void setImps(List<ImplementationSummary> imps) {
        this.imps = imps;
    }
    
    public void setApplication(String application) {
        this.application = application;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }

    public String[] getDescriptionArray() {
        return description.split(" ");
    }    
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getLinkAppDesc(){
        return linkAppDesc;
    }
    
    public void setLinkAppDesc(String linkAppDesc){
        this.linkAppDesc = linkAppDesc;
    }
    
    /**
     * @return the subdomain
     */
    public String getSubdomain() {
        return subdomain;
    }

    /**
     * @param subdomain the subdomain to set
     */
    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }
}
