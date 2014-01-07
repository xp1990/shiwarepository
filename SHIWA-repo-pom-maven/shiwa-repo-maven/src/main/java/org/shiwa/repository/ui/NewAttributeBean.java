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
public class NewAttributeBean implements Serializable {

    String name = "";
    String value = "";

    public NewAttributeBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void clear(){
        this.name = "";
        this.value = "";
    }

}
