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
public class AttrListItemTO implements Serializable {

    int parentID;
    String name;
    String value;

    public AttrListItemTO(Integer parentID, String name, String value) {
        this.parentID = parentID;
        this.name = name;
        this.value = value;
    }

    public int getParentID() {
        return parentID;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
