package uk.ac.wmin.repo.myexperiment.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author Benoit Meilhac
 */
public class MyExperimentClient {

    private Logger logger;

    private enum TYPE_DATA {

        NAME("name"), DESCRIPTION("description");
        private String name;

        private TYPE_DATA(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    };

    private enum TYPE_PORT {

        INPUT("source"), OUTPUT("sink");
        private String name;

        private TYPE_PORT(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    };

    public enum TYPE_WORKFLOW {

        TAVERNA1, TAVERNA2
    };

    public MyExperimentClient() {
    }

    public String getWorkflowTitle(String workflow) {
        logger.info("MyExperimentClient title: " + workflow);

        String urlWorkflowBasic = "http://www.myexperiment.org"
                + "/workflow.xml?id=" + workflow + "&elements=title";
        WorkflowContentsSummary workflowSummary = new WorkflowContentsSummary();
        getSpecificData("title", urlWorkflowBasic, workflowSummary);

        return workflowSummary.getTitle();
    }

    public List<String> getWorkflowInputNames(String workflow,
            TYPE_WORKFLOW typeWorkflow) {
        logger.info("MyExperimentClient input names for:" + workflow + " --- " + typeWorkflow);
        switch (typeWorkflow) {
            case TAVERNA1:
                return filledListTaverna1(workflow, TYPE_PORT.INPUT, TYPE_DATA.NAME);
            case TAVERNA2:
                return filledListTaverna2(workflow, TYPE_PORT.INPUT, TYPE_DATA.NAME);
            default:
                return new ArrayList<String>();
        }
    }

    public List<String> getWorkflowInputDescription(String workflow,
            TYPE_WORKFLOW typeWorkflow) {
        logger.info("MyExperimentClient input descriptions for:" + workflow + " --- " + typeWorkflow);
        switch (typeWorkflow) {
            case TAVERNA1:
                return filledListTaverna1(workflow, TYPE_PORT.INPUT, TYPE_DATA.DESCRIPTION);
            case TAVERNA2:
                return filledListTaverna2(workflow, TYPE_PORT.INPUT, TYPE_DATA.DESCRIPTION);
            default:
                return new ArrayList<String>();
        }
    }

    public List<String> getWorkflowOutputNames(String workflow,
            TYPE_WORKFLOW typeWorkflow) {
        logger.info("MyExperimentClient output names for:" + workflow + " --- " + typeWorkflow);
        switch (typeWorkflow) {
            case TAVERNA1:
                return filledListTaverna1(workflow, TYPE_PORT.OUTPUT, TYPE_DATA.NAME);
            case TAVERNA2:
                return filledListTaverna2(workflow, TYPE_PORT.OUTPUT, TYPE_DATA.NAME);
            default:
                return new ArrayList<String>();
        }
    }

    public List<String> getWorkflowOutputDescription(String workflow,
            TYPE_WORKFLOW typeWorkflow) {
        logger.info("MyExperimentClient input descriptions for:" + workflow + " --- " + typeWorkflow);
        switch (typeWorkflow) {
            case TAVERNA1:
                return filledListTaverna1(workflow, TYPE_PORT.OUTPUT, TYPE_DATA.DESCRIPTION);
            case TAVERNA2:
                return filledListTaverna2(workflow, TYPE_PORT.OUTPUT, TYPE_DATA.DESCRIPTION);
            default:
                return new ArrayList<String>();
        }
    }

    public WorkflowContentsSummary getWorkflow(String workflow) {
        logger.info("MyExperimentClient all workflow data: " + workflow);
        String urlWorkflowBasic = "http://www.myexperiment.org"
                + "/workflow.xml?id=" + workflow + "&elements=";
        WorkflowContentsSummary workflowSummary = new WorkflowContentsSummary();
        String[] listOfID = workflowSummary.getListOfID();

        for (int i = 0; i < listOfID.length; i++) {
            String currentUrl = urlWorkflowBasic + listOfID[i];
            getSpecificData(listOfID[i], currentUrl, workflowSummary);
        }

        return workflowSummary;
    }

    private List<String> filledListTaverna1(String workflow, TYPE_PORT type,
            TYPE_DATA data) {
        if (logger.isDebugEnabled()) {
            logger.debug("MyExperimentClient for (" + workflow + ") type ("
                    + type + ") data (" + data + ")");
        }
        String urlWorkflow = "http://www.myexperiment.org"
                + "/workflow.xml?id=" + workflow + "&elements=components";
        SAXBuilder builder = new SAXBuilder();
        List<String> list = new ArrayList<String>();

        try {
            Document document = builder.build(urlWorkflow);
            Element rootNode = document.getRootElement();
            Element components = rootNode.getChild("components");

            if (components != null) {
                // FIXME: find a better way, instead of the + "s"
                Element sources = components.getChild(type.getValue() + "s");

                if (sources != null) {
                    List listSources = sources.getChildren(type.getValue());
                    extractNameOrDescriptionTaverna1(listSources, data, list);
                }
            }
        } catch (JDOMException ex) {
            logger.error("Failed to retrieve example workflows", ex);
        } catch (IOException ex) {
            logger.error("Failed to retrieve example workflows", ex);
        }

        return list;
    }

    private void extractNameOrDescriptionTaverna1(List listSources,
            TYPE_DATA data, List<String> list) {
        for (int i = 0; i < listSources.size(); i++) {
            Element source = (Element) listSources.get(i);
            Element child = source.getChild(data.getValue());

            if (child != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("(" + child.getTextTrim() + ") added to data (" + data + ")");
                }
                list.add(child.getText().trim());
            }
        }
    }

    private List<String> filledListTaverna2(String workflow, TYPE_PORT type,
            TYPE_DATA data) {
        if (logger.isDebugEnabled()) {
            logger.debug("MyExperimentClient for (" + workflow + ") type ("
                    + type + ") data (" + data + ")");
        }
        String urlWorkflow = "http://www.myexperiment.org"
                + "/workflow.xml?id=" + workflow + "&elements=components";
        SAXBuilder builder = new SAXBuilder();
        List<String> list = new ArrayList<String>();

        try {
            Document document = builder.build(urlWorkflow);
            Element rootNode = document.getRootElement();
            Element components = rootNode.getChild("components");

            if (components != null) {
                Element dataflows = components.getChild("dataflows");

                if (dataflows != null) {
                    List dataflowList = dataflows.getChildren();
                    handleDataflowsTaverna2(dataflowList, type, data, list);
                }
            }
        } catch (JDOMException ex) {
            logger.error("Failed to retrieve example workflows", ex);
        } catch (IOException ex) {
            logger.error("Failed to retrieve example workflows", ex);
        }

        return list;
    }

    private void handleDataflowsTaverna2(List dataflowList,
            TYPE_PORT type, TYPE_DATA data, List<String> list) {
        for (int i = 0; i < dataflowList.size(); i++) {
            Element dataflow = (Element) dataflowList.get(i);

            if (isTopDataflowRole(dataflow)) {
                // FIXME: find a better way, instead of the + "s"
                Element sources = dataflow.getChild(type.getValue() + "s");

                if (sources != null) {
                    List listSources = sources.getChildren(type.getValue());
                    extractNameOrDescriptionTaverna2(listSources, data, list);
                    break;
                }
            }
        }
    }

    private boolean isTopDataflowRole(Element dataflow) {
        return dataflow.getAttribute("role") != null
                && dataflow.getAttribute("role").getValue().
                equals("top");
    }

    private void extractNameOrDescriptionTaverna2(List listSources,
            TYPE_DATA data, List<String> list) {
        for (int j = 0; j < listSources.size(); j++) {
            Element source = (Element) listSources.get(j);

            if (data != TYPE_DATA.DESCRIPTION) {
                Element child = source.getChild(data.getValue());

                if (child != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("(" + child.getTextTrim() + ") added to data (" + data + ")");
                    }
                    list.add(child.getText().trim());
                }
            } else {
                Element child = source.getChild(
                        data.getValue() + "s");

                if (child != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("(" + child.getTextTrim() + ") added to data (" + data + ")");
                    }
                    list.add(child.getValue().trim());
                }
            }
        }
    }

    private void getSpecificData(String id, String urlWorkflow,
            WorkflowContentsSummary workflowMap) {
        if (logger.isDebugEnabled()) {
            logger.debug("MyExperimentClient get (" + id + ") from (" + urlWorkflow + ")");
        }
        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(urlWorkflow);
            Element rootNode = document.getRootElement();
            Element element = rootNode.getChild(id);
            workflowMap.put(id, "");

            if (element != null) {
                if (!isTagsRequested(id)) {
                    workflowMap.put(id, element.getValue().trim());
                } else {
                    treatTagsOfWorkflow(element, workflowMap, id);
                }
            }
        } catch (JDOMException ex) {
            logger.error("Failed to retrieve example workflows", ex);
        } catch (IOException ex) {
            logger.error("Failed to retrieve example workflows", ex);
        }
    }

    private boolean isTagsRequested(String id) {
        return id.equals("tags");
    }

    private void treatTagsOfWorkflow(Element element,
            WorkflowContentsSummary workflowMap, String id) {
        List children = element.getChildren();
        workflowMap.put("tag", "");
        workflowMap.put(id, element.getValue().trim());

        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            workflowMap.put("tag", child.getValue().trim());
        }
    }
}
