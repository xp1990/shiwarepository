/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

/**
 *
 * @author kukla
 */
public class DependencyRTO {
    String name;
    String type;
    String title;
    String description;

    public DependencyRTO() {
    }

    public DependencyRTO(String name, String type, String description, String title) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DependencyRTO{" + "name=" + name + ", type=" + type + ", title=" + title + ", description=" + description + '}';
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
