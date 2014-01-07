/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import org.shiwa.repository.toolkit.wfengine.Backend;
import org.shiwa.repository.toolkit.wfengine.JobManager;
import org.shiwa.repository.toolkit.wfengine.JobType;
import org.shiwa.repository.toolkit.wfengine.OperatingSystems;

/**
 *
 * @author edward
 */
public class NewBeInstanceBean {

    private String name;
    private String resource;
    private String backendOutput;
    private String backendError;
    private Backend idBackend;
    private OperatingSystems idOS;
    
    //GT2/GT4
    private String site;
    private JobType jobType;
    private String jobManager;
    
    public NewBeInstanceBean() {
        name = "";
        resource ="";
        backendOutput = "";
        backendError = "";
        idBackend = new Backend();
        idOS = new OperatingSystems();
        
        //GT2/GT4
        site = "";
        jobType = new JobType();
        jobManager = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getBackendOutput() {
        return backendOutput;
    }

    public void setBackendOutput(String backendOutput) {
        this.backendOutput = backendOutput;
    }

    public String getBackendErrorOut() {
        return backendError;
    }

    public void setBackendErrorOut(String backendError) {
        this.backendError = backendError;
    }

    public Backend getIdBackend() {
        return idBackend;
    }

    public void setIdBackend(Backend idBackend) {
        this.idBackend = idBackend;
    }

    public OperatingSystems getIdOS() {
        return idOS;
    }

    public void setIdOS(OperatingSystems idOS) {
        this.idOS = idOS;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getJobManager() {
        return jobManager;
    }

    public void setJobManager(String jobManager) {
        this.jobManager = jobManager;
    }
    
    
    
    
}
