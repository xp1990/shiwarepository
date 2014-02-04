/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlTransient;
import org.shiwa.repository.toolkit.wfengine.WEImplementation;
import org.shiwa.repository.toolkit.wfengine.WEUploadedFile;

/**
 *
 * @author zsolt
 */
@Entity
@Table(name = "platform")
@NamedQueries({
    @NamedQuery(name = "Platform.findAll", query = "SELECT p FROM Platform p"),
    @NamedQuery(name = "Platform.findDistinctNames", query = "SELECT DISTINCT p.name FROM Platform p"),
    @NamedQuery(name = "Platform.findByName", query = "SELECT p FROM Platform p WHERE p.name = :name"),
    @NamedQuery(name = "Platform.findByNameAndVersion", query = "SELECT p FROM Platform p WHERE p.name = :name AND p.version = :version"),
    @NamedQuery(name = "Platform.findById", query = "SELECT p FROM Platform p WHERE p.id = :platformId"),
    @NamedQuery(name = "Platform.filterNames", query = "SELECT p.name FROM Platform p WHERE p.name LIKE :name ORDER BY p.name ASC"),
    @NamedQuery(name = "Platform.loadAllPlatforms", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.PlatformTO(p.id, p.name, p.version, p.description) FROM Platform p"),
    @NamedQuery(name = "Platform.listPlatformsByName", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.PlatformTO(p.id, p.name, p.version, p.description) FROM Platform p WHERE p.name = :name")
})
@TableGenerator(name="plat_gen", table="generator", pkColumnName="name", pkColumnValue="plat_gen", valueColumnName="value")
public class Platform implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="plat_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "name") private String name;
    @Basic(optional = true) @Column(name = "version") private String version;

    @Basic(optional = false) @Column(name = "description") private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "platform") private Set<Implementation> implementations;

    @JoinTable(name = "workflow_engine_files", joinColumns = {
        @JoinColumn(name = "idWE", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "idWEFile", referencedColumnName = "idWEFile")})
    @ManyToMany(cascade = CascadeType.REMOVE)
    private Collection<WEUploadedFile> uploadedFileCollection;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "idWE", orphanRemoval = true)
    private Collection<WEImplementation> weImplementationCollection;
    @Column(name = "submittable") private boolean submittable;

    public Platform() {
    }

    public Platform(String name, String description) {
        this.name = name;
        this.description = description;
        this.submittable = false;
    }

    public Platform(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.submittable = false;
    }

    public boolean isSubmittable() {
        return submittable;
    }

    public void setSubmittable(boolean submittable) {
        this.submittable = submittable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Implementation> getImplementations() {
        return implementations;
    }

    public String getShortDescription(){
        return (description.substring(0, Math.min(description.length(), 57))+((description.length()>57)?"...":""));
    }

     @XmlTransient
    public Collection<WEUploadedFile> getUploadedFileCollection() {
        return uploadedFileCollection;
    }

    public void setUploadedFileCollection(Collection<WEUploadedFile> uploadedFileCollection) {
        this.uploadedFileCollection = uploadedFileCollection;
    }

    @XmlTransient
    public Collection<WEImplementation> getWeImplementationCollection() {
        return weImplementationCollection;
    }

    public void setWeImplementationCollection(Collection<WEImplementation> weImplementationCollection) {
        this.weImplementationCollection = weImplementationCollection;
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
        if (!(object instanceof Platform)) {
            return false;
        }
        Platform other = (Platform) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.Platform[id=" + id + "]";
    }

}
