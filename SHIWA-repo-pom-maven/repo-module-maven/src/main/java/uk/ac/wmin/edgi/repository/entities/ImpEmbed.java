/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.entities;

import java.io.Serializable;
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

/**
 *
 * @author kukla
 */
@Entity
@Table(name = "imp_embed")
@NamedQueries({
    @NamedQuery(name = "ImpEmbed.listAll", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImpEmbedTO(e.id, e.implementation.id, e.extUserId, e.extServiceId) FROM ImpEmbed e"),
    @NamedQuery(name = "ImpEmbed.listByImpId", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImpEmbedTO(e.id, e.implementation.id, e.extUserId, e.extServiceId) FROM ImpEmbed e WHERE e.id = :id"),
    @NamedQuery(name = "ImpEmbed.listAllByExtServiceId", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImpEmbedTO(e.id, e.implementation.id, e.extUserId, e.extServiceId) FROM ImpEmbed e WHERE e.extServiceId = :extServiceId"),
    @NamedQuery(name = "ImpEmbed.loadImpEmbed", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImpEmbedTO(e.id, e.implementation.id, e.extUserId, e.extServiceId) FROM ImpEmbed e WHERE e.extServiceId = :extServiceId AND e.extUserId = :extUserId AND e.implementation.id = :impId"),
    @NamedQuery(name = "ImpEmbed.listAllByExtServiceIdAndExtUserId", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.ImpEmbedTO(e.id, e.implementation.id, e.extUserId, e.extServiceId) FROM ImpEmbed e WHERE e.extServiceId = :extServiceId AND e.extUserId = :extUserId")
})
@TableGenerator(name="imp_emb_gen", table="generator", pkColumnName="name", pkColumnValue="imp_emb_gen", valueColumnName="value")
public class ImpEmbed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="imp_emb_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "ext_user_id") private String extUserId;
    @Basic(optional = false) @Column(name = "ext_service_id") private String extServiceId;
    @JoinColumn(name = "imp_id", referencedColumnName = "id") @ManyToOne(optional = false) private Implementation implementation;

    public ImpEmbed() {
    }

    public ImpEmbed(Implementation implementation, String extUserId, String extServiceId) {
        this.extUserId = extUserId;
        this.extServiceId = extServiceId;
        this.implementation = implementation;
    }

    public Integer getId() {
        return id;
    }

    public String getExtUserId() {
        return extUserId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    public String getExtServiceId() {
        return extServiceId;
    }

    public void setExtServiceId(String extServiceId) {
        this.extServiceId = extServiceId;
    }

    public Implementation getImplementation() {
        return implementation;
    }

    public void setImplementation(Implementation implementation) {
        this.implementation = implementation;
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
        if (!(object instanceof ImpEmbed)) {
            return false;
        }
        ImpEmbed other = (ImpEmbed) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.ImpEmbed[id=" + id + "]";
    }

}
