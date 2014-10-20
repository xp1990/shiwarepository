/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.shiwa.repository.submission.objects.ImplShort;
import org.shiwa.repository.submission.objects.JSDL.ExecutionNode;
import org.shiwa.repository.submission.objects.JSDL.ImplJSDL;
import org.shiwa.repository.submission.objects.JSDL.Param;
import org.shiwa.repository.submission.objects.Parameter;
import org.shiwa.repository.submission.objects.workflowengines.AbstractDeployment;
import org.shiwa.repository.submission.objects.workflowengines.OnTheFly;
import org.shiwa.repository.submission.objects.workflowengines.PreDeploy;
import org.shiwa.repository.submission.objects.workflowengines.WorkflowEngineInstance;
import org.shiwa.repository.toolkit.wfengine.BeInstance;
import org.shiwa.repository.toolkit.wfengine.WEImplementation;
import org.shiwa.repository.toolkit.wfengine.WEUploadedFile;
import uk.ac.wmin.edgi.repository.common.DatabaseProblemException;
import uk.ac.wmin.edgi.repository.entities.ImpAttribute;
import uk.ac.wmin.edgi.repository.entities.Implementation;
import uk.ac.wmin.edgi.repository.entities.Platform;

/**
 * Class formatting the data in order to return them to the SHIWA Submission 
 * Service
 *
 * @author glassfish
 */
public class SubmissionHelpers {

    private static enum TypeFile {

        APPLICATION, IMPLEMENTATION, WORKFLOW_ENGINE
    }
    private static final String SEN = "Submission Execution Node";

    /**
     * Format a list of object (implementation name, description, selected of not)
     * into a list of ImplShort that can be returned.
     * @param listGiven list of objects
     * @return list of ImplShort
     */
    public static List<ImplShort> treatValidatedImplementations(
            List<Object[]> listGiven) {
        List<ImplShort> listImpl = new ArrayList<ImplShort>();

        if (listGiven != null && !listGiven.isEmpty()) {
            for (int i = 0; i < listGiven.size(); i++) {
                int cpt = 0;
                Object[] result = listGiven.get(i);

                if (result != null && result.length == 3) {
                    listImpl.add(new ImplShort(
                            String.valueOf(result[cpt++]),
                            String.valueOf(result[cpt++]),
                            Boolean.valueOf(String.valueOf(result[cpt++]))));
                }
            }
        }

        return listImpl;
    }

    /**
     * Get the list of all non fixed parameters from a list of attributes
     * of the implementation. This list is formatted into Parameter objects.
     * @param listAttributes list of attributes
     * @return list of parameters
     */
    public static List<Parameter> getNonFixedParameters(
            List<ImpAttribute> listAttributes) {
        return organizeParameters(listAttributes);
    }

    /**
     * Get a list of non-fixed parameters from a list of attributes of an implementation.
     * The Parameter object contains 2 attributes: int as id and a String.
     * The String is formatted as followed:
     * - name/title: String, 
     * - mandatory or not: Yes/No, 
     * - file of not: File/Commandline, 
     * - value: file name or value, 
     * - input or not: Input/Output, 
     * - regex associated: , -> has to appear but empty 
     * - default or not: Default/NoDefault -> by default, Default
     * @param list
     * @return 
     */
    private static List<Parameter> organizeParameters(List<ImpAttribute> list) {
        List<Parameter> listToReturn = new ArrayList<Parameter>();

        if (list != null && !list.isEmpty()) {
            TreeMap<String, String> paramsMap = getParametersMap(list);
            List<String> paramsList = getParametersList(list);
            getNonFixedParametersList(paramsList, paramsMap);

            if (!paramsMap.isEmpty() && !paramsList.isEmpty()) {
                for (int i = 0; i < paramsList.size(); i++) {
                    String idParam = paramsList.get(i);
                    StringBuilder builder = new StringBuilder();
                    builder.append(paramsMap.get(idParam + ".title"));
                    builder.append(",");
                    builder.append("No"); // by default
                    builder.append(",");
                    builder.append(paramsMap.get(idParam + ".file").
                            equals("true") ? "File" : "Commandline");
                    builder.append(",");
                    builder.append(paramsMap.get(idParam + ".defaultValue"));
                    builder.append(",");
                    builder.append(paramsMap.get(idParam + ".input").
                            equals("true") ? "Input" : "Output");
                    builder.append(",");
                    // no regex
                    builder.append(",");
                    builder.append("Default"); // by default

                    listToReturn.add(new Parameter(i, builder.toString()));
                }
            }
        }

        return listToReturn;
    }

    /**
     * Convert a list of attributes from an implementation into a TreeMap
     * @param list list of attributes
     * @return TreeMap containing names and values for each attributes
     */
    private static TreeMap<String, String> getParametersMap(
            List<ImpAttribute> list) {
        TreeMap<String, String> results = new TreeMap<String, String>();

        if (list != null && !list.isEmpty()) {
            for (ImpAttribute attr : list) {
                results.put(attr.getName(), attr.getValue());
            }
        }

        return results;
    }

    /**
     * Fill a list of all non-fixed parameters from a TreeMap of parameters.
     * The name ".fixed" is researched into all of them, if false, then added,
     * otherwise skipped.
     * @param listId list to fill
     * @param data TreeMap containing the list of parameters
     */
    private static void getNonFixedParametersList(List<String> listId,
            TreeMap<String, String> data) {
        List<String> newListId = new ArrayList<String>();

        if (listId != null && !listId.isEmpty()
                && data != null && !data.isEmpty()) {
            for (String idParam : listId) {
                String fixedLine = idParam + ".fixed";

                if (data.containsKey(fixedLine)
                        && data.get(fixedLine).equals("false")) {
                    newListId.add(idParam);
                }
            }

            listId.clear();
            listId.addAll(newListId);
        }
    }

    /**
     * Extract only the parameters from a list of attributes of an implementation.
     * @param list list of attributes
     * @return list of parameters only
     */
    private static List<String> getParametersList(List<ImpAttribute> list) {
        List<String> results = new ArrayList<String>();

        if (list != null && !list.isEmpty()) {
            for (ImpAttribute attr : list) {
                String name = attr.getName();

                if (name.startsWith(SEN + ".parameters.para")) {
                    int index = name.lastIndexOf(".");
                    name = name.substring(0, index);

                    if (!results.contains(name)) {
                        results.add(name.substring(0, index));
                    }
                }
            }
        }

        return results;
    }

    /**
     * Build the implementation to return to the SHIWA Submission Service.
     * The implementation will contain all required fields and will be built from
     * a database representation of the implementation.
     * The URL of the repository is given in order to create links to download
     * input files/workflow definition file
     * @param repositoryURL url of the repository
     * @param impl implementation to format
     * @param implId id of the implementation (used for the links to download)
     * @return Implementation for the SHIWA Submission Service
     * @throws IllegalArgumentException 
     */
    public static ImplJSDL treatImplementationToJSDL(String repositoryURL,
            Implementation impl, int implId) throws IllegalArgumentException {
        ImplJSDL implJSDL = new ImplJSDL();

        // set basic attributes
        implJSDL.setAppName(impl.getApplication().getName());
        implJSDL.setImplVersion(impl.getVersion());
        implJSDL.setWorkflowEngineName(impl.getPlatform().getName());
        implJSDL.setWorkflowEngineVersion(impl.getPlatform().getVersion());

        TreeMap<String, ImpAttribute> attributes =
                new TreeMap<String, ImpAttribute>(impl.getAttributes());

        // set definition file
        if (attributes.containsKey("definition")) {
            String name = attributes.get("definition").getValue();
            implJSDL.setDefinitionFileName(name);
            implJSDL.setDefinitionFilePath(getDownloadingFileURL(repositoryURL,
                    implId, name, TypeFile.IMPLEMENTATION));
        } else {
            throw new IllegalArgumentException("No definition file defined");
        }

        // set execution node
        ExecutionNode executionNode = extractParametersJSDL(repositoryURL, impl,
                attributes);

        // set others
        if (attributes.containsKey(SEN + ".maxWallTime")) {
            try {
                int maxWallTime = Integer.parseInt(
                        attributes.get(SEN + ".maxWallTime").getValue());
                if (maxWallTime > 0) {
                    executionNode.setMaxWallTime(maxWallTime);
                }
            } catch (NumberFormatException ex) {
            }
        }

        implJSDL.setExecutionNode(executionNode);

        return implJSDL;
    }

    /**
     * Build the execution node class representing the Submission Execution Node
     * of an implementation. URL of the repo is required to create downloadable
     * links for the input files.
     * @param repositoryURL url of the repository
     * @param impl object representing the implementation in the database
     * @param attributes list of attributes for the implementation
     * @return execution node
     */
    private static ExecutionNode extractParametersJSDL(String repositoryURL,
            Implementation impl, TreeMap<String, ImpAttribute> attributes) {
        List<String> listParams = getParametersList(
                new ArrayList(attributes.values()));
        ExecutionNode executionNode = new ExecutionNode(
                impl.getApplication().getName() + "#" + impl.getVersion());

        for (String param : listParams) {
            try {
                boolean isFile = Boolean.parseBoolean(
                        attributes.get(param + ".file").getValue());
                boolean isInput = Boolean.parseBoolean(
                        attributes.get(param + ".input").getValue());
                boolean isCustom = attributes.get(param + ".type").getValue().
                        equals("CUSTOM");

                Param paramObject = new Param();
                paramObject.setCmdLine(Boolean.parseBoolean(
                        attributes.get(param + ".cmdLine").getValue()));
                paramObject.setTitle(
                        attributes.get(param + ".title").getValue());
                paramObject.setFile(isFile);
                paramObject.setInput(isInput);
                paramObject.setPrefixCmd(
                        attributes.get(param + ".switchName").getValue());
                paramObject.setFixed(Boolean.parseBoolean(
                        attributes.get(param + ".fixed").getValue()));
                paramObject.setFileName(
                        attributes.get(param + ".defaultValue").getValue());

                if (isFile && isInput) {
                    if (isCustom) {
                        paramObject.setDefaultValue(getDownloadingFileURL(repositoryURL,
                                impl.getId(),
                                attributes.get(param + ".defaultValue").getValue(),
                                TypeFile.IMPLEMENTATION));
                    } else {
                        paramObject.setDefaultValue(getDownloadingFileURL(repositoryURL,
                                impl.getApplication().getId(),
                                attributes.get(param + ".defaultValue").getValue(),
                                TypeFile.APPLICATION));
                    }
                } else {
                    paramObject.setDefaultValue(
                            attributes.get(param + ".defaultValue").getValue());
                }

                if (paramObject.isInput()) {
                    executionNode.getListInputs().add(paramObject);
                } else {
                    executionNode.getListOutputs().add(paramObject);
                }
            } catch (Exception ex) {
            }
        }

        return executionNode;
    }

    /**
     * Build the full workflow engine implementation for a specific workflow engine.
     * This workflow engine implementation will contain the deployment strategy.
     * @param repositoryURL url of the repository for links to download 
     * (On the fly deployment)
     * @param engine workflow engine
     * @param engineInstanceName name of the workflow engine implementation
     * @return full workflow engine implementation for the SHIWA Submission Service
     * @throws IllegalArgumentException
     * @throws DatabaseProblemException 
     */
    public static WorkflowEngineInstance loadWorkflowEngineImplementation(
            String repositoryURL, Platform engine, String engineInstanceName)
            throws IllegalArgumentException, DatabaseProblemException {
        WEImplementation engineInstance = extractWEImplementation(engine,
                engineInstanceName);

        if (engineInstance == null || !engineInstance.isEnabled()) {
            throw new DatabaseProblemException(engineInstanceName
                    + " can no longer be selected");
        }

        WorkflowEngineInstance returnInstance = new WorkflowEngineInstance();
        returnInstance.setName(engineInstanceName);
        returnInstance.setWorkflowEngineName(engine.getName());
        returnInstance.setWorkflowEngineVersion(engine.getVersion());
        returnInstance.setPrefixWorkflow(engineInstance.getPrefixData());

        AbstractDeployment deployment =
                configurationDeployment(engineInstance, repositoryURL, engine);
//        BeInstance middleware = configurationMiddleware(engineInstance);
        BeInstance middleware = engineInstance.getIdBackendInst();

        returnInstance.setDeploymentConfig(deployment);
        returnInstance.setMiddlewareConfig(middleware);

        return returnInstance;
    }

    /**
     * Get the backend instance for a workflow engine implementation associated 
     * to a workflow engine
     * @param engine workflow engine concerned
     * @param engineInstanceName workflow engine implementation name
     * @return backend instance for the workflow engine implementation
     * @throws IllegalArgumentException 
     */
    public static BeInstance loadBackendInstance(Platform engine,
            String engineInstanceName) throws IllegalArgumentException {
        WEImplementation engineInstance = extractWEImplementation(engine,
                engineInstanceName);

        return engineInstance.getIdBackendInst();
    }

    /**
     * Get a full workflow engine implementation. It is recovered from 
     * a workflow engine and a workflow engine implementation name
     * @param engine workflow engine
     * @param engImplName workflow engine implementation name
     * @return full workflow engine implementation
     * @throws IllegalArgumentException 
     */
    private static WEImplementation extractWEImplementation(Platform engine,
            String engImplName) throws IllegalArgumentException {
        List<WEImplementation> engineImplList = new ArrayList<WEImplementation>(
                engine.getWeImplementationCollection());

        for (int i = 0; i < engineImplList.size(); i++) {
            if (engineImplList.get(i).getNameWEImp().equals(engImplName)) {
                return engineImplList.get(i);
            }
        }

        throw new IllegalArgumentException("Workflow engine implementation given "
                + "(" + engImplName + ") is unknown");
    }

    /**
     * Get the deployment strategy from a workflow engine implementation.
     * The repository url is used for downloadable links in case of a on the fly
     * deployment. The workflow engine is used to get the files 
     * in case of an on the fly deployment
     * @param engineInstance workflow engine implementation object
     * @param repositoryURL url of the repository
     * @param engine workflow engine
     * @return OnTheFly or PreDeploy deployment object
     */
    private static AbstractDeployment configurationDeployment(
            WEImplementation engineInstance, String repositoryURL, Platform engine) {
        if (engineInstance.getPreDeployed()) {
            PreDeploy preDeploy = new PreDeploy();
            preDeploy.setShellPathEndPoint(engineInstance.getShellPath());

            return preDeploy;
        } else {
            WEUploadedFile shellFile = engineInstance.getShellWEFileId();
            WEUploadedFile zipFile = engineInstance.getZipWEFileId();

            OnTheFly onTheFly = new OnTheFly();
            onTheFly.setShellName(shellFile.getFilePath());
            onTheFly.setShellPath(getDownloadingFileURL(repositoryURL,
                    engine.getId(), shellFile.getFilePath(),
                    TypeFile.WORKFLOW_ENGINE));
            onTheFly.setZipName(zipFile.getFilePath());
            onTheFly.setZipPath(getDownloadingFileURL(repositoryURL,
                    engine.getId(), zipFile.getFilePath(),
                    TypeFile.WORKFLOW_ENGINE));
            return onTheFly;
        }
    }

    /**
     * Build a downloadable link for a file stored for a application, 
     * implementation or workflow engine.
     * @param repositoryURL url of the shiwa repository
     * @param id id of the workflow engine, application or implementation
     * @param filename name of the file for the link
     * @param typeFile type to download for (application, workflow engine or
     * implementation)
     * @return url where to download the file
     */
    private static String getDownloadingFileURL(String repositoryURL, int id,
            String filename, TypeFile typeFile) {
        String type;
        switch (typeFile) {
            case APPLICATION:
                type = "appid";
                break;
            case IMPLEMENTATION:
                type = "impid";
                break;
            case WORKFLOW_ENGINE:
                type = "idWE";
                break;
            default:
                return null;
        }

        return repositoryURL + "/download?" + type + "=" + id + "&filename="
                + filename;
    }
}
