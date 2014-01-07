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
@Table(name = "imp_comment")
@NamedQueries({
    @NamedQuery(name = "ImpComment.loadCommentsOfImplementation", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.CommentTO(c.id, c.implementation.id, c.created, c.user.loginName, c.message) FROM ImpComment c WHERE c.implementation.id = :impId")
})
@TableGenerator(name="imp_com_gen", table="generator", pkColumnName="name", pkColumnValue="imp_com_gen", valueColumnName="value")
public class ImpComment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="imp_com_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "created") @Temporal(TemporalType.TIMESTAMP) private Date created;
    @Basic(optional = false) @Column(name = "message") private String message;
    @JoinColumn(name = "imp_id", referencedColumnName = "id") @ManyToOne(optional = false) private Implementation implementation;
    @JoinColumn(name = "user_id", referencedColumnName = "id") @ManyToOne(optional = false) private User user;

    public ImpComment() {
    }

    public ImpComment(Date when, String message) {
        this.created = when;
        this.message = message;
    }

    public ImpComment(Implementation implementation, User user, String message) {
        this.created = new Date();
        this.message = message;
        this.implementation = implementation;
        this.user = user;
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

    public Implementation getImplementation() {
        return implementation;
    }

    public void setImplementation(Implementation implementation) {
        this.implementation = implementation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        if (!(object instanceof ImpComment)) {
            return false;
        }
        ImpComment other = (ImpComment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.ImpComment[id=" + id + "]";
    }

}
