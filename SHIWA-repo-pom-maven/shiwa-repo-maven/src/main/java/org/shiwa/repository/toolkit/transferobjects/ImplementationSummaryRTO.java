/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import org.shiwa.repository.toolkit.util.RepoUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author kukla
 */
public class ImplementationSummaryRTO {
    private int id;
    private int workflowId;
    private String version;
    private String engineName;
    private String engineVersion;
    private String title;
    private String description;
    private String language;
    private String keywords;
    private List<String> dcis;

    public ImplementationSummaryRTO() {
    }

    public ImplementationSummaryRTO(int id, int workflowId, String version, String engineName, String engineVersion, String title, String description, String keywords, List<String> dcis) {
        this.id = id;
        this.workflowId = workflowId;
        this.version = version;
        this.engineName = engineName;
        this.engineVersion = engineVersion;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.dcis = dcis;
    }

    public ImplementationSummaryRTO(ImplementationRTO implementation, EngineRTO engine) {
        this.id = implementation.getId();
        this.title=implementation.getTitle();
        this.workflowId = implementation.getWorkflowId();
        this.version = implementation.getVersion();
        this.engineName = engine.getTitle();
        this.engineVersion=engine.getVersion();
        this.description=implementation.getDescription();
        this.language=implementation.getLanguage();
        this.keywords=implementation.getKeywords();
        
        //find DCI dependencies
        List<String> voDeps = new ArrayList<String>();
        Iterator<DependencyRTO> dIter = implementation.getDependencies().iterator();
        DependencyRTO dItem;
        while(dIter.hasNext()){
            dItem=dIter.next();
            if(dItem.getType().equals("DCI")){
                voDeps.add(dItem.getName());
            }
        }
        
        //find confs that resolve these dependencies and build DCI list
        dcis = new ArrayList<String>();
        Iterator<ConfigurationRTO> cIter = implementation.getConfigurations().iterator();
        ConfigurationRTO cItem;
        Iterator<ConfigurationNodeRTO> cnIter;
        ConfigurationNodeRTO cnItem;
        while(cIter.hasNext()){
            cItem=cIter.next();
            cnIter = cItem.getConfigurationNodes().iterator();
            while(cnIter.hasNext()){
                cnItem=cnIter.next();
                if(voDeps.contains(cnItem.getSubjectId())
                        && !dcis.contains(cnItem.getValue()))
                {
                    dcis.add(cnItem.getValue());
                }
            }
        }
    }

    public ImplementationSummaryRTO(int id, int workflowId, String version, String title, String description, EngineRTO engineRTO, List<String> dcis) {
        this.id = id;
        this.workflowId = workflowId;
        this.version = version;
        this.engineName = engineRTO.getTitle();
        this.engineVersion = engineRTO.getVersion();
        this.title = title;
        this.description = description;
        this.dcis = dcis;
    }
        
    public ImplementationSummaryRTO(
            int id, 
            int workflowId, 
            String version, 
            String engineName, 
            String engineVersion, 
            String title, 
            String description, 
            String language, 
            String keywords, 
            List<String> dcis) {
        this.id = id;
        this.workflowId = workflowId;
        this.version = version;
        this.engineName = engineName;
        this.engineVersion = engineVersion;
        this.title = title;
        this.description = description;
        this.language = language;
        this.keywords = keywords;
        this.dcis = dcis;
    }
    

    @Override
    public String toString() {
        return id + RepoUtil.UNIT + title + RepoUtil.UNIT + version + RepoUtil.UNIT + engineName + " " + engineVersion + RepoUtil.UNIT + description + RepoUtil.UNIT + language + RepoUtil.UNIT + keywords + RepoUtil.UNIT + dcis;
    }

    public List<String> getDcis() {
        return dcis;
    }

    public void setDcis(List<String> dcis) {
        this.dcis = dcis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    
    
    
    
}