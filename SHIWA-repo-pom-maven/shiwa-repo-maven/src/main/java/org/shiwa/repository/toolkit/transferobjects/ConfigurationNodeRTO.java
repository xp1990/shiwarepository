/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

/**
 *
 * @author kukla
 */
public class ConfigurationNodeRTO {
    String subjectId;
    String value;
    String type;

    public ConfigurationNodeRTO() {
    }

    public ConfigurationNodeRTO(String subjectId, String value, String type) {
        this.subjectId = subjectId;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ConfigurationNode{" + "subjectId=" + subjectId + ", value=" + value + '}';
    }
    
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
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