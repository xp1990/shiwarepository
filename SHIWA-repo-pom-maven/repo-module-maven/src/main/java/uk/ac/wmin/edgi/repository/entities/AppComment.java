/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author zsolt
 */
@Entity
@Table(name = "app_comment")
@NamedQueries({
    @NamedQuery(name = "AppComment.loadCommentsOfApplication", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.CommentTO(c.id, c.application.id, c.created, c.user.loginName, c.message) FROM AppComment c WHERE c.application.id = :appId")
})
@TableGenerator(name="app_com_gen", table="generator", pkColumnName="name", pkColumnValue="app_com_gen", valueColumnName="value")
public class AppComment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="app_com_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "created") @Temporal(TemporalType.TIMESTAMP) private Date created;
    @Basic(optional = false) @Column(name = "message") private String message;
    @JoinColumn(name = "user_id", referencedColumnName = "id") @ManyToOne(optional = false) private User user;
    @JoinColumn(name = "app_id", referencedColumnName = "id") @ManyToOne(optional = false) private Application application;

    public AppComment() {
    }

    public AppComment(String message, User user) {
        this.created = new Date();
        this.message = message;
        this.user = user;
    }

    public AppComment(Application application, User user, String message) {
        this.created = new Date();
        this.message = message;
        this.user = user;
        this.application = application;
    }

    public Integer getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date when) {
        this.created = when;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AppComment)) {
            return false;
        }
        AppComment other = (AppComment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.AppComment[id=" + id + "]";
    }

}
