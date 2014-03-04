/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import java.io.Serializable;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import uk.ac.wmin.edgi.repository.entities.User;

/**
 *
 * @author xp
 */
@Entity
@Table(name = "be_instance")
@NamedQueries({
    @NamedQuery(name = "BeInstance.findAll", query = "SELECT b FROM BeInstance b"),
    @NamedQuery(name = "BeInstance.findByName", query = "SELECT b FROM BeInstance b WHERE b.name= :name")})
public class BeInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "name")
    private String name;
    @Column(name ="backend")
    private String backend;
    @JoinColumn(name = "we_dev", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User WEDev;
    @OneToMany(mappedBy = "beId")
    List<BeAttr> attributes;

    public BeInstance() {
    }

    public BeInstance(String _name, String _backend, User _wedev) {
        this.name = _name;
        this.backend = _backend;
        this.WEDev = _wedev;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<BeAttr> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<BeAttr> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public User getWEDev() {
        return WEDev;
    }

    public void setWEDev(User WEDev) {
        this.WEDev = WEDev;
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
        if (!(object instanceof BeInstance)) {
            return false;
        }
        BeInstance other = (BeInstance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.BeInstance[ id=" + id + " ]";
    }
}
