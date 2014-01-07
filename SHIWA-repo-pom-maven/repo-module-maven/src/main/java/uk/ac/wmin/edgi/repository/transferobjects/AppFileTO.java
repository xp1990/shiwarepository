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
public class AppFileTO implements Serializable {

    Integer appId;
    String pathName;

    public AppFileTO() {
    }

    public AppFileTO(Integer appId, String pathName) {
        this.appId = appId;
        this.pathName = pathName;
    }

    public Integer getAppId() {
        return appId;
    }

    public String getPathName() {
        return pathName;
    }

}
