/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

/**
 *
 * @author kukla
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.shiwa.repository.toolkit.transferobjects.ConfigurationRTO;
import org.shiwa.repository.toolkit.transferobjects.DependencyRTO;
import uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO;

/**
 *
 * @author kukla
 */
public final class ImplementationSummary {
    int id;
    String workflowName;

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    String version;
    String engineName;
    String engineVersion;
    Date created;
    Date updated;
    String title;
    String description;
    String keywords;
    String rights;
    String licence;
    String definition;
    String language;
    String gemlcaId;
    String status;
    List<String> dcis;
    String graph;
    boolean submittable;
    List<DependencyRTO> deps = new ArrayList<DependencyRTO>();
    List<ConfigurationRTO> confs = new ArrayList<ConfigurationRTO>();
    ImplementationTO to;
    WorkflowSummary wf;

    public ImplementationSummary() {
    }

    public ImplementationSummary(int id, String workflowName, String version, String engineName, String engineVersion, Date created, Date updated, String title, String description, String keywords, List<String> dcis, String graph, String definition, String licence, String rights, String language, String gemlcaId, String status, List<DependencyRTO> deps, List<ConfigurationRTO> confs, ImplementationTO to, WorkflowSummary wf, boolean _submittable) {
        this.id = id;
        this.workflowName = workflowName;
        this.version = version;
        this.engineName = engineName;
        this.engineVersion = engineVersion;
        this.created = created;
        this.updated = updated;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.dcis = dcis;
        this.graph = graph;
        this.confs = confs;
        this.to = to;
        this.definition = definition;
        this.licence = licence;
        this.rights = rights;
        this.language = language;
        this.deps = deps;
        this.gemlcaId = gemlcaId;
        this.wf = wf;
        this.setStatus(status);
        this.submittable = _submittable;
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

    public boolean isSubmittable() {
        return submittable;
    }

    public void setSubmittable(boolean submittable) {
        this.submittable = submittable;
    }



    @Override
    public String toString() {
        return "ImplementationSummary{" + "id=" + id + ", workflowName=" + workflowName + ", version=" + version + ", engineName=" + engineName + ", engineVersion=" + engineVersion + ", title=" + title + ", description=" + description + ", keywords=" + keywords + ", rights=" + rights + ", licence=" + licence + ", definition=" + definition + ", language=" + language + ", gemlcaId=" + gemlcaId + ", status=" + status + ", dcis=" + dcis + ", graph=" + graph + ", deps=" + deps + ", confs=" + confs + ", wf=" + wf + '}';
    }

    public String getFullName() {
        return workflowName+"-"+engineName+"("+engineVersion+")-"+version;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if(status.equals("ready")){
            this.status="submitted for validation";
        }else if(status.equals("failed")){
            this.status="validation failed";
        }else{
            this.status = status;
        }
    }

    public WorkflowSummary getWf() {
        return wf;
    }

    public void setWf(WorkflowSummary wf) {
        this.wf = wf;
    }


    public String getGemlcaId() {
        return gemlcaId;
    }

    public void setGemlcaId(String gemlcaId) {
        this.gemlcaId = gemlcaId;
    }


    public String getDepName(String depId){
        for(DependencyRTO dep: deps){
            if(dep.getName().equals(depId)){
                return dep.getTitle();
            }
        }
        return depId;
    }

    public String getDepDescription(String depId){
        for(DependencyRTO dep: deps){
            if(dep.getTitle().equals(depId)){
                return dep.getDescription();
            }
        }
        return "";
    }

    public boolean hasGemlcaId() {
        return gemlcaId!=null && !gemlcaId.isEmpty();
    }

    public boolean hasDeps() {
        return !deps.isEmpty();
    }

    public boolean hasConfs() {
        return !confs.isEmpty();
    }

    public boolean hasMultipleConfs() {
        return confs.size()>1;
    }

    public boolean hasDCIs() {
        return !dcis.isEmpty();
    }

    public boolean hasKeywords() {
        return keywords.length()>0;
    }


    public boolean hasLanguage() {
        return language.length()>0;
    }

    public boolean hasTitle() {
        return title.length()>0;
    }

    public boolean hasDescription() {
        return description.length()>0;
    }

    public boolean hasDefinition() {
        return definition.length()>0;
    }

    public boolean hasLicence() {
        return licence.length()>0;
    }

    public boolean hasRights() {
        return rights.length()>0;
    }

    public List<ConfigurationRTO> getConfs() {
        return confs;
    }

    public void setConfs(List<ConfigurationRTO> confs) {
        this.confs = confs;
    }

    public List<DependencyRTO> getDeps() {
        return deps;
    }

    public void setDeps(List<DependencyRTO> deps) {
        this.deps = deps;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public ImplementationTO getTo() {
        return to;
    }

    public void setTo(ImplementationTO to) {
        this.to = to;
    }

    public String getGraph() {
        return graph;
    }
    public boolean hasGraph(){
        return graph != null && !graph.equals("");
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getDcis() {
        String dciList = dcis.toString();
        if(dciList.length()<2){
            return dciList;
        }
        return dciList.substring(1,dciList.length()-1);
    }

    public void setDcis(List<String> dcis) {
        this.dcis = dcis;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        if(description.length()>60){
            return description.substring(0, 59)+"...";
        }else{
            return description;
        }
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

    /*
     * TODO: Replace with code that validates execution node for use
     * by the submission service.
     */
    public boolean isPublic(){
        return this.status.equalsIgnoreCase("public");
    }

}
