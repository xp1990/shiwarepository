/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import org.shiwa.repository.toolkit.util.MyExperimentHelper;
import uk.ac.wmin.edgi.repository.common.AuthorizationException;
import uk.ac.wmin.edgi.repository.common.EntityAlreadyExistsException;
import uk.ac.wmin.edgi.repository.common.EntityNotFoundException;
import uk.ac.wmin.edgi.repository.common.FileOperationFailedException;
import uk.ac.wmin.edgi.repository.common.ValidationFailedException;
import uk.ac.wmin.repo.myexperiment.client.MyExpWorkflow;

/**
 *
 * @author john
 */
class MyExperimentManager {
    private final BackingBean backingBean;

    MyExperimentManager(final BackingBean backingBean) {
        this.backingBean = backingBean;
    }

    public void createApplicationAndImplementation() {
        try {
            MyExpWorkflow myExpWorkflow = new MyExpWorkflow(backingBean.getMyExpWorkflowID(), backingBean.getMyExpWorkflowName());
            MyExperimentHelper experimentHelper = new MyExperimentHelper(backingBean, backingBean.af);
            experimentHelper.groupManagement(myExpWorkflow, backingBean.currentUser);
            backingBean.selectedApp = experimentHelper.workflowManagement(myExpWorkflow);
            backingBean.addMessage(null, FacesMessage.SEVERITY_INFO, "Workflow application is created", null);
            backingBean.selectedImp = experimentHelper.implementationManagement(backingBean.selectedApp, myExpWorkflow);
            backingBean.addMessage(null, FacesMessage.SEVERITY_INFO, "" + "Workflow implementation is created", null);
            experimentHelper.uploadImplementationFiles(backingBean.selectedImp, myExpWorkflow);
            backingBean.addMessage(null, FacesMessage.SEVERITY_INFO, "Workflow files are uploaded", null);
        } catch (IllegalArgumentException ex) {
            backingBean.addMessage(null, FacesMessage.SEVERITY_INFO, ex.getMessage(), null);
        } catch (IllegalStateException ex) {
            backingBean.addMessage(null, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (IOException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileOperationFailedException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EntityAlreadyExistsException e) {
            backingBean.addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            backingBean.addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            backingBean.addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            backingBean.addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
    }
    
}
