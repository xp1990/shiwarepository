package org.shiwa.repository.toolkit.servlet;

import java.io.*;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shiwa.desktop.data.description.ConcreteBundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.*;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.resource.ConceptResource;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.validation.SignatureResult;
import org.shiwa.desktop.data.description.workflow.Application;
import org.shiwa.desktop.data.description.workflow.Domain;
import org.shiwa.desktop.data.description.workflow.Tasktype;
import org.shiwa.desktop.data.util.Base64;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.properties.DesktopProperties;
import org.shiwa.desktop.data.util.properties.Locations;
import org.shiwa.repository.toolkit.ToolkitImplementation;
import org.shiwa.repository.toolkit.ToolkitInterface;
import org.shiwa.repository.toolkit.exceptions.ForbiddenException;
import org.shiwa.repository.toolkit.exceptions.ItemExistsException;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;
import org.shiwa.repository.toolkit.exceptions.UnauthorizedException;
import org.shiwa.repository.toolkit.transferobjects.*;
import org.shiwa.repository.toolkit.util.RepoUtil;
import uk.ac.wmin.edgi.repository.common.AuthorizationException;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author zsolt
 */
@WebServlet(name="UploadBundle", urlPatterns={"/uploadbundle/*"})
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class UploadBundle extends HttpServlet {

    @EJB ApplicationFacadeLocal af;

    Boolean force = false;

    static ToolkitInterface repo;
    int userId=-1;

    private PrintWriter pw;

    @Override
    public void init() throws ServletException {
        super.init();
        //repo = Test.getRepo();
        repo = new ToolkitImplementation(af);
        DesktopProperties.setProperty(DesktopProperties.TEMP_LOCATION, RepoUtil.BUNDLE_TEMP_LOCATION);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        userId = af.loadUser(request.getRemoteUser()).getId();

        File log = File.createTempFile("upload-execution",".log");
        pw = new PrintWriter(new FileOutputStream(log));
        force = false;

        String path = request.getPathInfo();
        String query = request.getQueryString();

        if (query != null) {
            Map<String, String> queryMap = RepoUtil.getQuery(query);
            if (queryMap.containsKey("force")) {
                force = Boolean.valueOf(queryMap.get("force"));
            }
        }

        Integer workflowId = null;
        Integer implementationId = null;

        if (path != null) {
            String[] cs = path.split("/");
            List<String> components = new ArrayList<String>();

            for (String component : cs) {
                if (component.length() > 0) {
                    components.add(component);
                }
            }

            for (int i = 0; i < components.size(); i++) {
                String component = components.get(i);
                if (i == 0) {
                    workflowId = Integer.parseInt(component);
                } else if (i == 1) {
                    implementationId = Integer.parseInt(component);
                }
            }
        }

        String id = UUID.randomUUID().toString();

        File out = File.createTempFile("tmp", id);

        String status = "";
        int resposneValue = 201;

        try {
            status = RepoUtil.getServerPath(request) + processBundle(request, out, workflowId, implementationId);
            resposneValue = 201;
        } catch (ForbiddenException e) {
            e.printStackTrace(pw);
            status = e.getMessage();
            resposneValue = 403;
        } catch (ItemExistsException e) {
            e.printStackTrace(pw);
            status = e.getMessage();
            resposneValue = 409;
        } catch (NotFoundException e) {
            e.printStackTrace(pw);
            status = e.getMessage();
            resposneValue = 404;
        } catch (IOException e) {
            e.printStackTrace(pw);
            status = e.getMessage();
            resposneValue = 401;
        } catch (UnauthorizedException e) {
            e.printStackTrace(pw);
            status = e.getMessage();
            resposneValue = 401;
        } catch (Throwable e) {
            e.printStackTrace(pw);
            status = e.getMessage();
            resposneValue = 401;
        } finally {
            out.delete();
        }

        pw.println(status);
        pw.close();

        if (resposneValue >= 400) {
            StringBuilder fileData = new StringBuilder(1000);
            BufferedReader reader = new BufferedReader(new FileReader(log));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();

            response.sendError(resposneValue, Base64.encode(fileData.toString().getBytes()));
        } else {
            InputStream in = new FileInputStream(log);
            response.setStatus(resposneValue);

            response.setContentType("application/xml");
            response.addHeader("Content-Disposition", "attachment; filename="+log.getName());
            response.addHeader("Cache-Control", "no-cache");
            RepoUtil.write(in, response.getOutputStream());
        }
    }

    private String processBundle(HttpServletRequest request, File out, Integer workflowId, Integer implementationId) throws UnauthorizedException, NotFoundException, IOException, ItemExistsException, ForbiddenException {
        RepoUtil.write(request.getInputStream(), new FileOutputStream(out));
        pw.println("Writing Bundle to temp file: " + out.getName());
        ConcreteBundle bundle = new ConcreteBundle(out);
        pw.println("Bundle read... Passing to Workflow Controller");
        //WorkflowController controller = new WorkflowController(bundle);

        WorkflowRTO repositoryWorkflow = null;
        SignatureRTO signature = null;
        ImplementationRTO repositoryImplementation = null;

        pw.println("Checking for Abstract Workflow...");
        if (bundle.getPrimaryAbstractTask() != null && bundle.getPrimaryAbstractTask() instanceof AbstractWorkflow) {
            AbstractWorkflow bundleWorkflow = (AbstractWorkflow) bundle.getPrimaryAbstractTask();
            pw.println("Reading Abstract Workflow details...");
            repositoryWorkflow = setWorkflow(repositoryWorkflow, bundleWorkflow);
        }

        pw.println("Checking for Workflow Implementation...");
        if (bundle.getPrimaryConcreteTask() != null && bundle.getPrimaryConcreteTask() instanceof WorkflowImplementation) {
            WorkflowImplementation bundleImplementation = (WorkflowImplementation) bundle.getPrimaryConcreteTask();
            pw.println("Reading Workflow Implementation details...");
            repositoryImplementation = setImplementation(repositoryImplementation, bundleImplementation);
            signature = setSignature(signature, bundleImplementation);

            pw.println("Packaging CTOs...");
            for (AggregatedResource ar : bundleImplementation.getAggregatedResources()) {
                if (ar instanceof AtomicTask) {
                    File ctrBundle = Locations.getTempFile(ar.getTitle()+".ctrb");
                    DataUtils.removeFromPath(ar, ar.getId());
                    DataUtils.bundle(ctrBundle, ar);
                    repositoryImplementation.getFiles().add(new BundleFileRTO(ar.getTitle()+".ctrb", BundleFileRTO.FileType.CTR_FILE, ctrBundle, "ConcreteTaskRepresentation file for task: "+ar.getTitle()));
                }
            }
        }

        int i = 1;


        pw.println("Checking for Configurations...");
        for(Mapping bundleConfiguration : bundle.getPrimaryMappings()) {
            ConfigurationRTO repositoryConfiguration = new ConfigurationRTO();
            pw.println("Reading Configuration " + i + "...");
            repositoryConfiguration = setConfiguration(i, repositoryConfiguration, bundleConfiguration);

            //pw.println("ConfigType: " + bundleConfiguration.getType());
            if (bundleConfiguration instanceof EnvironmentMapping) {
                if (repositoryImplementation != null) {
                    pw.println("Adding Configuration to Implementation");
                    repositoryImplementation.getConfigurations().add(repositoryConfiguration);
                }
            } else if (bundleConfiguration instanceof DataMapping) {
                if (repositoryWorkflow != null) {
                    pw.println("Adding Configuration to Abstract Workflow");
                    repositoryWorkflow.getConfigurations().add(repositoryConfiguration);
                }
            }

            i++;
        }

        if (signature == null) throw new NotFoundException("Signature not present for validation");
        if (repositoryImplementation == null) throw new NotFoundException("Cannot create new Workflow Implementation; no Implementation found in Bundle");

        if (workflowId == null) {
            pw.println("Creating Abstract Workflow on repository...");
            if (repositoryWorkflow == null) throw new NotFoundException("Cannot create new Abstract Workflow; no Abstract Workflow found in Bundle");

            repositoryWorkflow.setSignature(signature);
            workflowId = repo.createWorkflow(userId, repositoryWorkflow);
            pw.println("Abstract Workflow generated with Id: " + workflowId);
        } else {
            WorkflowRTO parent = repo.getWorkflow(userId, workflowId);

            if (!force) {
                SignatureResult result = new SignatureResult(RepoUtil.validateSignature(parent.getSignature(), signature));

                if (result.getLevel() < 2) {
                    throw new ForbiddenException("Invalid Signture");
                }
            }
        }

        repositoryImplementation.setWorkflowId(workflowId);

        if (implementationId == null) {
            pw.println("Creating Workflow Implementation on repository...");
            implementationId = repo.createImplementation(userId, repositoryImplementation);
            pw.println("Workflow Implementation generated with Id:" + implementationId);
        } else {
            pw.println("Updating Implementation " + implementationId + " on reposiroty");
            repo.updateImplementation(userId, repositoryImplementation);
        }

        return workflowId + "/" + implementationId;
    }

    private WorkflowRTO setWorkflow(WorkflowRTO workflowRTO, AbstractWorkflow abstractWorkflow) {
        pw.println("Reading Workflow");
        workflowRTO = new WorkflowRTO();

        if (abstractWorkflow.getCreated()  != null) {
            workflowRTO.setCreated(new Timestamp(abstractWorkflow.getCreated().getTime()));
            pw.println("Setting Created: " + workflowRTO.getCreated().toString());
        }

        if (abstractWorkflow.getModified()  != null) {
            workflowRTO.setModified(new Timestamp(abstractWorkflow.getModified().getTime()));
            pw.println("Setting Modified: " + workflowRTO.getModified().toString());
        }

        List<BundleFileRTO> files = new ArrayList<BundleFileRTO>();

        for (BundleFile bf : abstractWorkflow.getBundleFiles()) {
            BundleFileRTO.FileType type = BundleFileRTO.FileType.BUNDLE_FILE;
            files.add(new BundleFileRTO(bf.getFilename(), type, new File(bf.getSystemPath()), bf.getDescription()));
        }

        workflowRTO.setConfigurations(new ArrayList<ConfigurationRTO>());

        String keywords = "";
        String comma = "";

        for (String s : abstractWorkflow.getKeywords()) {
            keywords += comma + s;
            comma = ",";
        }

        workflowRTO.setKeywords(keywords);
        pw.println("Setting Keywords: " + workflowRTO.getKeywords());

        if (abstractWorkflow.getDescription().isEmpty()) {
            workflowRTO.setDescription("Abstract Workflow generated by SHIWADesktop");
        } else {
            workflowRTO.setDescription(abstractWorkflow.getDescription());
        }
        pw.println("Setting Description: " + workflowRTO.getDescription());

        if (abstractWorkflow.getApplication() != null) {
            String value = abstractWorkflow.getApplication().getId().replace(Application.CONCEPT_URI, "");
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(pw);
            }

            workflowRTO.setApplication(value);
            pw.println("Setting Application: " + workflowRTO.getApplication());
        }

        pw.println("Checking Domain");
        if (abstractWorkflow.getDomain()  != null) {
            pw.println("Setting value");
            String value = abstractWorkflow.getDomain().getId().replace(Domain.CONCEPT_URI, "");
            pw.println("Attempting to decode");
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(pw);
            }
            pw.println("Replacing slashes... ");
            workflowRTO.setDomain(value.replace("/",", "));
            pw.println("Setting Domain: " + workflowRTO.getDomain());
        }

        pw.println("Checking Name");
        workflowRTO.setName(abstractWorkflow.getTitle());
        pw.println("Setting Name: " + workflowRTO.getName());

        pw.println("Checking Access Rights");
        AccessRightsRTO repositoryAccessRights = new AccessRightsRTO();
        AccessRights bundleAccessRights = abstractWorkflow.getAccessRights();

        pw.println("Checking Owner ID");
        repositoryAccessRights.setOwnerId(userId); //bundleAccessRights.getOwnerId());
        pw.println("Setting OwnerID: " + repositoryAccessRights.getOwnerId());

        pw.println("Setting all values of Access Rights");
        if (bundleAccessRights != null) {
            repositoryAccessRights.setGroupId(bundleAccessRights.getGroupId());
            pw.println("Setting GroupID: " + repositoryAccessRights.getGroupId());
            repositoryAccessRights.setGroupDownload(bundleAccessRights.isGroupDownload());
            pw.println("Setting GroupDownload: " + repositoryAccessRights.isGroupDownload());
            repositoryAccessRights.setGroupModify(bundleAccessRights.isGroupModify());
            pw.println("Setting GroupModify: " + repositoryAccessRights.isGroupModify());
            repositoryAccessRights.setGroupRead(bundleAccessRights.isGroupRead());
            pw.println("Setting GroupRead: " + repositoryAccessRights.isGroupRead());
            repositoryAccessRights.setOthersDownload(bundleAccessRights.isOthersDownload());
            pw.println("Setting OthersDownload: " + repositoryAccessRights.isOthersDownload());
            repositoryAccessRights.setOthersRead(bundleAccessRights.isOthersRead());
            pw.println("Setting OthersRead: " + repositoryAccessRights.isOthersRead());
            repositoryAccessRights.setPublished(bundleAccessRights.isPublished());
            pw.println("Setting Published: " + repositoryAccessRights.isPublished());
        }

        workflowRTO.setAccessRights(repositoryAccessRights);

        return workflowRTO;
    }

    public ImplementationRTO setImplementation(ImplementationRTO implementationRTO, WorkflowImplementation implementation) throws UnauthorizedException, NotFoundException {
        implementationRTO = new ImplementationRTO();
        implementationRTO.setState(ImplementationRTO.Status.NEW);

        if (implementation.getCreated()  != null) {
            implementationRTO.setCreated(new Timestamp(implementation.getCreated().getTime()));
            pw.println("Setting Created: " + implementationRTO.getCreated().toString());
        }

        if (implementation.getModified()  != null) {
            implementationRTO.setModified(new Timestamp(implementation.getModified().getTime()));
            pw.println("Setting Modified: " + implementationRTO.getModified().toString());
        }

        List<BundleFileRTO> files = new ArrayList<BundleFileRTO>();

        for (BundleFile bf : implementation.getBundleFiles()) {
            BundleFileRTO.FileType type = BundleFileRTO.FileType.BUNDLE_FILE;

            if (bf.getType() == BundleFile.FileType.DEFINITION_FILE) {
                implementationRTO.setDefinition(bf.getFilename());
                type = BundleFileRTO.FileType.DEFINITION_FILE;
            } else if (bf.getType() == BundleFile.FileType.IMAGE_FILE) {
                implementationRTO.setGraph(bf.getFilename());
                type = BundleFileRTO.FileType.IMAGE_FILE;
            }

            files.add(new BundleFileRTO(bf.getFilename(), type, new File(bf.getSystemPath()), bf.getDescription()));
        }

        implementationRTO.setFiles(files);

        if (implementationRTO.getGraph() == null) implementationRTO.setGraph("");
        if (implementationRTO.getDefinition() == null) implementationRTO.setDefinition("");

        EngineRTO dummyRTO = new EngineRTO(0, implementation.getEngine(), implementation.getEngineVersion(), "");
        List<EngineRTO> engsList = repo.getEngines(userId, null);

        for (EngineRTO erto : engsList) {
            pw.println("Comparing with repo engine: " + erto.getTitle() +
                    ", version: " + erto.getVersion());
            String engine = implementation.getEngine();
            if (erto.getTitle().equalsIgnoreCase(engine)
                    && erto.getVersion().equalsIgnoreCase(implementation.getEngineVersion())) {

                pw.println("Found a matching Engine: " + engine);

                pw.println("Setting Engine version to " + erto.getId());
                dummyRTO.setId(erto.getId());
                break;

            }
        }

        if(dummyRTO.getId() == 0){
            try {
                //a matching engine was not found in the list of engines present in the repository.
                pw.println("Engine not found - attempting automatic creation!");
                dummyRTO = repo.createEngine(implementation.getEngine(), implementation.getEngineVersion());
            } catch (AuthorizationException ex) {
               pw.println(ex);
            } catch (ForbiddenException ex) {
                pw.println(ex);
            }
        }

        pw.println("Engine is :" + dummyRTO.getTitle() + " " + dummyRTO.getVersion());
        implementationRTO.setEngineId(dummyRTO.getId());


        String keywords = "";
        String comma = "";

        for (String s : implementation.getKeywords()) {
            keywords += comma + s;
            comma = ",";
        }

        implementationRTO.setKeywords(keywords);
        pw.println("Setting Keywords: " + implementationRTO.getKeywords());
        implementationRTO.setDescription(implementation.getDescription());
        pw.println("Setting Description: " + implementationRTO.getDescription());

        if (implementation.getLanguage() != null) {
            String value = implementation.getLanguage().getId().replace(ConceptResource.CONCEPT_URI, "");
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(pw);
            }

            implementationRTO.setLanguage(value);
            pw.println("Setting Language: " + implementationRTO.getLanguage());
        }

        implementationRTO.setRights(implementation.getCopyright());
        pw.println("Setting Rights: " + implementationRTO.getRights());
        implementationRTO.setTitle(implementation.getTitle());
        pw.println("Setting Title: " + implementationRTO.getTitle());
        implementationRTO.setUuid(implementation.getUuid());
        pw.println("Setting UUID: " + implementationRTO.getUuid());
        if (implementation.getVersion() == null || implementation.getVersion().isEmpty() || implementation.getVersion().equals("")) {
            implementation.setVersion("0.1");
        }
        implementationRTO.setVersion(implementation.getVersion());
        pw.println("Setting Version: " + implementationRTO.getVersion());
        implementationRTO.setLicence(implementation.getLicence());
        pw.println("Setting Liscence: " + implementationRTO.getLicence());

        List<DependencyRTO> dependencies = new ArrayList<DependencyRTO>();

        for (ReferableResource depednency : implementation.getDependencies()) {
            DependencyRTO dependencyRTO = new DependencyRTO();
            dependencyRTO.setName(depednency.getId());
            dependencyRTO.setTitle(depednency.getTitle());
            dependencyRTO.setDescription(depednency.getDescription());
            dependencyRTO.setType(DataUtils.getDataTypeWithDepth(depednency, false));
            pw.println("Adding dependency: " + dependencyRTO.getTitle());
            dependencies.add(dependencyRTO);
        }

        implementationRTO.setDependencies(dependencies);

        implementationRTO.setConfigurations(new ArrayList<ConfigurationRTO>());

        return implementationRTO;
    }

    public SignatureRTO setSignature(SignatureRTO signatureRTO, WorkflowImplementation implementation) {
        pw.println("Looking for signature...");
        TaskSignature workflowSignature = implementation.getSignature();
        pw.println("Singatire is: " + workflowSignature);
        signatureRTO = new SignatureRTO();
        List<PortRTO> inputPorts = new ArrayList<PortRTO>();
        List<PortRTO> outputPorts = new ArrayList<PortRTO>();

        if (workflowSignature.getTasktype() != null) {
            String value = workflowSignature.getTasktype().getId().replace(Tasktype.CONCEPT_URI, "");
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace(pw);
            }

            signatureRTO.setTaskType(value);
            pw.println("Setting TaskType: " + signatureRTO.getTaskType());
        }

        pw.println("Generating ports...");
        for (ReferableResource port : workflowSignature.getPorts()) {
            PortRTO portRTO = new PortRTO();
            pw.println("Creating port with name: " + port.getId());
            pw.println("value: " + port.getTitle());
            pw.println("description: " + port.getDescription());
            pw.println("datatype: " + port.getDataType());
            pw.println("depth: " + port.getCollectionDepth());
            pw.println("porttype: " + port.getResourceType());

            portRTO.setName(port.getId());
            portRTO.setValue(port.getTitle());
            portRTO.setDescription(port.getDescription());
            portRTO.setType(DataUtils.getDataTypeWithDepth(port, false));

            pw.println(port.getResourceType());

            if (port.getResourceType() == ReferableResource.ResourceType.INPORT) {
                pw.print("Adding to input ports at: ");
                pw.println(inputPorts.size());
                inputPorts.add(portRTO);
            } else if (port.getResourceType() == ReferableResource.ResourceType.OUTPORT) {
                pw.print("Adding to output ports at: ");
                pw.println(outputPorts.size());
                outputPorts.add(portRTO);
            }
        }

        pw.println("Setting input ports...");
        signatureRTO.setInputPorts(inputPorts);
        pw.println("Setting output ports...");
        signatureRTO.setOutputPorts(outputPorts);

        return signatureRTO;
    }

    public ConfigurationRTO setConfiguration(int i, ConfigurationRTO configurationRTO, Mapping configuration) {
        configurationRTO.setId(i);
        configurationRTO.setUuid(configuration.getUuid());

        if (configuration instanceof DataMapping) {
            configurationRTO.setConfigurationType(ConfigurationRTO.ConfigurationType.PORT);
        } else {
            configurationRTO.setConfigurationType(ConfigurationRTO.ConfigurationType.DEPENDENCY);
        }

        configurationRTO.setDescription(configuration.getDescription());

        List<ConfigurationNodeRTO> nodes = new ArrayList<ConfigurationNodeRTO>();

        for (ConfigurationResource cr : configuration.getResources()) {
            ConfigurationNodeRTO configurationNodeRTO = new ConfigurationNodeRTO();

            configurationNodeRTO.setSubjectId(cr.getReferableResource().getId());
            configurationNodeRTO.setValue(cr.getValue().replace(configuration.getUuid()+"/",""));
            configurationNodeRTO.setType(cr.getRefType().name());

            nodes.add(configurationNodeRTO);
        }

        configurationRTO.setConfigurationNodes(nodes);

        List<BundleFileRTO> files = new ArrayList<BundleFileRTO>();

        for (BundleFile bf : configuration.getBundleFiles()) {
            BundleFileRTO.FileType type = BundleFileRTO.FileType.BUNDLE_FILE;

            if (bf.getType() == BundleFile.FileType.DATA_FILE) {
                type = BundleFileRTO.FileType.INPUT_FILE;
            }

            String filename = bf.getFilename().replace(configuration.getUuid() + "/", "");

            files.add(new BundleFileRTO(filename, type, new File(bf.getSystemPath()), bf.getDescription()));
        }

        configurationRTO.setFiles(files);

        return configurationRTO;
    }
}