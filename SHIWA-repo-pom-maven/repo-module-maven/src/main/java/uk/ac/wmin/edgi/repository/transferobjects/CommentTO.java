/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import java.util.Date;
import uk.ac.wmin.edgi.repository.entities.AppComment;
import uk.ac.wmin.edgi.repository.entities.ImpComment;

/**
 *
 * @author zsolt
 */
public class CommentTO implements Serializable{

    Integer id;
    Integer parentId;
    Date created;
    String userLoginName;
    String message;

    public CommentTO() {
    }

    public CommentTO(AppComment comm) {
        this.id = comm.getId();
        this.parentId = comm.getUser().getId();
        this.created = comm.getCreated();
        this.userLoginName = comm.getUser().getLoginName();
        this.message = comm.getMessage();
    }

    public CommentTO(ImpComment comm) {
        this.id = comm.getId();
        this.parentId = comm.getUser().getId();
        this.created = comm.getCreated();
        this.userLoginName = comm.getUser().getLoginName();
        this.message = comm.getMessage();
    }

    public CommentTO(Integer id, Integer parentId, Date created, String poster, String message) {
        this.id = id;
        this.parentId = parentId;
        this.created = created;
        this.userLoginName = poster;
        this.message = message;
    }

    public Date getCreated() {
        return created;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Integer getParentId() {
        return parentId;
    }

    public String getPosterLoginName() {
        return userLoginName;
    }

}
