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
public class NewGroupBean implements Serializable {

    String name = "";
    String leaderName = "";

    public NewGroupBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public void clear(){
        this.name = "";
        this.leaderName = "";
    }

}
