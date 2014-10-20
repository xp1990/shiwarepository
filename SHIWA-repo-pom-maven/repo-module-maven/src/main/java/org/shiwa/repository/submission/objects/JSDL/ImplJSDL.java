/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects.JSDL;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing an implementation to return to the SHIWA Submission Service
 * in order to complete the JSDL file
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@XmlRootElement
public class ImplJSDL implements Serializable {

    private String appName;
    private String implVersion;
    private String definitionFileName;
    private String definitionFilePath;
    private String workflowEngineName;
    private String workflowEngineVersion;
    private ExecutionNode executionNode;

    public ImplJSDL() {
        this.appName = null;
        this.definitionFileName = null;
        this.definitionFilePath = null;
        this.workflowEngineName = null;
        this.workflowEngineVersion = null;
        this.executionNode = null;
    }

    public String getImplVersion() {
        return implVersion;
    }

    public void setImplVersion(String implVersion) {
        this.implVersion = implVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDefinitionFileName() {
        return definitionFileName;
    }

    public void setDefinitionFileName(String definitionFileName) {
        this.definitionFileName = definitionFileName;
    }

    public String getDefinitionFilePath() {
        return definitionFilePath;
    }

    public void setDefinitionFilePath(String definitionFilePath) {
        this.definitionFilePath = definitionFilePath;
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

    public ExecutionNode getExecutionNode() {
        return executionNode;
    }

    public void setExecutionNode(ExecutionNode executionNode) {
        this.executionNode = executionNode;
    }
}
