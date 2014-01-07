/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.ui;

import java.io.Serializable;

/**
 *
 * @author zsolt
 */
public class NewPlatformBean implements Serializable {

    String name = "";
    String description = "";
    String version = "";

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public NewPlatformBean() {
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

    public void clear(){
        this.name = "";
        this.description = "";
    }

}
