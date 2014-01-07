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
 * @author zsolt
 */
@Entity
@Table(name = "imp_attribute")
@NamedQueries({
    @NamedQuery(name = "ImpAttribute.findByImpIDAndName", query = "SELECT a FROM ImpAttribute a WHERE a.implementation.id = :impId AND a.name like :name ORDER BY a.name"),
    @NamedQuery(name = "ImpAttribute.listByImpIDs", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttrListItemTO(a.implementation.id, a.name, a.value) FROM ImpAttribute a WHERE a.implementation.id IN :impIDs"),
    @NamedQuery(name = "ImpAttribute.listByImpIDsNames", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttrListItemTO(a.implementation.id, a.name, a.value) FROM ImpAttribute a WHERE a.implementation.id IN :impIDs and a.name IN :attrNames"),
    @NamedQuery(name = "ImpAttributes.loadAttributesOfImplementation", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.implementation.id, a.name, a.value) FROM ImpAttribute a WHERE a.implementation.id = :impId"),
    @NamedQuery(name = "ImpAttributes.loadLikeAttributesOfImplementationByName", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.implementation.id, a.name, a.value) FROM ImpAttribute a WHERE a.implementation.id = :impId and a.name LIKE :attrName"),
    @NamedQuery(name = "ImpAttributes.loadAttributesByKeyAndFilter", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.implementation.id, a.name, a.value) FROM ImpAttribute a WHERE a.name LIKE :attrKey AND a.value LIKE :filter ORDER BY a.value"),
    @NamedQuery(name = "ImpAttribute.loadAttributesByKey", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.implementation.id, a.name, a.value) FROM ImpAttribute a WHERE a.name = :attrKey ORDER BY a.value"),
    @NamedQuery(name = "ImpAttribute.deleteAllByImpID", query = "DELETE FROM ImpAttribute i WHERE i.implementation.id = :impId"),
    @NamedQuery(name = "ImpAttribute.filterNames", query = "SELECT DISTINCT a.name FROM ImpAttribute a WHERE a.name LIKE :name ORDER BY a.name ASC")
})
@TableGenerator(name="imp_attr_gen", table="generator", pkColumnName="name", pkColumnValue="imp_attr_gen", valueColumnName="value")
public class ImpAttribute implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="imp_attr_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "name") private String name;
    @Basic(optional = false) @Column(name = "attr_value") private String value;
    @JoinColumn(name = "imp_id", referencedColumnName = "id") @ManyToOne(optional = false) private Implementation implementation;

    public ImpAttribute() {
    }

    public ImpAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ImpAttribute(Implementation implementation, String name, String value) {
        this.name = name;
        this.value = value;
        this.implementation = implementation;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if (!(object instanceof ImpAttribute)) {
            return false;
        }
        ImpAttribute other = (ImpAttribute) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.ImpAttribute[id=" + id + "]";
    }

}
