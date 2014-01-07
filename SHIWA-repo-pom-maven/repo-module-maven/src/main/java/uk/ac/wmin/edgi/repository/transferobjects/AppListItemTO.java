/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;

/**
 *
 * @author zsolt
 */
public class AppListItemTO implements Serializable{

    private int ID;
    private String name;

    public AppListItemTO(Integer ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public AppListItemTO() {
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

}
