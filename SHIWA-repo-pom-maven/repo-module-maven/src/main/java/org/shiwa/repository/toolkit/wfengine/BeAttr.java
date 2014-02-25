/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edward
 */
@Entity
@Table(name = "be_attr")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BeAttr.findAll", query = "SELECT b FROM BeAttr b"),
    @NamedQuery(name = "BeAttr.findById", query = "SELECT b FROM BeAttr b WHERE b.id = :id"),
    @NamedQuery(name = "BeAttr.findByName", query = "SELECT b FROM BeAttr b WHERE b.name = :name"),
    @NamedQuery(name = "BeAttr.findByAttrValue", query = "SELECT b FROM BeAttr b WHERE b.attrValue = :attrValue")})
public class BeAttr implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "attr_value")
    private String attrValue;
    @JoinColumn(name = "be_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private BeInstance beId;

    public BeAttr() {
    }

    public BeAttr(Integer id) {
        this.id = id;
    }

    public BeAttr(Integer id, String name, String attrValue) {
        this.id = id;
        this.name = name;
        this.attrValue = attrValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public BeInstance getBeId() {
        return beId;
    }

    public void setBeId(BeInstance beId) {
        this.beId = beId;
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
        if (!(object instanceof BeAttr)) {
            return false;
        }
        BeAttr other = (BeAttr) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.BeAttr[ id=" + id + " ]";
    }

}
