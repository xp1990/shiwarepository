/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.shiwa.repository.ui.AppAttrTree;
import org.shiwa.repository.ui.BackingBean;
import uk.ac.wmin.edgi.repository.common.*;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;
import uk.ac.wmin.edgi.repository.transferobjects.*;
import uk.ac.wmin.repo.myexperiment.client.MyExpWorkflow;

/**
 *
 * @author Benoit Meilhac
 */
public class MyExperimentHelper {

    private BackingBean backingBean;
    private ApplicationFacadeLocal af;
    private AppAttrTree appAttrTree;

    public MyExperimentHelper(BackingBean backingBean,
            ApplicationFacadeLocal af) {
        this.backingBean = backingBean;
        this.af = af;
        this.appAttrTree = new AppAttrTree(new ArrayList<AttributeTO>());
    }

    public void groupManagement(MyExpWorkflow myExpWorkflow, UserTO user)
            throws EntityAlreadyExistsException, ValidationFailedException,
            AuthorizationException, EntityNotFoundException {
        if (!backingBean.getGroupNames().contains(myExpWorkflow.getGroupName())) {
            GroupTO selectedGroup = af.createGroup(myExpWorkflow.getGroupName());
            af.addUserToGroup(selectedGroup.getId(),
                    user.getLoginName());
        } else {
            af.addUserToGroup(backingBean.getGroupIDs().get(myExpWorkflow.getGroupName()),
                    user.getLoginName());
        }
    }

    public ApplicationTO workflowManagement(MyExpWorkflow myExpWorkflow)
            throws EntityAlreadyExistsException, ValidationFailedException,
            AuthorizationException, EntityNotFoundException {//TODO: continue here
        ApplicationTO selectedApp = af.createApp(myExpWorkflow.getApplicationName(),
                myExpWorkflow.getApplicationDescription(),
                myExpWorkflow.getGroupName(),
                false, false, false, false, false, false);

        // attribute creation
        af.createAppAttr(selectedApp.getId(), "application",
                myExpWorkflow.getApplicationAttr());
        af.createAppAttr(selectedApp.getId(), "domain",
                myExpWorkflow.getDomainAttr());


        af.createAppAttr(selectedApp.getId(), "keywords",
                myExpWorkflow.getKeywords());
        af.createAppAttr(selectedApp.getId(), "tasktype",
                myExpWorkflow.getTaskTypeAttr());

        inputsConfiguration(selectedApp, myExpWorkflow);
        outputConfiguration(selectedApp, myExpWorkflow);
        datasetConfiguration(selectedApp, myExpWorkflow);

        return selectedApp;
    }

    public ImplementationTO implementationManagement(ApplicationTO selectedApp,
            MyExpWorkflow myExpWorkflow)
            throws EntityAlreadyExistsException, EntityNotFoundException,
            ValidationFailedException, AuthorizationException {
        // implementation creation
        ImplementationTO selectedImp = af.createImp(selectedApp.getId(), // appID
                myExpWorkflow.getEngineName(), // platformID 
                myExpWorkflow.getEngineVersion(), // platformVersion
                myExpWorkflow.getImplementationVersion()); // impl version

        // implementation attributes creation
        af.createImpAttr(selectedImp.getId(), "title",
                myExpWorkflow.getImplementationTitle());
        af.createImpAttr(selectedImp.getId(), "uuid",
                myExpWorkflow.getImplementationUuid());
        af.createImpAttr(selectedImp.getId(), "description",
                myExpWorkflow.getApplicationDescription());
        af.createImpAttr(selectedImp.getId(), "definition",
                myExpWorkflow.getDefinition());
        af.createImpAttr(selectedImp.getId(), "graph",
                myExpWorkflow.getGraph());
        af.createImpAttr(selectedImp.getId(), "language",
                myExpWorkflow.getLanguage());
        af.createImpAttr(selectedImp.getId(), "rights",
                myExpWorkflow.getRights());
        af.createImpAttr(selectedImp.getId(), "licence",
                myExpWorkflow.getLicence());
        af.createImpAttr(selectedImp.getId(), "keywords",
                myExpWorkflow.getKeywords());

        return selectedImp;
    }

    public void uploadImplementationFiles(ImplementationTO selectedImp,
            MyExpWorkflow myExpWorkflow)
            throws MalformedURLException, EntityNotFoundException,
            EntityAlreadyExistsException, ValidationFailedException,
            AuthorizationException, FileOperationFailedException, IOException {
        af.uploadImpFile(selectedImp.getId(),
                myExpWorkflow.getDefinition(),
                new BufferedInputStream(new URL(
                myExpWorkflow.getDefinitionURL()).openStream()));
        af.uploadImpFile(selectedImp.getId(),
                myExpWorkflow.getGraph(),
                new BufferedInputStream(new URL(
                myExpWorkflow.getGraphURL()).openStream()));
        af.updateImpTimestamp(selectedImp.getId());
    }

    private void inputsConfiguration(ApplicationTO selectedApp,
            MyExpWorkflow myExpWorkflow)
            throws EntityAlreadyExistsException, EntityNotFoundException,
            ValidationFailedException, AuthorizationException {
        List<String> workflowInputNames = myExpWorkflow.getWorkflowInputsName();
        List<String> workflowInputDescription =
                myExpWorkflow.getWorkflowInputsDescription();

        for (int i = 0; i < workflowInputNames.size(); i++) {
            String poolPort = appAttrTree.getPortPool()[i];

            af.createAppAttr(selectedApp.getId(), "inports."
                    + poolPort + ".title",
                    workflowInputNames.get(i));
            af.createAppAttr(selectedApp.getId(), "inports."
                    + poolPort + ".datatype",
                    "file");

            if (workflowInputDescription.size() < i) {
                af.createAppAttr(selectedApp.getId(), "inports."
                        + poolPort + ".description", "");
            } else {
                af.createAppAttr(selectedApp.getId(), "inports."
                        + poolPort + ".description",
                        workflowInputDescription.get(i));
            }
        }
    }

    private void outputConfiguration(ApplicationTO selectedApp,
            MyExpWorkflow myExpWorkflow)
            throws EntityAlreadyExistsException, EntityNotFoundException,
            ValidationFailedException, AuthorizationException {
        List<String> workflowOutputNames = myExpWorkflow.getWorkflowOutputsName();
        List<String> workflowOutputDescription =
                myExpWorkflow.getWorkflowOutputsDescription();
        List<String> workflowInputNames = myExpWorkflow.getWorkflowInputsName();

        for (int i = 0; i < workflowOutputNames.size(); i++) {
            String portPool = appAttrTree.getPortPool()[i + workflowInputNames.size()];

            af.createAppAttr(selectedApp.getId(), "outports."
                    + portPool + ".title",
                    workflowOutputNames.get(i));
            af.createAppAttr(selectedApp.getId(), "outports."
                    + portPool + ".datatype",
                    "file");

            if (workflowOutputDescription.size() < i) {
                af.createAppAttr(selectedApp.getId(), "outports."
                        + portPool + ".description", "");
            } else {
                af.createAppAttr(selectedApp.getId(), "outports."
                        + portPool + ".description",
                        workflowOutputDescription.get(i));
            }
        }
    }

    private void datasetConfiguration(ApplicationTO selectedApp,
            MyExpWorkflow myExpWorkflow)
            throws EntityAlreadyExistsException, EntityNotFoundException,
            ValidationFailedException, AuthorizationException {
        List<String> workflowPort = new ArrayList<String>();
        workflowPort.addAll(myExpWorkflow.getWorkflowInputsName());
        workflowPort.addAll(myExpWorkflow.getWorkflowOutputsName());
        String configPool = appAttrTree.getConfigPool()[0];

        af.createAppAttr(selectedApp.getId(), "configurations."
                + configPool + ".description",
                "");

        for (int i = 0; i < workflowPort.size(); i++) {
            af.createAppAttr(selectedApp.getId(), "configurations."
                    + configPool + "."
                    + appAttrTree.getPortPool()[i], workflowPort.get(i));
        }
    }
}
