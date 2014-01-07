/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author kukla
 */
public class WorkflowRTO {
    int id;
    String name;
    String uuid;
    AccessRightsRTO accessRights;
    Timestamp modified;
    Timestamp created;
    String application;
    String description;
    String domain;
    String keywords;
    SignatureRTO signature;
    List<ConfigurationRTO> configurations = new ArrayList<ConfigurationRTO>();
    List<BundleFileRTO> files = new ArrayList<BundleFileRTO>();
    
    public WorkflowRTO(String name, AccessRightsRTO accessRights, Timestamp created, Timestamp modified, String application, String description, String domain, String keywords, SignatureRTO signature, List<ConfigurationRTO> configurations, List<BundleFileRTO> files) {
        this.name = name;
        this.accessRights = accessRights;
        this.modified = modified;
        this.created = created;
        this.application = application;
        this.description = description;
        this.domain = domain;
        this.keywords = keywords;
        this.signature = signature;
        this.configurations = configurations;
        this.files = files;
    }    

    public WorkflowRTO(int id, String name, AccessRightsRTO accessRights, Timestamp created, Timestamp modified, String application, String description, String domain, String keywords, SignatureRTO signature, List<ConfigurationRTO> configurations, List<BundleFileRTO> files) {
        this.id = id;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.accessRights = accessRights;
        this.modified = modified;
        this.created = created;
        this.application = application;
        this.description = description;
        this.domain = domain;
        this.keywords = keywords;
        this.signature = signature;
        this.configurations = configurations;
        this.files = files;
    }

    public WorkflowRTO() {
    }

    @Override
    public String toString() {
        return "Workflow{" + "id=" + id + ", name=" + name + ", accessRights=" + accessRights + ", modified=" + modified + ", created=" + created + ", application=" + application + ", description=" + description + ", domain=" + domain + ", keywords=" + keywords + ", signature=" + signature + ", configurations=" + configurations + ", files=" + files + '}';
    }

    public String getUuid() {
        return uuid;
    }

    public AccessRightsRTO getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(AccessRightsRTO accessRights) {
        this.accessRights = accessRights;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<BundleFileRTO> getFiles() {
        return files;
    }

    public void setFiles(List<BundleFileRTO> files) {
        this.files = files;
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

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SignatureRTO getSignature() {
        return signature;
    }

    public void setSignature(SignatureRTO signature) {
        this.signature = signature;
    }

}
