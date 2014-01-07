/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author kukla
 */
public class ConfigurationRTO {
    int id;
    List<ConfigurationNodeRTO> configurationNodes = new ArrayList<ConfigurationNodeRTO>();
    public enum ConfigurationType {PORT, DEPENDENCY};
    ConfigurationType configurationType;
    List<BundleFileRTO> files = new ArrayList<BundleFileRTO>();
    String description, uuid;

    public ConfigurationRTO() {
    }

    public ConfigurationRTO(int id) {
        this.id = id;
    }    
    
    public ConfigurationRTO(int id, ConfigurationType configurationType) {
        this.id = id;
        this.configurationType = configurationType;
    }

    public ConfigurationRTO(int id, List<ConfigurationNodeRTO> configurationNodes, ConfigurationType configurationType, List<BundleFileRTO> files, String description) {
        this.id = id;
        this.uuid = UUID.randomUUID().toString();
        this.configurationNodes = configurationNodes;
        this.configurationType = configurationType;
        this.files = files;
        this.description = description;
    }
    
    public ConfigurationRTO(List<ConfigurationNodeRTO> configurationNodes, ConfigurationType configurationType, List<BundleFileRTO> files, String description) {
        this.uuid = UUID.randomUUID().toString();
        this.configurationNodes = configurationNodes;
        this.configurationType = configurationType;
        this.files = files;
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Configuration{" + "id=" + id + ", configurationNodes=" + configurationNodes + ", configurationType=" + configurationType + ", files=" + files + '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<ConfigurationNodeRTO> getConfigurationNodes() {
        return configurationNodes;
    }

    public void setConfigurationNodes(List<ConfigurationNodeRTO> configurationNodes) {
        this.configurationNodes = configurationNodes;
    }

    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(ConfigurationType configurationType) {
        this.configurationType = configurationType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}