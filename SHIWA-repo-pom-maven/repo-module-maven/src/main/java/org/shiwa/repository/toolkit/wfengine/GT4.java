/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import uk.ac.wmin.edgi.repository.entities.User;

/**
 *
 * @author edward
 */
@Entity
@DiscriminatorValue("1")
public class GT4 extends BeInstance{
    @Size(max = 100)
    @Column(name = "site")
    private String site;
    @Size(max = 50)
    @Column(name = "jobManager")
    private String jobManager;
    @JoinColumn(name = "jobTypeId", referencedColumnName = "jobTypeId")
    @ManyToOne(optional = false)
    private JobType jobTypeId;

    public GT4(){

    }

    public GT4(String _jobManager, JobType _jobTypeId, String _name, Backend _idBackend, String _site, String _backendOut, String _backendErr, OperatingSystems _idOS, User _wedev) {
        super(_name, _idBackend, _backendOut, _backendErr, _idOS, _wedev);
        this.site = _site;
        this.jobManager = _jobManager;
        this.jobTypeId = _jobTypeId;
    }

    public GT4(String _jobManager, JobType _jobTypeId, String _name, Backend _idBackend, String _site, String _backendOut, String _backendErr, OperatingSystems _idOS, String _resource, User _wedev) {
        super(_name, _idBackend, _backendOut, _backendErr, _idOS, _resource, _wedev);
        this.site = _site;
        this.jobManager = _jobManager;
        this.jobTypeId = _jobTypeId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getJobManager() {
        return jobManager;
    }

    public void setJobManager(String jobManager) {
        this.jobManager = jobManager;
    }

    public JobType getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(JobType jobTypeId) {
        this.jobTypeId = jobTypeId;
    }
}
