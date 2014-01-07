/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author kukla
 */
public class ImplementationRTO {
    int id;
    int workflowId;
    String version;
    int engineId;
    public enum Status{
        NEW("private"),
        READY("private"),
        FAILED("private"),
        PUBLIC("public"),
        OLD("private"),
        DEPRECATED("private"),
        COMPROMISED("private");

        private static List<String> statuses;
        private String friendlyName;

        static{
            statuses = Arrays.asList("private", "public");
        }

        private Status(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public String getFriendlyName(){
            return friendlyName;
        }

        public static List<String> getPossibleStatuses(){
            return statuses;
        }

        public static Status fromString(String text) {
            if (text != null) {
                for (Status s : Status.values()) {
                    if (text.equalsIgnoreCase(s.friendlyName)) {
                        return s;
                    }
                }
            }
            return null;
        }
    }
    Status state;
    Timestamp created;
    Timestamp modified;
    String title;
    String uuid;
    String description;
    String definition;
    String graph;
    String language;
    String rights;
    String licence;
    String keywords;
    List<DependencyRTO> dependencies;
    List<ConfigurationRTO> configurations;
    List<BundleFileRTO> files;

    public ImplementationRTO(int id, int workflowId, String version, int engineId, Status state, Timestamp created, Timestamp modified, String title, String uuid, String description, String definition, String graph, String language, String rights, String licence, String keywords, List<DependencyRTO> dependencies, List<ConfigurationRTO> configurations, List<BundleFileRTO> files) {
        this.id = id;
        this.workflowId = workflowId;
        this.version = version;
        this.engineId = engineId;
        this.state = state;
        this.created = created;
        this.modified = modified;
        this.title = title;
        this.uuid = uuid;
        this.description = description;
        this.definition = definition;
        this.graph = graph;
        this.language = language;
        this.rights = rights;
        this.licence = licence;
        this.keywords = keywords;
        this.dependencies = dependencies;
        this.configurations = configurations;
        this.files = files;
    }

    public ImplementationRTO() {
    }

    @Override
    public String toString() {
        return "Implementation{" + "id=" + id + ", workflowId=" + workflowId + ", version=" + version + ", engineId=" + engineId + ", state=" + state + ", created=" + created + ", modified=" + modified + ", title=" + title + ", uuid=" + uuid + ", description=" + description + ", definition=" + definition + ", graph=" + graph + ", language=" + language + ", rights=" + rights + ", licence=" + licence + ", keywords=" + keywords + ", dependencies=" + dependencies + ", configurations=" + configurations + ", files=" + files + '}';
    }

    public List<ConfigurationRTO> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<ConfigurationRTO> configurations) {
        this.configurations = configurations;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<DependencyRTO> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<DependencyRTO> dependencies) {
        this.dependencies = dependencies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEngineId() {
        return engineId;
    }

    public void setEngineId(int engineId) {
        this.engineId = engineId;
    }

    public List<BundleFileRTO> getFiles() {
        if (files == null) files = new ArrayList<BundleFileRTO>();
        return files;
    }

    public void setFiles(List<BundleFileRTO> files) {
        this.files = files;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public Status getState() {
        return state;
    }

    public void setState(Status state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

}
