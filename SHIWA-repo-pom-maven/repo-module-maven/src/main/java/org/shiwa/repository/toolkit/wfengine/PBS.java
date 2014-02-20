/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import uk.ac.wmin.edgi.repository.entities.User;

/**
 *
 * @author edward
 */
@Entity
@DiscriminatorValue("5")
public class PBS extends BeInstance{

    @Column(name = "jobManager")
    private String jobManager;

    protected PBS(){

    }

    public PBS(String _name, Backend _idBackend, String _backendOut, String _backendErr, OperatingSystems _idOS, String _resource, User _wedev, String _jobManager) {
        super(_name, _idBackend, _backendOut, _backendErr, _idOS, _resource, _wedev);
        jobManager = _jobManager;

    }

    public String getJobManager() {
        return jobManager;
    }

    public void setJobManager(String jobManager) {
        this.jobManager = jobManager;
    }



}