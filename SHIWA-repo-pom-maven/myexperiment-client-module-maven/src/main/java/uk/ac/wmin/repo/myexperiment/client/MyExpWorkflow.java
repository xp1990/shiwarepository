/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.repo.myexperiment.client;

import java.util.List;

/**
 *
 * @author Benoit Meilhac
 */
public class MyExpWorkflow {

    private String workflowID;
    private String applicationName;
    private String language;
    private String groupName;
    private String applicationDescription;
    private String applicationAttr;
    private String domainAttr;
    private String taskTypeAttr;
    private String keywords;
    private List<String> workflowInputsName;
    private List<String> workflowInputsDescription;
    private List<String> workflowOutputsName;
    private List<String> workflowOutputsDescription;
    private String engineName;
    private String engineVersion;
    private String implementationVersion;
    private String implementationTitle;
    private String implementationUuid;
    private String definition;
    private String definitionURL;
    private String graph;
    private String graphURL;
    private String rights;
    private String licence;
    private List<String> dependencies;
    private MyExperimentClient myClient;
    private WorkflowContentsSummary workflowContents;

    public MyExpWorkflow(String workflowID, String applicationName)
            throws IllegalArgumentException, IllegalStateException {
        this.myClient = new MyExperimentClient();
        this.workflowID = workflowID;
        this.applicationName = applicationName;

        fillAllData();
    }

    private void fillAllData()
            throws IllegalArgumentException, IllegalStateException {
        controlMyExpWorkflow();
        String helper;

        MyExperimentClient.TYPE_WORKFLOW typeWorkflow =
                isTaverna1()
                ? MyExperimentClient.TYPE_WORKFLOW.TAVERNA1
                : MyExperimentClient.TYPE_WORKFLOW.TAVERNA2;

        workflowInputsName = myClient.getWorkflowInputNames(workflowID,
                typeWorkflow);
        workflowInputsDescription = myClient.getWorkflowInputDescription(workflowID,
                typeWorkflow);
        workflowOutputsName = myClient.getWorkflowOutputNames(workflowID,
                typeWorkflow);
        workflowOutputsDescription = myClient.getWorkflowOutputDescription(workflowID,
                typeWorkflow);

        keywords = workflowContents.getTags().trim().replaceAll("\\s  ", ",");

        definitionURL = workflowContents.getContentUri();
        graphURL = workflowContents.getThumbnailBig();
        definition = definitionURL.substring(definitionURL.lastIndexOf("/") + 1);

        if (isTaverna1()) {
            graph = definition.replace(".xml", ".jpg");
        } else {
            graph = definition.replace(".t2flow", ".jpg");
        }

        applicationDescription = workflowContents.getDescription().
                replaceAll("\\<.*?>", "").concat("\n").concat("\n").
                concat("This workflow has been downloaded from the myExperiment"
                + " web site. \n").
                concat("URL: http://www.myexperiment.org/workflows/"
                + workflowID + ".html");

        groupName = applicationName + "MyExp";

        if (workflowOutputsDescription.size() < workflowOutputsName.size()) {
            for (int k = 0; k < workflowOutputsName.size(); k++) {
                workflowOutputsDescription.add("");
            }
        }

        if (workflowInputsDescription.size() < workflowInputsName.size()) {
            for (int k = 0; k < workflowInputsName.size(); k++) {
                workflowInputsDescription.add("");
            }
        }

        if (workflowContents.getTag() != null) {
            applicationAttr = domainAttr = workflowContents.getTag();
        } else {
            applicationAttr = domainAttr = "";
        }

        taskTypeAttr = "";

        helper = workflowContents.getPreview();
        implementationVersion = helper.substring(helper.lastIndexOf("previews") - 2,
                helper.lastIndexOf("previews") - 1) + ".0";
        implementationTitle = workflowContents.getTitle();
        implementationUuid = rights = "";
        licence = workflowContents.getLicenseType();


    }

    private boolean isTaverna1() {
        return language.equals("scufl+xml");
    }

    private void controlMyExpWorkflow()
            throws IllegalArgumentException, IllegalStateException {
        if (applicationName == null
                || !applicationName.matches("[A-Za-z0-9-_]{3,250}")) {
            throw new IllegalArgumentException("Application names can only "
                    + "contain alphanumeric characters, - and _. They must be "
                    + "between 3 and 50 charaters long");
        }

        workflowContents = myClient.getWorkflow(workflowID);

        if (workflowContents == null) {
            throw new IllegalArgumentException("Workflow with ID number "
                    + workflowID + " does not exist or you are authorized to "
                    + "import it");
        }

        language = workflowContents.getContentType();
        language = language.substring(language.lastIndexOf(".") + 1);
        engineName = workflowContents.getType().substring(0, 7);

        if (engineName.equals("Taverna") && isTaverna1()) {
            engineVersion = "1.7";
        } else if (engineName.equals("Taverna")
                && isTaverna2()) {
            engineVersion = "2.4";
        } else {
            throw new IllegalStateException("Currently only Taverna 1 and 2 "
                    + "workflows are supported.");
        }
    }

    private boolean isTaverna2() {
        return language.equals("t2flow+xml");
    }

    public String getApplicationAttr() {
        return applicationAttr;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getDefinition() {
        return definition;
    }

    public String getDefinitionURL() {
        return definitionURL;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public String getDomainAttr() {
        return domainAttr;
    }

    public String getEngineName() {
        return engineName;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public String getGraph() {
        return graph;
    }

    public String getGraphURL() {
        return graphURL;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getImplementationTitle() {
        return implementationTitle;
    }

    public String getImplementationUuid() {
        return implementationUuid;
    }

    public String getImplementationVersion() {
        return implementationVersion;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getLanguage() {
        return language;
    }

    public String getLicence() {
        return licence;
    }

    public MyExperimentClient getMyClient() {
        return myClient;
    }

    public String getRights() {
        return rights;
    }

    public String getTaskTypeAttr() {
        return taskTypeAttr;
    }

    public WorkflowContentsSummary getWorkflowContents() {
        return workflowContents;
    }

    public String getWorkflowID() {
        return workflowID;
    }

    public List<String> getWorkflowInputsDescription() {
        return workflowInputsDescription;
    }

    public List<String> getWorkflowInputsName() {
        return workflowInputsName;
    }

    public List<String> getWorkflowOutputsDescription() {
        return workflowOutputsDescription;
    }

    public List<String> getWorkflowOutputsName() {
        return workflowOutputsName;
    }
}
