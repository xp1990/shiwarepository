/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author xp
 */
@Entity
@Table(name = "operating_systems")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OperatingSystems.listOnlyNames", query = "SELECT DISTINCT o.name FROM OperatingSystems o"),
    @NamedQuery(name = "OperatingSystems.findAll", query = "SELECT o FROM OperatingSystems o"),
    @NamedQuery(name = "OperatingSystems.findByIdOS", query = "SELECT o FROM OperatingSystems o WHERE o.idOS = :idOS"),
    @NamedQuery(name = "OperatingSystems.findByName", query = "SELECT o FROM OperatingSystems o WHERE o.name = :name"),
    @NamedQuery(name = "OperatingSystems.findByVersion", query = "SELECT o FROM OperatingSystems o WHERE o.version = :version")})
public class OperatingSystems implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idOS")
    private Integer idOS;
    @Size(max = 50)
    @Column(name = "name")
    private String name;
    @Size(max = 10)
    @Column(name = "version")
    private String version;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idOS")
    private Collection<BeInstance> beInstanceCollection;

    public OperatingSystems() {
    }

    public OperatingSystems(String name, String version) {
        this.name = name;
        this.version = version;
    }
    
    public OperatingSystems(Integer idOS) {
        this.idOS = idOS;
    }

    public Integer getIdOS() {
        return idOS;
    }

    public void setIdOS(Integer idOS) {
        this.idOS = idOS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlTransient
    public Collection<BeInstance> getBeInstanceCollection() {
        return beInstanceCollection;
    }

    public void setBeInstanceCollection(Collection<BeInstance> beInstanceCollection) {
        this.beInstanceCollection = beInstanceCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOS != null ? idOS.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OperatingSystems)) {
            return false;
        }
        OperatingSystems other = (OperatingSystems) object;
        if ((this.idOS == null && other.idOS != null) || (this.idOS != null && !this.idOS.equals(other.idOS))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.OperatingSystems[ idOS=" + idOS + " ]";
    }
    
}
