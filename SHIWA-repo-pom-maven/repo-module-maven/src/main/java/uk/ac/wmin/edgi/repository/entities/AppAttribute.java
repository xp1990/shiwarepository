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
@Table(name = "app_attribute")
@NamedQueries({
    @NamedQuery(name = "AppAttribute.findByAppIDAndName", query = "SELECT a FROM AppAttribute a WHERE a.application.id = :appId AND a.name = :name"),
    @NamedQuery(name = "AppAttribute.listByAppIDs", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttrListItemTO(a.application.id, a.name, a.value) FROM AppAttribute a WHERE a.application.id IN :appIDs"),
    @NamedQuery(name = "AppAttribute.listByAppIDsNames", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttrListItemTO(a.application.id, a.name, a.value) FROM AppAttribute a WHERE a.application.id IN :appIDs and a.name IN :attrNames"),
    @NamedQuery(name = "AppAttribute.loadAttributesOfApplication", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.application.id, a.name, a.value) FROM AppAttribute a WHERE a.application.id = :appId"),
    @NamedQuery(name = "AppAttribute.loadAttributesOfApplicationAndByName", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.application.id, a.name, a.value) FROM AppAttribute a WHERE a.application.id = :appId AND a.name = :attr_name"),
    @NamedQuery(name = "AppAttribute.listAttributesByKey", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.application.id, a.name, a.value) FROM AppAttribute a WHERE a.name LIKE :attrKey ORDER BY a.value"),
    @NamedQuery(name = "AppAttribute.listAttributesByKeyAndFilter", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(a.id, a.application.id, a.name, a.value) FROM AppAttribute a WHERE a.name LIKE :attrKey AND a.value LIKE :filter ORDER BY a.value"),
    @NamedQuery(name = "AppAttribute.deleteAllByAppID", query = "DELETE FROM AppAttribute a WHERE a.application.id = :appId"),
    @NamedQuery(name = "AppAttribute.filterNames", query = "SELECT DISTINCT a.name FROM AppAttribute a WHERE a.name LIKE :name ORDER BY a.name ASC")
})
@TableGenerator(name="app_attr_gen", table="generator", pkColumnName="name", pkColumnValue="app_attr_gen", valueColumnName="value")
public class AppAttribute implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="app_attr_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "name") private String name;
    @Basic(optional = false) @Column(name = "attr_value") private String value;
    @JoinColumn(name = "app_id", referencedColumnName = "id") @ManyToOne(optional = false) private Application application;

    public AppAttribute() {
    }

    public AppAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public AppAttribute(Application application, String name, String value) {
        this.name = name;
        this.value = value;
        this.application = application;
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
        if (!(object instanceof AppAttribute)) {
            return false;
        }
        AppAttribute other = (AppAttribute) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.AppAttribute[id=" + id + "]";
    }

}
