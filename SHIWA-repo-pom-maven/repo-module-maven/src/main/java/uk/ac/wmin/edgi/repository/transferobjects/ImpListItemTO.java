/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

/**
 *
 * @author zsolt
 */
public class ImpListItemTO {

    int ID;
    int appID;

    public ImpListItemTO(Integer ID, Integer appID) {
        this.ID = ID;
        this.appID = appID;
    }

    public int getID() {
        return ID;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

}
