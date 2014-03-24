/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.FileImageOutputStream;
import org.shiwa.desktop.data.util.properties.Locations;
import org.shiwa.repository.toolkit.exceptions.ForbiddenException;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;
import org.shiwa.repository.toolkit.exceptions.UnauthorizedException;
import org.shiwa.repository.toolkit.exceptions.ItemExistsException;
import org.shiwa.repository.toolkit.transferobjects.WorkflowSummaryRTO;
import org.shiwa.repository.toolkit.transferobjects.ImplementationRTO;
import org.shiwa.repository.toolkit.transferobjects.ConfigurationSummaryRTO;
import org.shiwa.repository.toolkit.transferobjects.EngineRTO;
import org.shiwa.repository.toolkit.transferobjects.SignatureRTO;
import org.shiwa.repository.toolkit.transferobjects.WorkflowRTO;
import org.shiwa.repository.toolkit.transferobjects.ConfigurationRTO;
import org.shiwa.repository.toolkit.transferobjects.ImplementationSummaryRTO;
import org.shiwa.repository.toolkit.transferobjects.AccessRightsRTO;
import org.shiwa.repository.toolkit.transferobjects.BundleFileRTO;
import org.shiwa.repository.toolkit.transferobjects.ConfigurationNodeRTO;
import org.shiwa.repository.toolkit.transferobjects.DependencyRTO;
import org.shiwa.repository.toolkit.transferobjects.GroupRTO;
import org.shiwa.repository.toolkit.transferobjects.PortRTO;
import org.shiwa.repository.toolkit.util.RepoUser;
import uk.ac.wmin.edgi.repository.common.AuthorizationException;
import uk.ac.wmin.edgi.repository.common.EntityAlreadyExistsException;
import uk.ac.wmin.edgi.repository.common.EntityNotFoundException;
import uk.ac.wmin.edgi.repository.common.ImpFile;
import uk.ac.wmin.edgi.repository.common.ValidationFailedException;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;
import uk.ac.wmin.edgi.repository.transferobjects.AppFileTO;
import uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO;
import uk.ac.wmin.edgi.repository.transferobjects.AttributeTO;
import uk.ac.wmin.edgi.repository.transferobjects.GroupTO;
import uk.ac.wmin.edgi.repository.transferobjects.ImpFileTO;
import uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO;
import uk.ac.wmin.edgi.repository.transferobjects.PlatformTO;

/**
 *
 * @author kukla
 */
public class ToolkitImplementation implements ToolkitInterface {

    ApplicationFacadeLocal af = null;

    public ToolkitImplementation() {
    }

    public ToolkitImplementation(ApplicationFacadeLocal af) {
        this.af = af;
    }

    @Override
    public List<WorkflowSummaryRTO> listWorkflows(int userId, boolean modify) throws UnauthorizedException {
        checkUserAuthorized(userId);
        Logger.getLogger(ToolkitImplementation.class.getName()).log(Level.INFO, "User Authorized as ID: " + userId);
        List<WorkflowSummaryRTO> wfsList = new ArrayList<WorkflowSummaryRTO>();
        List<ApplicationTO> appList;
        if (modify) {
            Logger.getLogger(ToolkitImplementation.class.getName()).log(Level.INFO, "getAppsModify");
            System.out.println();
            appList = af.listApplicationsUserCanModify();
        } else {
            Logger.getLogger(ToolkitImplementation.class.getName()).log(Level.INFO, "getAppsRead");
            appList = af.listApplicationsUserCanRead();
        }

        if (appList != null) {
            Logger.getLogger(ToolkitImplementation.class.getName()).log(Level.INFO, "App list not null. Getting workflow summaries....");

            for(ApplicationTO a : appList){
                if(a.getPublished()){
                    wfsList.add(getWorkflowSummaryRTO(a));
                }
            }

        }
        return wfsList;
    }

    // method to generate WorkflowSummaryRTO from ApplicationTO
    private WorkflowSummaryRTO getWorkflowSummaryRTO(ApplicationTO applicationTO) {
        if (applicationTO == null) {
            return null;
        }

        // getting keywords form app attrs
        List<AttributeTO> attrList = af.listAttributesOfApplication(applicationTO.getId());
        Iterator<AttributeTO> iter = attrList.iterator();
        AttributeTO item;
        String keywords = "";
        while (iter.hasNext()) {
            item = iter.next();
            if (item.getName().equals("keywords")) {
                keywords = item.getValue();
            }
        }

        WorkflowSummaryRTO workflowSummary = new WorkflowSummaryRTO(
                applicationTO.getId(),
                applicationTO.getName(),
                applicationTO.getDescription(),
                keywords);

        return workflowSummary;
    }

    @Override
    public List<ImplementationSummaryRTO> listImplementations(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        checkUserCanReadApplication(userId, workflowId);

        List<ImplementationSummaryRTO> imsList = new ArrayList<ImplementationSummaryRTO>();
        List<ImplementationTO> implementations = af.listImplementationsOfApplication(workflowId);
        if (implementations.isEmpty()) {
            throw new NotFoundException("The given workflow has no implementations!");
        }

        for(ImplementationTO i : implementations){
            if(i.isPublic()){
                imsList.add(getImlementationSummaryRTO(i));
            }
        }


        return imsList;
    }

    public List<ImplementationSummaryRTO> listImplementations(int userId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);

        List<ImplementationSummaryRTO> imsList = new ArrayList<ImplementationSummaryRTO>();
        List<ImplementationTO> implementations = af.listImplementations();
        if (implementations.isEmpty()) {
            throw new NotFoundException("The given workflow has no implementations!");
        }
        Iterator<ImplementationTO> iter = implementations.iterator();
        while (iter.hasNext()) {
            imsList.add(getImlementationSummaryRTO(iter.next()));
        }
        return imsList;
    }

    // method to generate ImplementationSummaryRTO from ImplementationTO
    private ImplementationSummaryRTO getImlementationSummaryRTO(ImplementationTO implementationTO) {
        if (implementationTO == null) {
            return null;
        }

        //get data from attribute table
        List<String> dciDeps = new ArrayList<String>();
        List<String> dciConfs = new ArrayList<String>();
        List<AttributeTO> attributes = af.listAttributesOfImplementation(implementationTO.getId());
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;
        String title = "";
        String keywords = "";
        String language = "";
        String description = "";
        String[] depKey;

        while (aIter.hasNext()) {
            aItem = aIter.next();
            if (aItem.getValue().equals("DCI")
                    && aItem.getName().startsWith("dependencies.")
                    && aItem.getName().endsWith(".type")) {
                dciDeps.add(aItem.getName().split("\\.")[1]);
            }
            if (aItem.getName().equals("title")) {
                title = aItem.getValue();
            }
            if (aItem.getName().equals("description")) {
                description = aItem.getValue();
            }
            if (aItem.getName().equals("language")) {
                language = aItem.getValue();
            }
            if (aItem.getName().equals("keywords")) {
                keywords = aItem.getValue();
            }
        }


        aIter = attributes.iterator();
        while (aIter.hasNext()) {
            aItem = aIter.next();
            if (aItem.getName().startsWith("configurations.")
                    && aItem.getName().split("\\.").length > 2
                    && dciDeps.contains(aItem.getName().split("\\.")[2])
                    && !dciConfs.contains(aItem.getValue())) {
                dciConfs.add(aItem.getValue());
            }
            if (aItem.getName().equals("keywords")) {
                keywords = aItem.getValue();
            }
        }

        ImplementationSummaryRTO implementationSummary = new ImplementationSummaryRTO(
                implementationTO.getId(),
                implementationTO.getAppId(),
                implementationTO.getVersion(),
                implementationTO.getPlatformName(),
                implementationTO.getPlatformVersion(),
                title,
                description,
                language,
                keywords,
                dciConfs);

        return implementationSummary;
    }

    @Override
    public List<ConfigurationSummaryRTO> listConfigurations(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        checkUserCanReadApplication(userId, workflowId);

        Map<Integer, ConfigurationSummaryRTO> configMap = new HashMap<Integer, ConfigurationSummaryRTO>();
        //List<ConfigurationSummaryRTO> confs = new ArrayList<ConfigurationSummaryRTO>();
        //List<Integer> confIDs = new ArrayList<Integer>();
        List<AttributeTO> attributes = af.listAttributesOfApplication(workflowId);
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;
        String confId;
        String description;
        String title;

        if (attributes.isEmpty()) {
            throw new NotFoundException("Workflow " + workflowId + " not found or has no configurations.");
        }

        while (aIter.hasNext()) {
            aItem = aIter.next();
            //System.out.println("attrs: "+aItem.getName());
            //find configurations
            if (aItem.getName().startsWith("configurations.") && aItem.getName().split("\\.").length > 1) {
                try {
                    ConfigurationSummaryRTO csRTO;

                    confId = aItem.getName().split("\\.")[1];

                    //map int to string for conf id
                    int id = Integer.parseInt(confId.replaceFirst("^.*\\D", ""));

                    if (configMap.containsKey(id)) {
                        csRTO = configMap.get(id);
                    } else {
                        csRTO = new ConfigurationSummaryRTO(id, "", "");
                        configMap.put(id, csRTO);
                    }

                    if (isDescription(aItem)) {
                        csRTO.setDescription(aItem.getValue());
                    } else if (aItem.getName().endsWith(".title")) {
                        csRTO.setTitle(aItem.getValue());
                    }
                } catch (Exception e) {/*if cannot get integer, then don't insert the object*/

                }
            }
        }

        if (configMap.isEmpty()) {
            throw new NotFoundException("Workflow " + workflowId + " has no configurations!");
        }



        return new ArrayList<ConfigurationSummaryRTO>(configMap.values());
    }

    @Override
    public int getWorkflowId(int userId, int implementationId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        if (af.canUserReadImp(userId, implementationId) != null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserReadApplication(userId, implementationId));
        }
        ImplementationTO implementation = af.loadImplementation(implementationId);
        if (implementation == null) {
            throw new NotFoundException("Implementation " + implementationId + " not found.");
        }
        return implementation.getAppId();
    }

    @Override
    public SignatureRTO getSignature(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        checkUserCanReadApplication(userId, workflowId);


        List<AttributeTO> attributes = af.listAttributesOfApplication(workflowId);
        List<String> inportIDs = new ArrayList<String>();
        List<PortRTO> inports = new ArrayList<PortRTO>();
        List<String> outportIDs = new ArrayList<String>();
        List<PortRTO> outports = new ArrayList<PortRTO>();
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;
        String inportId;
        String outportId;
        String tasktype = "";

        if (attributes.isEmpty()) {
            throw new NotFoundException("Workflow " + workflowId + " not found or has no signature defined");
        }

        while (aIter.hasNext()) {
            aItem = aIter.next();

            //see if we have an inport
            if (aItem.getName().startsWith("inports.")
                    && aItem.getName().split("\\.").length > 2) {
                //get id
                inportId = aItem.getName().split("\\.")[1];

                //if the port was not inserted before, then add
                if (!inportIDs.contains(inportId)) {
                    inportIDs.add(inportId);
                    inports.add(new PortRTO(inportId, "", "", "", false));
                }

                //insert value
                Iterator<PortRTO> pIter = inports.iterator();
                PortRTO pItem;
                while (pIter.hasNext()) {
                    pItem = pIter.next();
                    if (pItem.getName().equals(inportId)) {
                        if (aItem.getName().endsWith(".datatype")) {
                            pItem.setType(aItem.getValue());
                        }
                        if (isDescription(aItem)) {
                            pItem.setDescription(aItem.getValue());
                        }
                        if (aItem.getName().endsWith(".title")) {
                            pItem.setValue(aItem.getValue());
                        }
                    }
                }
            }

            //see if we have an outport
            if (aItem.getName().startsWith("outports.")
                    && aItem.getName().split("\\.").length > 2) {
                //get id
                outportId = aItem.getName().split("\\.")[1];

                //if the port was not inserted before, then add
                if (!outportIDs.contains(outportId)) {
                    outportIDs.add(outportId);
                    outports.add(new PortRTO(outportId, "", "", "", false));
                }

                //insert value
                Iterator<PortRTO> pIter = outports.iterator();
                PortRTO pItem;
                while (pIter.hasNext()) {
                    pItem = pIter.next();
                    if (pItem.getName().equals(outportId)) {
                        if (aItem.getName().endsWith(".datatype")) {
                            pItem.setType(aItem.getValue());
                        }
                        if (isDescription(aItem)) {
                            pItem.setDescription(aItem.getValue());
                        }
                        if (aItem.getName().endsWith(".title")) {
                            pItem.setValue(aItem.getValue());
                        }
                    }
                }
            }
            //see if we have a tasktype
            if (aItem.getName().equals("tasktype")) {
                tasktype = aItem.getValue();
            }
        }


        /* if no signature, then an empty RTO will be returned, no error message
         if(tasktype==null){
         throw new NotFoundException("Workflow "+workflowId+" has no signature!");
         }*/

        SignatureRTO signature = new SignatureRTO(tasktype, inports, outports);
        return signature;
    }

    /*
     * note that signature equivalence is not checked
     */
    @Override
    public int createWorkflow(int userId, WorkflowRTO workflow) throws UnauthorizedException, ForbiddenException, ItemExistsException {
        checkUserAuthorized(userId);
        if (af.canUserCreateApplications(userId) != null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserCreateApplications(userId));
        }

        // creating application
        ApplicationTO workflowTO = null;
        try {
            workflowTO = af.createApp(
                    workflow.getName(),
                    workflow.getDescription(),
                    workflow.getCreated(),
                    workflow.getModified(),
                    workflow.getAccessRights().getGroupId(),
                    workflow.getAccessRights().isGroupRead(),
                    workflow.getAccessRights().isOthersRead(),
                    workflow.getAccessRights().isGroupDownload(),
                    workflow.getAccessRights().isOthersDownload(),
                    workflow.getAccessRights().isGroupModify(),
                    workflow.getAccessRights().isPublished());
        } catch (EntityAlreadyExistsException e) {
            throw new ItemExistsException(e.getMessage());
        } catch (AuthorizationException e) {
            throw new UnauthorizedException(e.getMessage());
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        //creating app attributes
        try {

            //input ports
            List<PortRTO> pList = workflow.getSignature().getInputPorts();
            Iterator<PortRTO> pIter = pList.iterator();
            PortRTO pItem;
            while (pIter.hasNext()) {
                pItem = pIter.next();
                af.createAppAttr(workflowTO.getId(), "inports." + pItem.getName() + ".title", pItem.getValue());
                af.createAppAttr(workflowTO.getId(), "inports." + pItem.getName() + ".datatype", pItem.getType());
                af.createAppAttr(workflowTO.getId(), "inports." + pItem.getName() + ".description", pItem.getDescription());
            }

            //output ports
            pList = workflow.getSignature().getOutputPorts();
            pIter = pList.iterator();
            while (pIter.hasNext()) {
                pItem = pIter.next();
                af.createAppAttr(workflowTO.getId(), "outports." + pItem.getName() + ".title", pItem.getValue());
                af.createAppAttr(workflowTO.getId(), "outports." + pItem.getName() + ".datatype", pItem.getType());
                af.createAppAttr(workflowTO.getId(), "outports." + pItem.getName() + ".description", pItem.getDescription());
            }

            //configurations
            List<ConfigurationRTO> cList = workflow.getConfigurations();
            Iterator<ConfigurationRTO> cIter = cList.iterator();
            ConfigurationRTO cItem;

            List<ConfigurationNodeRTO> cnList;
            Iterator<ConfigurationNodeRTO> cnIter;
            ConfigurationNodeRTO cnItem;
            String cId;

            List<BundleFileRTO> bList;
            Iterator<BundleFileRTO> bIter;
            BundleFileRTO bItem;
            List<String> fileNames = new ArrayList<String>();

            while (cIter.hasNext()) {
                // adding description
                cItem = cIter.next();
                cId = "conf0000".substring(0, 8 - Integer.toString(cItem.getId()).length()) + cItem.getId();
                if (cItem.getDescription() == null) {
                    cItem.setDescription("");
                }
                af.createAppAttr(workflowTO.getId(), "configurations." + cId + ".description", cItem.getDescription());
                cnList = cItem.getConfigurationNodes();
                cnIter = cnList.iterator();
                while (cnIter.hasNext()) {
                    // adding port configurations
                    cnItem = cnIter.next();
                    if (cnItem.getValue() == null) {
                        cnItem.setValue("");
                    }
                    af.createAppAttr(workflowTO.getId(), "configurations." + cId + "." + cnItem.getSubjectId(), cnItem.getValue());
                }

                // checking files
                bList = cItem.getFiles();
                bIter = bList.iterator();
                while (bIter.hasNext()) {
                    // adding port configurations
                    bItem = bIter.next();
                    if (!fileNames.contains(bItem.getTitle())) {
                        af.uploadAppFile(workflowTO.getId(), bItem.getTitle(), new FileInputStream(bItem.getFile()));
                        fileNames.add(bItem.getTitle());
                    }
                }

            }


            //simple attributes
            if (workflow.getApplication() != null) {
                af.createAppAttr(workflowTO.getId(), "application", workflow.getApplication());
            }
            if (workflow.getDomain() != null) {
                af.createAppAttr(workflowTO.getId(), "domain", workflow.getDomain());
            }
            if (workflow.getKeywords() != null) {
                af.createAppAttr(workflowTO.getId(), "keywords", workflow.getKeywords());
            }
            if (workflow.getSignature() != null && workflow.getSignature().getTaskType() != null) {
                af.createAppAttr(workflowTO.getId(), "tasktype", workflow.getSignature().getTaskType());
            }

            //adding further files
            bList = workflow.getFiles();
            bIter = bList.iterator();
            while (bIter.hasNext()) {
                // adding port configurations
                bItem = bIter.next();
                if (!fileNames.contains(bItem.getTitle())) {
                    System.out.println("adding: file" + bItem.getTitle());
                    af.uploadAppFile(workflowTO.getId(), bItem.getTitle(), new FileInputStream(bItem.getFile()));
                    fileNames.add(bItem.getTitle());
                }
            }


        } catch (Exception e) {
            throw new ForbiddenException(e.toString() + ": " + e.getMessage());
        }

        if (workflowTO != null) {
            return workflowTO.getId();
        }
        return -1;
    }

    @Override
    public int createImplementation(int userId, ImplementationRTO implementation) throws UnauthorizedException, ForbiddenException, NotFoundException, ItemExistsException {
        checkUserAuthorized(userId);
        if (af.canUserCreateImps(userId, implementation.getWorkflowId()) != null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserCreateImps(userId, implementation.getWorkflowId()));
        }

        // creating implementation
        ImplementationTO implementationTO = null;
        try {
            implementationTO = af.createImp(
                    implementation.getWorkflowId(),
                    implementation.getEngineId(),
                    implementation.getVersion(),
                    implementation.getCreated(),
                    implementation.getModified());
        } catch (EntityAlreadyExistsException e) {
            throw new ItemExistsException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (AuthorizationException e) {
            throw new UnauthorizedException(e.getMessage());
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        //System.out.println("impId: "+implementationTO.getId());


        //creating imp attributes
        try {

            //simple attributes
            af.createImpAttr(implementationTO.getId(), "title", implementation.getTitle());
            af.createImpAttr(implementationTO.getId(), "uuid", implementation.getUuid());
            af.createImpAttr(implementationTO.getId(), "description", implementation.getDescription());
            af.createImpAttr(implementationTO.getId(), "definition", implementation.getDefinition());
            af.createImpAttr(implementationTO.getId(), "graph", implementation.getGraph());
            af.createImpAttr(implementationTO.getId(), "language", implementation.getLanguage());
            af.createImpAttr(implementationTO.getId(), "rights", implementation.getRights());
            af.createImpAttr(implementationTO.getId(), "licence", implementation.getLicence());
            af.createImpAttr(implementationTO.getId(), "keywords", implementation.getKeywords());

            //dependencies
            List<DependencyRTO> dList = implementation.getDependencies();
            Iterator<DependencyRTO> dIter = dList.iterator();
            DependencyRTO dItem;
            while (dIter.hasNext()) {
                dItem = dIter.next();
                af.createImpAttr(implementationTO.getId(), "dependencies." + dItem.getName() + ".description", dItem.getDescription());
                af.createImpAttr(implementationTO.getId(), "dependencies." + dItem.getName() + ".title", dItem.getTitle());
                af.createImpAttr(implementationTO.getId(), "dependencies." + dItem.getName() + ".type", dItem.getType());
            }

            //configurations
            List<ConfigurationRTO> cList = implementation.getConfigurations();
            Iterator<ConfigurationRTO> cIter = cList.iterator();
            ConfigurationRTO cItem;

            List<ConfigurationNodeRTO> cnList;
            Iterator<ConfigurationNodeRTO> cnIter;
            ConfigurationNodeRTO cnItem;
            String cId;

            List<BundleFileRTO> bList;
            Iterator<BundleFileRTO> bIter;
            BundleFileRTO bItem;
            List<String> fileNames = new ArrayList<String>();

            while (cIter.hasNext()) {
                // adding description
                cItem = cIter.next();
                cId = "conf0000".substring(0, 8 - Integer.toString(cItem.getId()).length()) + cItem.getId();
                if (cItem.getDescription() == null) {
                    cItem.setDescription("");
                }
                af.createImpAttr(implementationTO.getId(), "configurations." + cId + ".description", cItem.getDescription());
                cnList = cItem.getConfigurationNodes();
                cnIter = cnList.iterator();
                while (cnIter.hasNext()) {
                    // adding port configurations
                    cnItem = cnIter.next();
                    if (cnItem.getValue() == null) {
                        cnItem.setValue("");
                    }
                    af.createImpAttr(implementationTO.getId(), "configurations." + cId + "." + cnItem.getSubjectId(), cnItem.getValue());
                }

                // checking files
                bList = cItem.getFiles();
                bIter = bList.iterator();
                while (bIter.hasNext()) {
                    // adding port configurations
                    bItem = bIter.next();
                    if (!fileNames.contains(bItem.getTitle())) {
                        af.uploadImpFile(implementationTO.getId(), bItem.getTitle(), new FileInputStream(bItem.getFile()));
                        fileNames.add(bItem.getTitle());
                    }
                }

            }

            //adding further files
            bList = implementation.getFiles();
            bIter = bList.iterator();
            while (bIter.hasNext()) {
                // adding port configurations
                bItem = bIter.next();
                if (!fileNames.contains(bItem.getTitle())) {
                    af.uploadImpFile(implementationTO.getId(), bItem.getTitle(), new FileInputStream(bItem.getFile()));
                    fileNames.add(bItem.getTitle());
                }
            }

            return implementationTO.getId();
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }
    }

    @Override
    public int createConfiguration(int userId, int workflowId, ConfigurationRTO configuration) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        if (af.canUserCreateAppAttrs(userId, workflowId) != null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserCreateAppAttrs(userId, workflowId));
        }

        try {
            //get max conf id
            List<ConfigurationSummaryRTO> csList = listConfigurations(userId, workflowId);
            Iterator<ConfigurationSummaryRTO> csIter = csList.iterator();
            int candidateId = 1;
            int item;
            while (csIter.hasNext()) {
                item = csIter.next().getId();
                if (item >= candidateId) {
                    candidateId = item + 1;
                }
            }

            configuration.setId(candidateId);

            System.out.println("confId: " + candidateId);

            //add configuration nodes
            List<ConfigurationNodeRTO> cnList;
            Iterator<ConfigurationNodeRTO> cnIter;
            ConfigurationNodeRTO cnItem;
            String cId;

            List<BundleFileRTO> bList;
            Iterator<BundleFileRTO> bIter;
            BundleFileRTO bItem;
            List<String> fileNames = getFileNamesOfWf(workflowId);

            // adding description
            cId = "conf0000".substring(0, 8 - Integer.toString(configuration.getId()).length()) + configuration.getId();
            if (configuration.getDescription() == null) {
                configuration.setDescription("");
            }
            af.createAppAttr(workflowId, "configurations." + cId + ".description", configuration.getDescription());
            cnList = configuration.getConfigurationNodes();
            cnIter = cnList.iterator();
            while (cnIter.hasNext()) {
                // adding port configurations
                cnItem = cnIter.next();
                if (cnItem.getValue() == null) {
                    cnItem.setValue("");
                }
                af.createAppAttr(workflowId, "configurations." + cId + "." + cnItem.getSubjectId(), cnItem.getValue());
            }

            // checking files
            bList = configuration.getFiles();
            bIter = bList.iterator();
            while (bIter.hasNext()) {
                // adding port configurations
                bItem = bIter.next();
                if (!fileNames.contains(bItem.getTitle())) {
                    af.uploadAppFile(workflowId, bItem.getTitle(), new FileInputStream(bItem.getFile()));
                    fileNames.add(bItem.getTitle());
                }
            }


        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        return configuration.getId();

    }

    @Override
    public int updateImplementation(int userId, ImplementationRTO implementation) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        if (af.canUserDeleteImp(userId, implementation.getWorkflowId()) != null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserDeleteImp(userId, implementation.getWorkflowId()));
        }
        if (af.canUserCreateImps(userId, implementation.getWorkflowId()) != null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserCreateImps(userId, implementation.getWorkflowId()));
        }
        try {
            af.deleteImp(implementation.getId());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (AuthorizationException e) {
            throw new UnauthorizedException(e.getMessage());
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }
        int newId = -1;
        try {
            newId = createImplementation(userId, implementation);
        } catch (ItemExistsException e) {
            throw new ForbiddenException(e.getMessage());
        }
        return newId;
    }

    @Override
    public WorkflowRTO getWorkflow(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        checkUserCanReadApplication(userId, workflowId);
        ApplicationTO appTO = af.loadApplication(workflowId);
        if (appTO == null) {
            throw new NotFoundException("Workflow " + workflowId + " not found!");
        }

        List<AttributeTO> attributes = af.listAttributesOfApplication(workflowId);
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;
        String application = "";
        String domain = "";
        String keywords = "";

        while (aIter.hasNext()) {
            aItem = aIter.next();

            //see if we have a tasktype
            if (aItem.getName().equals("application")) {
                application = aItem.getValue();
            }
            if (aItem.getName().equals("domain")) {
                domain = aItem.getValue();
            }
            if (aItem.getName().equals("keywords")) {
                keywords = aItem.getValue();
            }
        }

        SignatureRTO signature = getSignature(userId, workflowId);

        AccessRightsRTO accessRights = new AccessRightsRTO(
                af.loadUser(appTO.getOwnerLoginName()).getId(),
                af.getGroup(appTO.getGroupName()).getId(),
                appTO.getGroupRead(),
                appTO.getGroupDownload(),
                appTO.getGroupModify(),
                appTO.getOthersRead(),
                appTO.getOthersDownload(),
                appTO.getPublished());

        //getting non-configuration files
        List<String> wfFiles = getFileNamesOfWf(workflowId);
        List<String> wfConfValues = getWfConfValues(workflowId);
        List<BundleFileRTO> bundleFiles = new ArrayList<BundleFileRTO>();
        String fileName;
        Iterator<String> fIter = wfFiles.iterator();
        while (fIter.hasNext()) {
            fileName = fIter.next();
            // only insert if not config value
            if (!wfConfValues.contains(fileName)) {
                bundleFiles.add(getWfFile(workflowId, fileName));
            }
        }

        List<ConfigurationRTO> configurations = new ArrayList<ConfigurationRTO>();

        try {
            configurations.addAll(getConfigurations(userId, workflowId, null));
        } catch (Exception e) {
        }

        WorkflowRTO workflow = new WorkflowRTO(
                workflowId,
                appTO.getName(),
                accessRights,
                new Timestamp(appTO.getCreated().getTime()),
                new Timestamp(appTO.getUpdated().getTime()),
                application,
                appTO.getDescription(),
                domain,
                keywords,
                signature,
                configurations,
                bundleFiles);
        return workflow;
    }

    @Override
    public List<ConfigurationRTO> getConfigurations(int userId, int workflowId, List<Integer> configurationIds) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        checkUserCanReadApplication(userId, workflowId);

        List<ConfigurationRTO> confs = new ArrayList<ConfigurationRTO>();
        List<Integer> confIDs = new ArrayList<Integer>();
        List<AttributeTO> attributes = af.listAttributesOfApplication(workflowId);
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;
        String confId;
        ConfigurationNodeRTO newNode;


        if (attributes.isEmpty()) {
            throw new NotFoundException("Workflow " + workflowId + " not found or has no configurations.");
        }

        while (aIter.hasNext()) {
            aItem = aIter.next();
            //System.out.println("attrs: "+aItem.getName());
            //find configurations
            if (aItem.getName().startsWith("configurations.")
                    && aItem.getName().split("\\.").length > 2) {
                confId = aItem.getName().split("\\.")[1];
                //System.out.println(aItem.getName());
                try {
                    //map int to string for conf id
                    int id = Integer.parseInt(confId.replaceFirst("^.*\\D", ""));

                    //if the conf was not inserted before, then add
                    if (!confIDs.contains(id)
                            && (configurationIds == null
                            || configurationIds.isEmpty()
                            || configurationIds.contains(id))) {
                        confIDs.add(id);
                        confs.add(new ConfigurationRTO(id, ConfigurationRTO.ConfigurationType.PORT));
                    }

                    //see if have a description for the id. If, so insert it
                    Iterator<ConfigurationRTO> cIter = confs.iterator();
                    ConfigurationRTO cItem;
                    while (cIter.hasNext()) {
                        cItem = cIter.next();
                        if (cItem.getId() == id) {
                            // if description then add description to conf
                            if (isDescription(aItem)) {
                                cItem.setDescription(aItem.getValue());
                                // if not description then port, so insert a new port
                            } else {
                                newNode = new ConfigurationNodeRTO(aItem.getName().split("\\.")[2], aItem.getValue(), "INLINE_REF");

                                // see if there is a file with the same filename as the value
                                if (getFileNamesOfWf(workflowId).contains(aItem.getValue())) {
                                    BundleFileRTO bFile = getWfFile(workflowId, aItem.getValue());
                                    if (bFile != null) {
                                        newNode.setType("FILE_REF");
                                        bFile.setFileType(BundleFileRTO.FileType.INPUT_FILE);
                                        cItem.getFiles().add(bFile);
                                    }
                                }

                                cItem.getConfigurationNodes().add(newNode);
                            }
                        }
                    }

                } catch (Exception e) {/*if cannot get integer, then don't insert the object*/

                }
            }
        }

        if (confs.isEmpty()) {
            throw new NotFoundException("Workflow " + workflowId + " has no configurations!");
        }
        return confs;
    }

    @Override
    public List<ImplementationRTO> getImplementations(int userId, List<Integer> implementationIds) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkUserAuthorized(userId);
        List<ImplementationRTO> imsList = new ArrayList<ImplementationRTO>();
        Iterator<Integer> iIdIter = implementationIds.iterator();
        Integer iId;
        while (iIdIter.hasNext()) {
            iId = iIdIter.next();
            if (af.canUserReadImp(userId, iId) != null) {
                throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserReadImp(userId, iId));
            }
            imsList.add(getImplementationRTO(iId));
        }
        if (imsList.isEmpty()) {
            throw new NotFoundException("No implementation exists with any of the given ids!");
        }
        return imsList;
    }

    @Override
    public EngineRTO getEngine(int userId, String name, String version) throws UnauthorizedException, NotFoundException {
        checkUserAuthorized(userId);
        PlatformTO platform = af.loadPlatform(name, version);
        if (platform != null) {
            EngineRTO engine = new EngineRTO(
                    platform.getId(),
                    platform.getName(),
                    platform.getVersion(),
                    platform.getShortDescription());
            return engine;
        } else {
            throw new NotFoundException("No engine exists with the given name and version: " + name + "(" + version + ")");
        }
    }

    @Override
    public EngineRTO getEngine(int userId, int engineId) throws UnauthorizedException, NotFoundException {
        checkUserAuthorized(userId);
        PlatformTO platform = af.getPlatform(engineId);
        if (platform != null) {
            EngineRTO engine = new EngineRTO(
                    platform.getId(),
                    platform.getName(),
                    platform.getVersion(),
                    platform.getShortDescription());
            return engine;
        } else {
            throw new NotFoundException("No engine exists with the given id: " + engineId);
        }
    }

    @Override
    public List<EngineRTO> getEngines(int userId, List<Integer> engineIds) throws UnauthorizedException, NotFoundException {
        checkUserAuthorized(userId);
        List<PlatformTO> platforms = af.listPlatforms();
        /*
         if(platforms==null) {
         System.out.println("platforms is null");
         }else{
         System.out.println("platform list has "+platforms.size()+" items.");
         }*/
        Iterator<PlatformTO> iter = platforms.iterator();
        PlatformTO item;
        List<EngineRTO> engList = new ArrayList<EngineRTO>();
        EngineRTO engine;
        while (iter.hasNext()) {
            item = iter.next();
            if (engineIds == null
                    || engineIds.contains(item.getId())) {
                //System.out.println("adding element");
                engine = new EngineRTO(
                        item.getId(),
                        item.getName(),
                        item.getVersion(),
                        item.getDescription());
                engList.add(engine);
            }

        }
        if (engList.isEmpty()) {
            throw new NotFoundException("No engine exists with any of the given ids!");
        }
        return engList;
    }

    @Override
    public EngineRTO createEngine(String name, String version) throws ForbiddenException, AuthorizationException{
        EngineRTO engine;
        try {
            PlatformTO item = af.createPlatform(name, version, "");
            engine = new EngineRTO(
                        item.getId(),
                        item.getName(),
                        item.getVersion(),
                        item.getDescription());
        } catch (EntityAlreadyExistsException ex) {
            Logger.getLogger(ToolkitImplementation.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForbiddenException(ex);
        } catch (ValidationFailedException ex) {
            Logger.getLogger(ToolkitImplementation.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForbiddenException(ex);
        } catch (AuthorizationException ex) {
            Logger.getLogger(ToolkitImplementation.class.getName()).log(Level.SEVERE, null, ex);
            throw new AuthorizationException(ex.getMessage());
        }
        return engine;
    }

    protected ImplementationRTO getImplementationRTO(int id) throws ForbiddenException {

        ImplementationTO impTO = af.loadImplementation(id);

        //get data from attribute table
        List<AttributeTO> attributes = af.listAttributesOfImplementation(id);
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;
        String title = "";
        String uuid = "";
        String description = "";
        String definition = "";
        String graph = "";
        String language = "";
        String rights = "";
        String licence = "";
        String keywords = "";

        List<String> depIDs = new ArrayList<String>();
        List<DependencyRTO> deps = new ArrayList<DependencyRTO>();
        List<ConfigurationRTO> confs = new ArrayList<ConfigurationRTO>();
        List<Integer> confIDs = new ArrayList<Integer>();
        String depId;
        String confId;
        ConfigurationNodeRTO newNode;

        while (aIter.hasNext()) {
            aItem = aIter.next();

            //see if we have a dependency
            if (aItem.getName().startsWith("dependencies.")
                    && aItem.getName().split("\\.").length > 2) {
                //get id
                depId = aItem.getName().split("\\.")[1];

                //if the dependency was not inserted before, then add
                if (!depIDs.contains(depId)) {
                    depIDs.add(depId);
                    deps.add(new DependencyRTO(depId, "", "", ""));
                }

                //insert value
                Iterator<DependencyRTO> dIter = deps.iterator();
                DependencyRTO dItem;
                while (dIter.hasNext()) {
                    dItem = dIter.next();
                    if (dItem.getName().equals(depId)) {
                        if (aItem.getName().endsWith(".type")) {
                            dItem.setType(aItem.getValue());
                        }
                        if (isDescription(aItem)) {
                            dItem.setDescription(aItem.getValue());
                        }
                        if (aItem.getName().endsWith(".title")) {
                            dItem.setTitle(aItem.getValue());
                        }
                    }
                }
            }

            // see if we have a configuration
            if (aItem.getName().startsWith("configurations.")
                    && aItem.getName().split("\\.").length > 2) {
                confId = aItem.getName().split("\\.")[1];
                //System.out.println(aItem.getName());
                try {
                    //map int to string for conf id
                    int cId = Integer.parseInt(confId.replaceFirst("^.*\\D", ""));

                    //if the conf was not inserted before, then add
                    if (!confIDs.contains(cId)) {
                        confIDs.add(cId);
                        confs.add(new ConfigurationRTO(cId, ConfigurationRTO.ConfigurationType.DEPENDENCY));
                    }

                    Iterator<ConfigurationRTO> cIter = confs.iterator();
                    ConfigurationRTO cItem;
                    while (cIter.hasNext()) {
                        cItem = cIter.next();
                        if (cItem.getId() == cId) {
                            // if description then add description to conf
                            if (isDescription(aItem)) {
                                cItem.setDescription(aItem.getValue());
                                // if not description then dependency, so insert a new dep
                            } else {
                                newNode = new ConfigurationNodeRTO(aItem.getName().split("\\.")[2], aItem.getValue(), "INLINE_REF");

                                // see if there is a file with the same filename as the value
                                if (getFileNamesOfImp(id).contains(aItem.getValue())) {
                                    BundleFileRTO bFile = getImpFile(id, aItem.getValue());
                                    if (bFile != null) {
                                        cItem.getFiles().add(bFile);
                                        bFile.setFileType(BundleFileRTO.FileType.DEPENDENCY_FILE);
                                        newNode.setType("FILE_REF");
                                    }
                                }
                                cItem.getConfigurationNodes().add(newNode);
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("if cannot get integer, then don't insert the object");
                }
            }


            //see if we have anything else
            if (aItem.getName().equals("title")) {
                title = aItem.getValue();
            }
            if (aItem.getName().equals("uuid")) {
                uuid = aItem.getValue();
            }
            if (aItem.getName().equals("description")) {
                description = aItem.getValue();
            }
            if (aItem.getName().equals("definition")) {
                definition = aItem.getValue();
            }
            if (aItem.getName().equals("graph")) {
                graph = aItem.getValue();
            }
            if (aItem.getName().equals("language")) {
                language = aItem.getValue();
            }
            if (aItem.getName().equals("rights")) {
                rights = aItem.getValue();
            }
            if (aItem.getName().equals("licence")) {
                licence = aItem.getValue();
            }
            if (aItem.getName().equals("keywords")) {
                keywords = aItem.getValue();
            }
        }

        //getting non-configuration files
        List<String> impFiles = getFileNamesOfImp(id);
        List<String> impConfValues = getWfConfValues(id);
        List<BundleFileRTO> bundleFiles = new ArrayList<BundleFileRTO>();
        String fileName;
        Iterator<String> fIter = impFiles.iterator();
        while (fIter.hasNext()) {
            fileName = fIter.next();
            insertFileIfNotConfigValue(impConfValues, fileName, bundleFiles, id);
        }


        ImplementationRTO impRTO = new ImplementationRTO(
                id,
                impTO.getAppId(),
                impTO.getVersion(),
                af.getPlatform(impTO.getPlatformName(), impTO.getPlatformVersion()).getId(),
                ImplementationRTO.Status.fromString(impTO.getStatusFriendlyName()),
                new Timestamp(impTO.getCreated().getTime()),
                new Timestamp(impTO.getUpdated().getTime()),
                title,
                uuid,
                description,
                definition,
                graph,
                language,
                rights,
                licence,
                keywords,
                deps,
                confs,
                bundleFiles);

        return impRTO;
    }

    protected List<String> getFileNamesOfWf(int workflowId) {
        List<String> fileNames = new ArrayList<String>();
        List<Integer> appIdList = new ArrayList<Integer>();
        appIdList.add(workflowId);
        List<AppFileTO> appFiles = af.getFilesForApps(appIdList);
        Iterator<AppFileTO> afIter = appFiles.iterator();
        while (afIter.hasNext()) {
            fileNames.add(afIter.next().getPathName());
        }
        return fileNames;
    }

    protected List<String> getFileNamesOfImp(int implementationId) {
        List<String> fileNames = new ArrayList<String>();
        List<Integer> impIdList = new ArrayList<Integer>();
        impIdList.add(implementationId);
        List<ImpFileTO> impFiles = af.getFilesForImps(impIdList);
        Iterator<ImpFileTO> ifIter = impFiles.iterator();
        while (ifIter.hasNext()) {
            fileNames.add(ifIter.next().getPathName());
        }
        return fileNames;
    }

    protected List<String> getWfConfValues(int workflowId) {
        List<String> confValues = new ArrayList<String>();
        List<AttributeTO> attributes = af.listAttributesOfApplication(workflowId);
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;

        while (aIter.hasNext()) {
            aItem = aIter.next();

            //find configurations
            if (aItem.getName().startsWith("configurations.")
                    && !isDescription(aItem)) {
                confValues.add(aItem.getValue());
            }
        }
        return confValues;
    }

    protected List<String> getImpConfValues(int implementationId) {
        List<String> confValues = new ArrayList<String>();
        List<AttributeTO> attributes = af.listAttributesOfImplementation(implementationId);
        Iterator<AttributeTO> aIter = attributes.iterator();
        AttributeTO aItem;

        while (aIter.hasNext()) {
            aItem = aIter.next();

            //find configurations
            if (aItem.getName().startsWith("configurations.")
                    && !isDescription(aItem)) {
                confValues.add(aItem.getValue());
            }
        }
        return confValues;
    }

    protected BundleFileRTO getWfFile(int appId, String fileName) {
        BundleFileRTO bundleFile = new BundleFileRTO(fileName);
        try {
            ImpFile f = af.getAppFile(appId, fileName);
            InputStream in = f.getStream();
            File file = Locations.getTempFile("tmp");
            FileImageOutputStream out = new FileImageOutputStream(file);

            byte[] byteArray = new byte[1024];
            int len = in.read(byteArray);
            while (len != -1) {
                out.write(byteArray, 0, len);
                len = in.read(byteArray);
            }
            out.close();
            in.close();
            bundleFile.setFile(file);
            return bundleFile;
        } catch (Exception e) {
            System.out.println("Cannot read file: " + e.getClass() + ", " + e.getMessage() + ", " + e.getStackTrace());
            // throw a file read exception
        }
        return null;
    }

    protected BundleFileRTO getImpFile(int impId, String fileName) throws ForbiddenException {
        BundleFileRTO bundleFile = new BundleFileRTO(fileName);
        try {
            ImpFile f = af.getImpFile(impId, fileName);
            InputStream in = f.getStream();
            File file = Locations.getTempFile("tmp");
            FileImageOutputStream out = new FileImageOutputStream(file);

            byte[] byteArray = new byte[1024];
            int len = in.read(byteArray);
            while (len != -1) {
                out.write(byteArray, 0, len);
                len = in.read(byteArray);
            }
            out.close();
            in.close();
            bundleFile.setFile(file);
            return bundleFile;
        } catch (AuthorizationException e) {
            throw new ForbiddenException("The requested action is forbidden to user: " + e.getMessage() + " (impid=\'" + impId + "\')");
        } catch (Exception e) {
            System.out.println("Cannot read file: " + e.getClass() + ", " + e.getMessage() + ", " + e.getStackTrace());
        }
        return null;
    }

    @Override
    public List<String> getWFAttributeValues(int userId, String attrName) throws UnauthorizedException, NotFoundException {
        checkUserAuthorized(userId);
        List<AttributeTO> aList = af.getAppAttributesByKey(attrName);
        List<String> attrValueList = new ArrayList<String>();
        for (AttributeTO attr : aList) {
            if (attr != null
                    && !attr.getValue().equals("")
                    && !attrValueList.contains(attr.getValue())) {
                attrValueList.add(attr.getValue());
            }
        }
        if (attrValueList.isEmpty()) {
            throw new NotFoundException("No attributes found with the given name: " + attrName);
        }
        return attrValueList;
    }

    @Override
    public List<GroupRTO> getUsersGroups(int userId) throws UnauthorizedException, NotFoundException {
        checkUserAuthorized(userId);
        List<GroupTO> gList = af.listGroupsOfUser(userId);
        List<GroupRTO> gRTOList = new ArrayList<GroupRTO>();
        for (GroupTO gItem : gList) {
            if (gItem != null) {
                gRTOList.add(new GroupRTO(gItem.getId(), gItem.getName()));
            }
        }
        if (gRTOList.isEmpty()) {
            throw new NotFoundException("No groups found for the given user: " + userId);
        }
        return gRTOList;
    }

    private void checkUserAuthorized(int userId) throws UnauthorizedException {
        if (userId == af.loadUser(RepoUser.GUEST_USER).getId()) {
            return;
        }

        if (!af.isAuthorizedUser()) {
            throw new UnauthorizedException("user not authorized!");
        }
    }

    private void checkUserCanReadApplication(int userId, int workflowId) throws ForbiddenException {
        if (isGuest(userId)) {
            check_application_exists_and_world_downloadable(userId, workflowId);
        } else {
            check_registered_user_can_download_application(userId, workflowId);
        }
    }

    private boolean isDescription(AttributeTO aItem) {
        return aItem.getName().endsWith(".description");
    }

    private void insertFileIfNotConfigValue(List<String> impConfValues, String fileName, List<BundleFileRTO> bundleFiles, int id) throws ForbiddenException {
        // only insert if not config value
        if (!impConfValues.contains(fileName)) {
            bundleFiles.add(getImpFile(id, fileName));
        }
    }

    private boolean isGuest(int userId) {
        return af.loadUser("guest").getId().equals(userId);
    }

    private void check_application_exists_and_world_downloadable(int userId, int workflowId) throws ForbiddenException {
        Boolean worldDownloadable = af.canUserReadDownloadApplication(userId, workflowId);

        if (worldDownloadable == null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": "
                    + "application does not exist (id=\'" + workflowId + "\')");
        } else if (!worldDownloadable) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": "
                    + "you are not allowed to view and/or download this application (id=\'" + workflowId + "\')");
        }
    }

    private void check_registered_user_can_download_application(int userId, int workflowId) throws ForbiddenException {
        if (af.canUserReadApplication(userId, workflowId) != null) {
            throw new ForbiddenException("The requested action is forbidden to user " + userId + ": " + af.canUserReadApplication(userId, workflowId));
        }
    }
}