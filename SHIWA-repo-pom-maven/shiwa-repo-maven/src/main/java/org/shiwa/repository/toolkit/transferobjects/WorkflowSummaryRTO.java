/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import org.shiwa.repository.toolkit.util.RepoUtil;

/**
 *
 * @author kukla
 */
public class WorkflowSummaryRTO {
    int workflowId;
    String workflowName;
    String description;
    String keywords;

    public WorkflowSummaryRTO(int workflowId, String workflowName, String description, String keywords) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.description = description;
        this.keywords = keywords;
    }
    
    public WorkflowSummaryRTO(WorkflowRTO workflow) {
        this.workflowId = workflow.getId();
        this.workflowName = workflow.getName();
        this.description = workflow.getDescription();
        this.keywords = workflow.getKeywords();
    }

    public WorkflowSummaryRTO() {
    }

    @Override
    public String toString() {
        return workflowId + RepoUtil.UNIT + workflowName + RepoUtil.UNIT + description + RepoUtil.UNIT + keywords;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
}