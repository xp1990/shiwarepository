/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author xp
 */
@Entity
@Table(name = "job_manager")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "JobManager.findAll", query = "SELECT j FROM JobManager j"),
    @NamedQuery(name = "JobManager.findByJobManagerId", query = "SELECT j FROM JobManager j WHERE j.jobManagerId = :jobManagerId"),
    @NamedQuery(name = "JobManager.findByJobManagerName", query = "SELECT j FROM JobManager j WHERE j.jobManagerName = :jobManagerName")})
public class JobManager implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "jobManagerId")
    private Integer jobManagerId;
    @Size(max = 50)
    @Column(name = "jobManagerName")
    private String jobManagerName;
    @JoinTable(name = "supported_job_managers", joinColumns = {
        @JoinColumn(name = "jobManagerId", referencedColumnName = "jobManagerId")}, inverseJoinColumns = {
        @JoinColumn(name = "idBackend", referencedColumnName = "idBackend")})
    @ManyToMany/*(cascade = CascadeType.ALL)*/
    private Collection<Backend> backendCollection;

    public JobManager() {
    }
       
    public JobManager(Integer jobManagerId) {
        this.jobManagerId = jobManagerId;
    }

    public Integer getJobManagerId() {
        return jobManagerId;
    }

    public void setJobManagerId(Integer jobManagerId) {
        this.jobManagerId = jobManagerId;
    }

    public String getJobManagerName() {
        return jobManagerName;
    }

    public void setJobManagerName(String jobManagerName) {
        this.jobManagerName = jobManagerName;
    }

    @XmlTransient
    public Collection<Backend> getBackendCollection() {
        return backendCollection;
    }

    public void setBackendCollection(Collection<Backend> backendCollection) {
        this.backendCollection = backendCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (jobManagerId != null ? jobManagerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JobManager)) {
            return false;
        }
        JobManager other = (JobManager) object;
        if ((this.jobManagerId == null && other.jobManagerId != null) || (this.jobManagerId != null && !this.jobManagerId.equals(other.jobManagerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.JobManager[ jobManagerId=" + jobManagerId + " ]";
    }
    
}
