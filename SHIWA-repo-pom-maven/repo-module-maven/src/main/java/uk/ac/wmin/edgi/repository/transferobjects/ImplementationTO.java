/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import uk.ac.wmin.edgi.repository.common.ImplementationStatus;
import uk.ac.wmin.edgi.repository.entities.Implementation;

/**
 *
 * @author zsolt
 */
public class ImplementationTO implements Serializable {

    public static final String queryPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO(o.id, o.application.id, o.platform.name, o.version) FROM Implementation o ";
    public static final String countPrefix = "SELECT COUNT(o) FROM Implementation o ";
    private static final String[][] filterFields = {
        {"appId", "o.application.id = :appId "},
        {"version", "o.version LIKE :version "},
        {"platformName", "o.platform.name LIKE :platformName "}
    };
    private static final String[][] sortFields = {
        {null, "o.version "}, //default sort field
        {"version", "o.version "},
        {"platformName", "o.platform.name"}
    };

    public static final Map<String, String> filterMap;
    public static final Map<String, String> sortMap;

    static{
        filterMap = new HashMap<String, String>(filterFields.length);
        for(String[] s: filterFields){
            filterMap.put(s[0], s[1]);
        }
        sortMap = new HashMap<String, String>(sortFields.length);
        for(String[] s: sortFields){
            sortMap.put(s[0], s[1]);
        }
    }

    private Integer id;
    private Integer appId;
    private String appName;
    private String platformName;
    private String platformVersion;
    private String version;
    private ImplementationStatus status;
    private Date created;
    private Date updated;
    private boolean submittable;

    public ImplementationTO() {
    }

    public ImplementationTO(Implementation imp){
        this.id = imp.getId();
        this.appId = imp.getApplication().getId();
        this.appName = imp.getApplication().getName();
        this.platformName = imp.getPlatform().getName();
        this.platformVersion = imp.getPlatform().getVersion();
        this.version = imp.getVersion();
        this.status = imp.getStatus();
        this.created = imp.getCreated();
        this.updated = imp.getUpdated();
        this.submittable = imp.isSubmittable();
    }

    public boolean isSubmittable() {
        return submittable;
    }

    public void setSubmittable(boolean submittable) {
        this.submittable = submittable;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }


    /*
    public ImplementationTO(Integer id, Integer appId, String platformName, String version) {
        this.id = id;
        this.appId = appId;
        this.platformName = platformName;
        this.version = version;
    }
    */

    public ImplementationTO(Integer id, Integer appId, String appName, String platformName, String platformVersion, String version, ImplementationStatus status) {
        this.id = id;
        this.appId = appId;
        this.platformName = platformName;
        this.platformVersion = platformVersion;
        this.version = version;
        this.appName = appName;
        this.status = status;
    }

    public ImplementationTO(Integer id, Integer appId, String appName, String platformName, String platformVersion, String version, ImplementationStatus status, Date created, Date updated, boolean _submittable) {
        this.id = id;
        this.appId = appId;
        this.appName = appName;
        this.platformName = platformName;
        this.platformVersion = platformVersion;
        this.version = version;
        this.status = status;
        this.created = created;
        this.updated = updated;
        this.submittable = _submittable;
    }



    public Integer getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }

    public Integer getId() {
        return id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getVersion() {
        return version;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ImplementationStatus getStatus() {
        return status;
    }

    public void setStatus(ImplementationStatus status) {
        this.status = status;
    }

    public String getStatusFriendlyName(){
        return status.getFriendlyName();
    }

    public boolean getNew(){
        return status.equals(ImplementationStatus.NEW);
    }

    public boolean getReadyForValidation(){
        return status.equals(ImplementationStatus.READY);
    }

    public boolean getPublic(){
        return status.equals(ImplementationStatus.PUBLIC);
    }

    public boolean getFailed(){
        return status.equals(ImplementationStatus.FAILED);
    }

    public boolean getOld(){
        return status.equals(ImplementationStatus.OLD);
    }

    public boolean getDeprecated(){
        return status.equals(ImplementationStatus.DEPRECATED);
    }

    public boolean getCompromised(){
        return status.equals(ImplementationStatus.COMPROMISED);
    }


    /*
     * TODO: Replace with code that validates execution node for use
     * by the submission service.
     */
    public boolean isPublic(){
        return this.getStatusFriendlyName().equalsIgnoreCase("public");
    }


    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }
}
