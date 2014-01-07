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

/**
 *
 * @author xp
 */
@Entity
@Table(name = "backend")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Backend.listAll", query = "SELECT b FROM Backend b"),
    @NamedQuery(name = "Backend.listOnlyNames", query = "SELECT DISTINCT b.backendName FROM Backend b"),
    @NamedQuery(name = "Backend.findAll", query = "SELECT b FROM Backend b"),
    @NamedQuery(name = "Backend.findByIdBackend", query = "SELECT b FROM Backend b WHERE b.idBackend = :idBackend"),
    @NamedQuery(name = "Backend.findByBackendName", query = "SELECT b FROM Backend b WHERE b.backendName = :backendName"),    
    @NamedQuery(name = "Backend.findByBackendDesc", query = "SELECT b FROM Backend b WHERE b.backendDesc = :backendDesc")})
public class Backend implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idBackend")
    private Integer idBackend;
    @Size(max = 50)
    @Column(name = "backendName")
    private String backendName;
    @Size(max = 5000)
    @Column(name = "backendDesc")
    private String backendDesc;
    @ManyToMany(mappedBy = "backendCollection")
    private Collection<JobManager> jobManagerCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idBackend")
    private Collection<BeInstance> beInstanceCollection;

    public Backend(){        
    }
    
    public Backend(String name, String desc, Collection<JobManager> supp_JM) {
        this.backendName = name;        
        this.backendDesc = desc;
        //now that cascade is gone allow this?!
        this.setJobManagerCollection(supp_JM);
    }
    
    public Backend(String name, String desc) {
        this.backendName = name;        
        this.backendDesc = desc;
        
    }

    public static Backend BackendFactory(String name, String desc, Collection<JobManager> supp_JM){
        //DO AUTH FOR PARAMS!
        
        return new Backend(name, desc, supp_JM);
    }
    
    public Backend(Integer idBackend) {
        this.idBackend = idBackend;
    }

    public Integer getIdBackend() {
        return idBackend;
    }

    public void setIdBackend(Integer idBackend) {
        this.idBackend = idBackend;
    }

    public String getBackendName() {
        return backendName;
    }

    public void setBackendName(String backendName) {
        this.backendName = backendName;
    }


    public String getBackendDesc() {
        return backendDesc;
    }

    public void setBackendDesc(String backendDesc) {
        this.backendDesc = backendDesc;
    }

    @XmlTransient
    public Collection<JobManager> getJobManagerCollection() {
        return jobManagerCollection;
    }

    public void setJobManagerCollection(Collection<JobManager> jobManagerCollection) {
        this.jobManagerCollection = jobManagerCollection;
    }
    
    public String getJobManagerCollectionString(){
        String s = "";
        for(JobManager t : jobManagerCollection){
            if(!s.equals("")){
                s = s + ", ";
            }
            s = s + t.getJobManagerName();
        }
        return s;
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
        hash += (idBackend != null ? idBackend.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Backend)) {
            return false;
        }
        Backend other = (Backend) object;
        if ((this.idBackend == null && other.idBackend != null) || (this.idBackend != null && !this.idBackend.equals(other.idBackend))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.Backend[ idBackend=" + idBackend + " ]";
    }
    
}