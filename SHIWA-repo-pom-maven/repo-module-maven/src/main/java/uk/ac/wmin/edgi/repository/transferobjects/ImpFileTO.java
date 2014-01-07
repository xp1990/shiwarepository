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
public class ImpFileTO implements Serializable {

    Integer appId;
    Integer impId;
    String pathName;

    public ImpFileTO() {
    }

    public ImpFileTO(Integer appId, Integer impId, String pathName) {
        this.appId = appId;
        this.impId = impId;
        this.pathName = pathName;
    }

    public Integer getAppId() {
        return appId;
    }

    public Integer getImpId() {
        return impId;
    }

    public String getPathName() {
        return pathName;
    }

}
