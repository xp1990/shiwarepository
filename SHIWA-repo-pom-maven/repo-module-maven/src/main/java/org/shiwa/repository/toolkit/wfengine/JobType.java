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
@Table(name = "job_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "JobType.findAll", query = "SELECT j FROM JobType j"),
    @NamedQuery(name = "JobType.findByJobTypeId", query = "SELECT j FROM JobType j WHERE j.jobTypeId = :jobTypeId"),
    @NamedQuery(name = "JobType.findByJobTypeName", query = "SELECT j FROM JobType j WHERE j.jobTypeName = :jobTypeName")})
public class JobType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "jobTypeId")
    private Integer jobTypeId;
    @Size(max = 50)
    @Column(name = "jobTypeName")
    private String jobTypeName;
    /*
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "jobTypeId")
    private Collection<BeInstance> beInstanceCollection;
    */
    public JobType() {
    }

    public JobType(Integer jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    public Integer getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(Integer jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    public String getJobTypeName() {
        return jobTypeName;
    }

    public void setJobTypeName(String jobTypeName) {
        this.jobTypeName = jobTypeName;
    }
    /*
    @XmlTransient
    public Collection<BeInstance> getBeInstanceCollection() {
        return beInstanceCollection;
    }

    public void setBeInstanceCollection(Collection<BeInstance> beInstanceCollection) {
        this.beInstanceCollection = beInstanceCollection;
    }
    */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (jobTypeId != null ? jobTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JobType)) {
            return false;
        }
        JobType other = (JobType) object;
        if ((this.jobTypeId == null && other.jobTypeId != null) || (this.jobTypeId != null && !this.jobTypeId.equals(other.jobTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.shiwa.repository.toolkit.wfengine.JobType[ jobTypeId=" + jobTypeId + " ]";
    }
    
}
