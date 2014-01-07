/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

/**
 *
 * @author kukla
 */
public class AccessRightsRTO {
    int ownerId;
    int groupId;
    boolean groupRead;
    boolean groupDownload;
    boolean groupModify;
    boolean othersRead;
    boolean othersDownload;
    boolean published;

    public AccessRightsRTO(int ownerId, int groupId, boolean groupRead, boolean groupDownload, boolean groupModify, boolean othersRead, boolean othersDownload, boolean published) {
        this.ownerId = ownerId;
        this.groupId = groupId;
        this.groupRead = groupRead;
        this.groupDownload = groupDownload;
        this.groupModify = groupModify;
        this.othersRead = othersRead;
        this.othersDownload = othersDownload;
        this.published = published;
    }

    public AccessRightsRTO() {
    }

    @Override
    public String toString() {
        return "AccessRights{" + "ownerId=" + ownerId + ", groupId=" + groupId + ", groupRead=" + groupRead + ", groupDownload=" + groupDownload + ", groupModify=" + groupModify + ", othersRead=" + othersRead + ", othersDownload=" + othersDownload + ", published=" + published + '}';
    }
    
    public boolean isGroupDownload() {
        return groupDownload;
    }

    public void setGroupDownload(boolean groupDownload) {
        this.groupDownload = groupDownload;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isGroupModify() {
        return groupModify;
    }

    public void setGroupModify(boolean groupModify) {
        this.groupModify = groupModify;
    }

    public boolean isGroupRead() {
        return groupRead;
    }

    public void setGroupRead(boolean groupRead) {
        this.groupRead = groupRead;
    }

    public boolean isOthersDownload() {
        return othersDownload;
    }

    public void setOthersDownload(boolean othersDownload) {
        this.othersDownload = othersDownload;
    }

    public boolean isOthersRead() {
        return othersRead;
    }

    public void setOthersRead(boolean othersRead) {
        this.othersRead = othersRead;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
    
}
