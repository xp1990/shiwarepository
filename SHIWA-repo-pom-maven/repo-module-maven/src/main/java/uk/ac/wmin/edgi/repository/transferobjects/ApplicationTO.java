/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import uk.ac.wmin.edgi.repository.entities.Application;

/**
 *
 * @author zsolt
 */
public class ApplicationTO implements Serializable{

    public static final String queryPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO(o.id, o.name, o.owner.loginName, o.group.name, o.description, o.groupRead, o.othersRead, o.groupDownload, o.othersDownload, o.groupModify, o.published, o.views, o.linkAppDesc) FROM Application o ";
    public static final String countPrefix = "SELECT COUNT(o) FROM Application o ";
    private static final String[][] filterFields = {
        {"name", "o.name LIKE :name "},
        {"groupName", "o.group.name LIKE :groupName "},
        {"ownerLoginName", "o.owner.loginName LIKE :ownerLoginName "},
        {"shortDescription", "o.description LIKE :shortDescription "}
    };
    private static final String[][] sortFields = {
        {null, "o.name "}, //default sort field
        {"name", "o.name "}
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
    private String name;
    private String ownerLoginName;
    private String groupName;
    private String description;
    private Boolean groupRead;
    private Boolean othersRead;
    private Boolean groupDownload;
    private Boolean othersDownload;
    private Boolean groupModify;
    private Boolean published;
    private Date created;
    private Date updated;
    private Integer views;
    private String linkAppDesc;
    
    public void setName(String name) {
        this.name = name;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
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

    public String getLinkAppDesc(){
        return linkAppDesc;
    }
    
    public void setLinkAppDesc(String linkAppDesc){
        this.linkAppDesc = linkAppDesc;
    }

    public ApplicationTO() {
    }

    public ApplicationTO(Integer id, String name, String ownerLoginName, String groupName, String description, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published, int views, String linkAppDesc) {
        this.id = id;
        this.name = name;
        this.ownerLoginName = ownerLoginName;
        this.groupName = groupName;
        this.description = description;
        this.groupRead = groupRead;
        this.othersRead = othersRead;
        this.groupDownload = groupDownload;
        this.othersDownload = othersDownload;
        this.groupModify = groupModify;
        this.published = published;
        this.views = views;
        this.linkAppDesc = linkAppDesc;
    }

    public ApplicationTO(Integer id, String name, String ownerLoginName, String groupName, String description, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published, Date created, Date updated, int views, String linkAppDesc) {
        this.id = id;
        this.name = name;
        this.ownerLoginName = ownerLoginName;
        this.groupName = groupName;
        this.description = description;
        this.groupRead = groupRead;
        this.othersRead = othersRead;
        this.groupDownload = groupDownload;
        this.othersDownload = othersDownload;
        this.groupModify = groupModify;
        this.published = published;
        this.created = created;
        this.updated = updated;
        this.views = views;
        this.linkAppDesc = linkAppDesc;
    }



    public ApplicationTO(Application app){
        this.id = app.getId();
        this.name = app.getName();
        this.ownerLoginName = app.getOwner().getLoginName();
        this.groupName = app.getGroup().getName();
        this.description = app.getDescription();
        this.groupRead = app.getGroupRead();
        this.othersRead = app.getOthersRead();
        this.groupDownload = app.getGroupDownload();
        this.othersDownload = app.getOthersDownload();
        this.groupModify = app.getGroupModify();
        this.published = app.getPublished();
        this.created = app.getCreated();
        this.updated = app.getUpdated();
        this.views = app.getViews();
        this.linkAppDesc = app.getLinkAppDesc();
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        if(description == null || description.length() <= 40){
            return description;
        }else{
            return (description.substring(0, 37) + "...");
        }
    }

    public Boolean getGroupDownload() {
        return groupDownload;
    }

    public Boolean getGroupModify() {
        return groupModify;
    }

    public String getGroupName() {
        return groupName;
    }

    public Boolean getGroupRead() {
        return groupRead;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getOthersDownload() {
        return othersDownload;
    }

    public Boolean getOthersRead() {
        return othersRead;
    }

    public String getOwnerLoginName() {
        return ownerLoginName;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroupDownload(Boolean groupDownload) {
        this.groupDownload = groupDownload;
    }

    public void setGroupModify(Boolean groupModify) {
        this.groupModify = groupModify;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupRead(Boolean groupRead) {
        this.groupRead = groupRead;
    }

    public void setOthersDownload(Boolean othersDownload) {
        this.othersDownload = othersDownload;
    }

    public void setOthersRead(Boolean othersRead) {
        this.othersRead = othersRead;
    }

    public void setOwnerLoginName(String ownerLoginName) {
        this.ownerLoginName = ownerLoginName;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

}
