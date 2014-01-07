/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects.workflowengines;

import java.io.Serializable;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.XmlRootElement;
import org.shiwa.repository.toolkit.wfengine.BeInstance;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowEngineInstance implements Serializable {

    private String name;
    private String workflowEngineName;
    private String workflowEngineVersion;
    private String prefixWorkflow;
    @XmlElement
    private AbstractDeployment deploymentConfig;
    @XmlElement
    private BeInstance middlewareConfig;

    public WorkflowEngineInstance() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkflowEngineName() {
        return workflowEngineName;
    }

    public void setWorkflowEngineName(String workflowEngineName) {
        this.workflowEngineName = workflowEngineName;
    }

    public String getWorkflowEngineVersion() {
        return workflowEngineVersion;
    }

    public void setWorkflowEngineVersion(String workflowEngineVersion) {
        this.workflowEngineVersion = workflowEngineVersion;
    }

    public String getPrefixWorkflow() {
        return prefixWorkflow;
    }

    public void setPrefixWorkflow(String prefixWorkflow) {
        this.prefixWorkflow = prefixWorkflow;
    }

    public AbstractDeployment getDeploymentConfig() {
        return deploymentConfig;
    }

    public void setDeploymentConfig(AbstractDeployment deploymentConfig) {
        this.deploymentConfig = deploymentConfig;
    }

    public BeInstance getMiddlewareConfig() {
        return middlewareConfig;
    }

    public void setMiddlewareConfig(BeInstance middlewareConfig) {
        this.middlewareConfig = middlewareConfig;
    }
}
