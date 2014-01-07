/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

/**
 *
 * @author kukla
 */
public class PortRTO {
    String name;
    String type;
    String description;
    String value;
    boolean remote;

    public PortRTO(String name, String type, String description, String value, boolean remote) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.value = value;
        this.remote = remote;
    }

    public PortRTO() {
    }

    @Override
    public String toString() {
        return "Port{" + "name=" + name + ", type=" + type + ", description=" + description + ", value=" + value + ", remote=" + remote + '}';
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
