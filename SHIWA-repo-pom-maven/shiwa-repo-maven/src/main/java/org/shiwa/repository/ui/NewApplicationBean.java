/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.ui;

/**
 *
 * @author zsolt
 */
public class NewApplicationBean {

    private String name = "";
    private String groupName = "";
    private String description = "";

    public NewApplicationBean() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void clear(){
        this.name = "";
        this.groupName = "";
        this.description = "";
    }

}
