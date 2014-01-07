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
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import uk.ac.wmin.edgi.repository.entities.Platform;

/**
 *
 * @author xp
 */
@Entity
@Table(name = "uploaded_file")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WEUploadedFile.findAll", query = "SELECT u FROM WEUploadedFile u"),
    @NamedQuery(name = "WEUploadedFile.findByIdWEFile", query = "SELECT u FROM WEUploadedFile u WHERE u.idWEFile = :idWEFile"),
    @NamedQuery(name = "WEUploadedFile.findByName", query = "SELECT u FROM WEUploadedFile u WHERE u.name = :name"),
    @NamedQuery(name = "WEUploadedFile.findByFilePath", query = "SELECT u FROM WEUploadedFile u WHERE u.filePath = :filePath")})    
public class WEUploadedFile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idWEFile")
    private Integer idWEFile;
    @Size(max = 300)
    @Column(name = "name")
    private String name;
    @Size(max = 300)
    @Column(name = "filePath")
    private String filePath;
    @Size(max = 5000)
    @Column(name = "description")
    private String description;
    @Column(name = "isData")
    private boolean isData;
    @ManyToMany(mappedBy = "uploadedFileCollection")
    private Collection<Platform> workflowEngineCollection;
    @OneToMany(mappedBy = "zipWEFileId", cascade = CascadeType.REMOVE)
    private Collection<WEImplementation> weImplementationCollection;

    public WEUploadedFile() {
    }
    
    /*
     * Add a factory instead of a constructor!!
     */
    public WEUploadedFile(String name, String description, String filePath, Collection<Platform> temp, boolean isData) {
        this.name = name;
        this.filePath = filePath;
        this.description = description;        
        this.isData = isData;
        setWorkflowEngineCollection(temp);
    }
    
    public WEUploadedFile(String name, String version, String description, String filePath, boolean isData){
        this.name = name;
        this.filePath = filePath;
        this.description = description;        
        this.isData = isData;
    }
    
    public WEUploadedFile(String filePath, Collection<Platform> temp)
    {
        setWorkflowEngineCollection(temp);
        this.filePath = filePath;
    }

    public WEUploadedFile(Integer idWEFile) {
        this.idWEFile = idWEFile;
    }

    public Integer getIdWEFile() {
        return idWEFile;
    }

    public void setIdWEFile(Integer idWEFile) {
        this.idWEFile = idWEFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean getIsData() {
        return isData;
    }

    public void setIsData(boolean isData) {
        this.isData = isData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public Collection<Platform> getWorkflowEngineCollection() {
        return workflowEngineCollection;
    }

    public void setWorkflowEngineCollection(Collection<Platform> workflowEngineCollection) {
        this.workflowEngineCollection = workflowEngineCollection;
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
        hash += (idWEFile != null ? idWEFile.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WEUploadedFile)) {
            return false;
        }
        WEUploadedFile other = (WEUploadedFile) object;
        if ((this.idWEFile == null && other.idWEFile != null) || (this.idWEFile != null && !this.idWEFile.equals(other.idWEFile))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.UploadedFile[ idWEFile=" + idWEFile + " ]";
    }
    
}
