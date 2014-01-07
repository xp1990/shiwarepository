/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.servlet;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shiwa.desktop.data.description.ConcreteBundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.bundle.Concepts;
import org.shiwa.desktop.data.description.core.*;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.*;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.shiwa.desktop.data.util.properties.DesktopProperties;
import org.shiwa.desktop.data.util.properties.Locations;
import org.shiwa.repository.toolkit.ToolkitImplementation;
import org.shiwa.repository.toolkit.ToolkitInterface;
import org.shiwa.repository.toolkit.exceptions.ForbiddenException;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;
import org.shiwa.repository.toolkit.exceptions.UnauthorizedException;
import org.shiwa.repository.toolkit.transferobjects.*;
import org.shiwa.repository.toolkit.util.RepoUtil;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;


/**
 *
 * @author kukla
 */


@WebServlet(name = "DownloadBundle", urlPatterns = {"/downloadbundle/*", "/downloadbundle", "/downloadbundle_guest/*", "/downloadbundle_guest"})
public class DownloadBundle extends HttpServlet {        
            
    @EJB ApplicationFacadeLocal af;     

    private void setWorkflowAccessRights(AbstractWorkflow desktopWorkflow, WorkflowRTO repositoryWorkflow, PrintWriter pw) {
        AccessRights desktopAccessRights = desktopWorkflow.getAccessRights();
        pw.println("Getting Access Rights...");
        
        desktopAccessRights.setGroupId(repositoryWorkflow.getAccessRights().getGroupId());
        pw.println("Group ID: " + desktopAccessRights.getGroupId());
        desktopAccessRights.setOwnerId(repositoryWorkflow.getAccessRights().getOwnerId());
        pw.println("Owner ID: " + desktopAccessRights.getOwnerId());
        desktopAccessRights.setGroupDownload(repositoryWorkflow.getAccessRights().isGroupDownload());
        pw.println("Group Download: " + desktopAccessRights.isGroupDownload());
        desktopAccessRights.setGroupModify(repositoryWorkflow.getAccessRights().isGroupModify());
        pw.println("Group Modify: " + desktopAccessRights.isGroupModify());
        desktopAccessRights.setGroupRead(repositoryWorkflow.getAccessRights().isGroupRead());
        pw.println("Group Read: " + desktopAccessRights.isGroupRead());
        desktopAccessRights.setOthersRead(repositoryWorkflow.getAccessRights().isOthersRead());
        pw.println("Others Read: " + desktopAccessRights.isOthersRead());
        desktopAccessRights.setOthersDownload(repositoryWorkflow.getAccessRights().isOthersDownload());
        pw.println("Others Download: " + desktopAccessRights.isOthersDownload());
    }

    private void attachFiles(PrintWriter pw, ConfigurationNodeRTO repositoryNode, Set<BundleFile> attachedFiles, ConfigurationRTO repositoryConfiguration, RepoElement elem, Mapping desktopConfiguration, ConfigurationResource configurationResource) {
        boolean fileFound = false;

        pw.println("Attaching file...");

        pw.println("Looking for: " + repositoryNode.getValue());
        for (BundleFile file : attachedFiles) {
            String filename = file.getFilename();
            pw.println("File is called: " + filename);

            String check = checkPrimaryMatch(PrimaryType.CONFIGURATION,repositoryConfiguration.getId(), elem) ? repositoryNode.getValue() : desktopConfiguration.getUuid() + "/" + repositoryNode.getValue();
            pw.println("Corrected to a search for: " + check);

            if (check.endsWith(filename)) {
                pw.println("Found file: " + filename);

                fileFound = true;
                configurationResource.setBundleFile(file);
                configurationResource.setAttached(true);
            }
        }

        if (!fileFound) {
            configurationResource.setRefType(ConfigurationResource.RefTypes.INLINE_REF);
        }
    }

    private List<String> extractComponents(String path) {
        String[] cs = path.split("/");
        List<String> components = new ArrayList<String>();
        for (String component : cs) {
            if (component.length() > 0) {
                components.add(component);
            }
        }
        return components;
    }

    private Map<String, String> createQueryMap(String query) {
        Map<String, String> queryMap = new HashMap<String, String>();
        if (query != null) {
            queryMap = RepoUtil.getQuery(query);
        }
        return queryMap;
    }

    private void addHeaders(HttpServletResponse response, File file) {
        response.addHeader("Content-Disposition", "attachment; filename="+file.getName());
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Content-Length", Long.toString(file.length()));
    }    

    
    private void addInputPorts(WorkflowRTO repositoryWorkflow, PrintWriter pw, TaskSignature signature) {
        for (PortRTO repositoryPort : repositoryWorkflow.getSignature().getInputPorts()) {
            ReferableResource desktopPort = new InputPort(repositoryPort.getName());
            pw.println("Adding Input Port: " + repositoryPort.getName());
            setPortProperties(desktopPort, repositoryPort, signature);
        }
    }

    private void addOutputPorts(WorkflowRTO repositoryWorkflow, PrintWriter pw, TaskSignature signature) {
        for (PortRTO repositoryPort : repositoryWorkflow.getSignature().getOutputPorts()) {
            ReferableResource desktopPort = new OutputPort(repositoryPort.getName());
            pw.println("Adding Output Port: " + repositoryPort.getName());
            setPortProperties(desktopPort, repositoryPort, signature);
        }
    }

    private void setPortProperties(ReferableResource desktopPort, PortRTO repositoryPort, TaskSignature signature) {
        desktopPort.setTitle(repositoryPort.getValue());
        desktopPort.setDescription(repositoryPort.getDescription());
        desktopPort.setDataType(repositoryPort.getType());
        DataUtils.setDataType(desktopPort);
        signature.addPort(desktopPort);
    }

    private void setImplementationEngine(int userId, ImplementationRTO repositoryImplementation, WorkflowImplementation desktopImplementation) throws UnauthorizedException, NotFoundException {
        EngineRTO engine = repo.getEngine(userId, repositoryImplementation.getEngineId());

        desktopImplementation.setEngineVersion(engine.getVersion());
        desktopImplementation.setEngine(engine.getTitle());
    }

    private void setKeywords(ImplementationRTO repositoryImplementation, WorkflowImplementation desktopImplementation) {
        if (repositoryImplementation.getKeywords() != null) {
        
            String[] implementationKeywords = repositoryImplementation.getKeywords().split(",");

            for (String k : implementationKeywords) {
                desktopImplementation.addKeyword(k.trim());
            }
        
        }
    }

    private Set<BundleFile> getBundleFiles(ImplementationRTO repositoryImplementation, WorkflowImplementation desktopImplementation, PrintWriter pw, RepoElement elem) {
        Set<BundleFile> files = new HashSet<BundleFile>();
        for (BundleFileRTO repositoryFile : repositoryImplementation.getFiles()) {
            if (repositoryFile.getTitle() != null && repositoryFile.getTitle().endsWith(".ctrb")) {
                try {
                    ConcreteBundle ctrBundle = new ConcreteBundle(repositoryFile.getFile());
                    DataUtils.appendToPath(ctrBundle.getPrimaryConcreteTask(), ctrBundle.getPrimaryConcreteTask().getId());
                    desktopImplementation.getAggregatedResources().add(ctrBundle.getPrimaryConcreteTask());
                } catch (SHIWADesktopIOException ex) {
                    pw.println("Cannot create CTR: " + repositoryFile.getTitle());
                    Logger.getLogger(DownloadBundle.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                BundleFile.FileType type = BundleFile.FileType.BUNDLE_FILE;
                pw.println("Adding a Bundle File: " + repositoryFile.getTitle());
                if (repositoryFile.getFileType() == BundleFileRTO.FileType.IMAGE_FILE || repositoryFile.getTitle().equals(repositoryImplementation.getGraph())) {
                    type = BundleFile.FileType.IMAGE_FILE;
                } else if (repositoryFile.getFileType() == BundleFileRTO.FileType.DEFINITION_FILE || repositoryFile.getTitle().equals(repositoryImplementation.getDefinition())) {
                    type = BundleFile.FileType.DEFINITION_FILE;
                }

                files.add( 
                    new BundleFile(
                        checkPrimaryMatch(PrimaryType.IMPLEMANTATION,repositoryImplementation.getId(), elem) 
                        ? repositoryFile.getTitle() 
                        : desktopImplementation.getUuid() + "/" + repositoryFile.getTitle(), repositoryFile.getFile().getAbsolutePath(), repositoryFile.getDescription(), type));
            }
        }
        return files;
    }

    private Map<String, ReferableResource> getImplementationDependencies(ImplementationRTO repositoryImplementation, PrintWriter pw, WorkflowImplementation desktopImplementation) {
        List<Dependency> dependencies = new ArrayList<Dependency>();
        Map<String, ReferableResource> dependencyMap = new HashMap<String, ReferableResource>();
        for (DependencyRTO dependencyRTO : repositoryImplementation.getDependencies()) {
            pw.println("Adding a Dependency: " + dependencyRTO.getTitle());
            ReferableResource dependency = new Dependency(dependencyRTO.getName());
            dependency.setTitle(dependencyRTO.getName());
            dependency.setDescription(dependencyRTO.getDescription());
            dependency.setDataType(dependencyRTO.getType());
            DataUtils.setDataType(dependency);

            dependencyMap.put(dependency.getId(), dependency);
            dependencies.add((Dependency) dependency);
        }
        desktopImplementation.setDependencies(dependencies);
        return dependencyMap;
    }

    private void setImplementationConfigurations(ImplementationRTO repositoryImplementation, PrintWriter pw, WorkflowImplementation desktopImplementation, RepoElement elem, Set<BundleFile> files) {
        Map<String, ReferableResource> dependencyMap = getImplementationDependencies(repositoryImplementation, pw, desktopImplementation);

        pw.println("Getting Environment Configurations...");

        for (ConfigurationRTO repositoryConfiguration : repositoryImplementation.getConfigurations()) {
            pw.println("Cretaing Configuration: " + repositoryConfiguration.getId());
            
            Mapping desktopConfiguration = createConfiguration(
                    repositoryConfiguration, 
                    dependencyMap,
                    elem,
                    pw);

            desktopImplementation.getAggregatedResources().add(desktopConfiguration);
            
            List<BundleFile> removeFiles = new ArrayList<BundleFile>();
            
            for (BundleFile configBundleFile : desktopConfiguration.getBundleFiles()) {
                for (BundleFile ibf : files) {
                    if (configBundleFile.getFilename().equalsIgnoreCase(ibf.getFilename())) {
                        removeFiles.add(ibf);
                    }
                }
            }
            
            for (BundleFile rbf : removeFiles) {
                files.remove(rbf);
            }
        }
    }

    private void setWorkflowDomain(WorkflowRTO repositoryWorkflow, AbstractWorkflow desktopWorkflow) {
        if (repositoryWorkflow.getDomain()  != null) {
            String value = repositoryWorkflow.getDomain().replace(", ", "/").replace(",","/");

            Domain domain = DataUtils.addDomain(concepts, value);
            
            desktopWorkflow.setDomain(domain);
        }
    }

    private void setWorkflowKeywords(WorkflowRTO repositoryWorkflow, AbstractWorkflow desktopWorkflow) {
        String[] workflowKeywords = repositoryWorkflow.getKeywords().split(",");

        for (String k : workflowKeywords) {
            desktopWorkflow.addKeyword(k.trim());
        }
    }

    class RepoElement {
        Integer workflowId = null;
        Integer implementationId = null;
        Integer configurationId = null;
        PrimaryType primaryType;
    }
    
    class RepoRequestContext {
        int userId;
        RepoElement elem;
        
    }
    
    private static enum PrimaryType {WORKFLOW, IMPLEMANTATION, CONFIGURATION};

    static ToolkitInterface repo;
    
            
    
    //private PrintWriter pw;    
    private Concepts concepts;

    @Override
    public void init() throws ServletException {
        super.init();
        //repo = Test.getRepo();
        repo = new ToolkitImplementation(af);    
        DesktopProperties.setProperty(DesktopProperties.TEMP_LOCATION, RepoUtil.BUNDLE_TEMP_LOCATION);    
        concepts = new Concepts();
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
        Locations.getDefaultTempLocation();
        File log = Locations.getDebugFile("download-execution",".log",false);        
                              
        boolean includeWorkflow = false;
        Set<Integer> implementations = new HashSet<Integer>();
        Set<Integer> configurations = new HashSet<Integer>();

        String path = request.getPathInfo();
        List<String> components = extractComponents(path);
        RepoElement elem = new RepoElement();
        for (int i = 0; i < components.size(); i++) {
            String component = components.get(i);
            if (i == 0) {
                elem.workflowId = Integer.parseInt(component);
                elem.primaryType = PrimaryType.WORKFLOW;
            } else if (i == 1) {
                elem.implementationId = Integer.parseInt(component);
                implementations.add(elem.implementationId);
                elem.primaryType = PrimaryType.IMPLEMANTATION;
            } else if (i == 2) {
                elem.configurationId = Integer.parseInt(component);
                configurations.add(elem.configurationId);
                elem.primaryType = PrimaryType.CONFIGURATION;
            }
        }

        String query = request.getQueryString();        
        Map<String, String> queryMap = createQueryMap(query); 
        
        for (String s : queryMap.keySet()) {
            String[] values;

            if (s != null && !s.equals("")) values = queryMap.get(s).split(",");
            else values = null;

            if (s.equals("wf")) {
                includeWorkflow = true;
            } else if (s.equals("imps")) {
                for (String st : values) {
                    try {
                        implementations.add(Integer.parseInt(st));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } else if (s.equals("confs")) {
                for (String st : values) {
                    try {
                        configurations.add(Integer.parseInt(st));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } else if (s.equals("v")) {
                String version = queryMap.get(s);
            }
        }
        
        File file = null;
        InputStream in = null;               
        PrintWriter pw = new PrintWriter(new FileOutputStream(log));
        
        try {
            int userId = RepoUtil.getUserId(request, af);
            file = processBundle(includeWorkflow, implementations, configurations, userId, elem, pw);

            try {
                pw.close();

                File[] files = {log};

                addFileToExistingZip(file, files);

            } catch (Exception e) {
                throw new SHIWADesktopIOException(e, "Could Not Update ZIP", file.getName());
            }

            in = new FileInputStream(file);

            response.setContentType("application/zip");
            addHeaders(response, file);
            RepoUtil.write(in, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace(pw);

            pw.close();
            in = new FileInputStream(log);
            response.setContentType("application/xml");
            response.addHeader("Content-Disposition", "attachment; filename="+log.getName());
            response.addHeader("Cache-Control", "no-cache");
            RepoUtil.write(in, response.getOutputStream());
        } finally {
            pw.close();
            file.delete();
            in.close();
        }
    }

    private File processBundle(
            boolean includeWorkflow, 
            Set<Integer> implementations, 
            Set<Integer> configurations, 
            int userId, 
            RepoElement elem,
            PrintWriter pw) 
            
   throws IOException, UnauthorizedException, NotFoundException, ForbiddenException {
        AggregatedResource primary = null;
        Set<AggregatedResource> secondary = new HashSet<AggregatedResource>();
        
        WorkflowRTO repositoryWorkflow = repo.getWorkflow(userId, elem.workflowId);

        pw.println("workflowId: " + elem.workflowId);
        pw.println("implementationId: " + elem.implementationId);
        pw.println("configurationId: " + elem.configurationId);

        AbstractWorkflow desktopWorkflow = createAbstractWorkflow(repositoryWorkflow, pw);

        TaskSignature signature = createWorkflowSignature(repositoryWorkflow, pw);

        Map<String,ReferableResource> signatureMap = new HashMap<String, ReferableResource>();

        for (ReferableResource rr : signature.getPorts()) {
            signatureMap.put(rr.getId(), rr);
        }

        if (elem.primaryType == PrimaryType.WORKFLOW) {
            desktopWorkflow.setSignature(signature);
            primary = desktopWorkflow;
        } else if (includeWorkflow) {
            secondary.add(desktopWorkflow);
        }

        if (RepoUtil.isNotEmpty(implementations)) {

            List<Integer> implementationIds = new ArrayList<Integer>();

            for (Integer imp : implementations) {
                implementationIds.add(imp);
            }

            pw.println("Getting implementations from repo");
            List<ImplementationRTO> repoImplementations = repo.getImplementations(userId, implementationIds);
            pw.println(repoImplementations.size() + " implementation(s) retrieved");


            for (ImplementationRTO repositoryImplementation : repoImplementations) {
                pw.println("Generating Workflow Implementation for repoImp: " + repositoryImplementation.getId());
                
                WorkflowImplementation desktopImplementation = createImplementation(
                        repositoryImplementation, 
                        userId,
                        elem,
                        pw);

                if (checkPrimaryMatch(PrimaryType.IMPLEMANTATION,repositoryImplementation.getId(), elem)) {
                    pw.println("Setting Workflow Implementation " + repositoryImplementation.getId() + " as Primary");
                    desktopImplementation.setSignature(signature);
                    primary = desktopImplementation;

                    secondary.addAll(desktopImplementation.getAggregatedResources());

                } else {
                    secondary.add(desktopImplementation);
                }
            }
        }

        if (RepoUtil.isNotEmpty(configurations)) {

            List<Integer> configurationIds = new ArrayList<Integer>();

            for (Integer i : configurations) {
                configurationIds.add(i);
            }

            pw.println("Getting configurations from repo");
            List<ConfigurationRTO> repoConfigurations = repo.getConfigurations(userId, elem.workflowId, configurationIds);
            pw.println(repoConfigurations.size() + " configuration(s) retrieved");

            for (ConfigurationRTO repositoryConfiguration : repoConfigurations) {
                pw.println("Generating Data Configuration for repoImp: " + repositoryConfiguration.getId());
                Mapping desktopConfiguration = createConfiguration(repositoryConfiguration, signatureMap, elem, pw);

                if (checkPrimaryMatch(PrimaryType.CONFIGURATION,repositoryConfiguration.getId(), elem)) {
                    pw.println("Setting Configuration " + repositoryConfiguration.getId() + " as Primary");
                    primary = desktopConfiguration;
                } else {
                    secondary.add(desktopConfiguration);
                }
            }
        }

        if (primary != null) {
            if (RepoUtil.isNotEmpty(secondary)) primary.setAggregatedResources(secondary);
        } else {
            throw new NotFoundException("No Primary Resource found! Primary Type:" + elem.primaryType + " Primary ID:" + getPrimaryId(elem));
        }

        File file = Locations.getTempFile("bundle-" + primary.getId(),".zip");

        try {
            file = DataUtils.bundle(file, primary);
        } catch (SHIWADesktopIOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public TaskSignature createWorkflowSignature(
            WorkflowRTO repositoryWorkflow,
            PrintWriter pw) 
    throws UnsupportedEncodingException
    {
        
        pw.println("Generating Signature...");
        TaskSignature signature = new TaskSignature();

        if (repositoryWorkflow.getSignature().getTaskType() != null) {
            signature.setTasktype(new Tasktype(repositoryWorkflow.getSignature().getTaskType()));
        }
        
        addInputPorts(repositoryWorkflow, pw, signature);
        addOutputPorts(repositoryWorkflow, pw, signature);

        return signature;
    }

    public AbstractWorkflow createAbstractWorkflow(
            WorkflowRTO repositoryWorkflow,
            PrintWriter pw)    
            throws UnsupportedEncodingException 
    {
        AbstractWorkflow desktopWorkflow = new AbstractWorkflow(Integer.toString(repositoryWorkflow.getId()));
        if (repositoryWorkflow.getUuid()  != null && !repositoryWorkflow.getUuid().equals("")) {
            desktopWorkflow.setUuid(repositoryWorkflow.getUuid());
        }
        pw.println("AbstractWorkflow generated with UUID " + desktopWorkflow.getUuid());

        desktopWorkflow.setTitle(repositoryWorkflow.getName());
        desktopWorkflow.setCreated(repositoryWorkflow.getCreated());
        desktopWorkflow.setModified(repositoryWorkflow.getModified());
        
        if (repositoryWorkflow.getApplication()  != null) {
            desktopWorkflow.setApplication(new Application(repositoryWorkflow.getApplication()));
        }
        
        setWorkflowDomain(repositoryWorkflow, desktopWorkflow);
        desktopWorkflow.setDescription(repositoryWorkflow.getDescription());

        
        setWorkflowAccessRights(desktopWorkflow, repositoryWorkflow, pw);
        setWorkflowKeywords(repositoryWorkflow, desktopWorkflow);

        pw.println("Returning Abstract Workflow ; ID: " + desktopWorkflow.getId() + " UUID:" + desktopWorkflow.getUuid());
        return desktopWorkflow;
    }

    public WorkflowImplementation createImplementation(
            ImplementationRTO repositoryImplementation, 
            int userId,
            RepoElement elem,
            PrintWriter pw) 
            throws UnauthorizedException, NotFoundException {
        WorkflowImplementation desktopImplementation = new WorkflowImplementation(Integer.toString(repositoryImplementation.getId()));
        if (repositoryImplementation.getUuid() != null && !repositoryImplementation.getUuid().equals("")) {
            desktopImplementation.setUuid(repositoryImplementation.getUuid());
        }
        
        pw.println("WorkflowImplementation generated with UUID " + desktopImplementation.getUuid());

        desktopImplementation.setTitle(repositoryImplementation.getTitle());
        desktopImplementation.setCopyright(repositoryImplementation.getRights());
        desktopImplementation.setLicence(repositoryImplementation.getLicence());
        desktopImplementation.setCreated(repositoryImplementation.getCreated());
        desktopImplementation.setModified(repositoryImplementation.getModified());
        desktopImplementation.setDescription(repositoryImplementation.getDescription());

        desktopImplementation.setLanguage(new Language(repositoryImplementation.getLanguage()));
        
        setKeywords(repositoryImplementation, desktopImplementation);
        
        setImplementationEngine(userId, repositoryImplementation, desktopImplementation);

        desktopImplementation.setVersion(repositoryImplementation.getVersion());
        
        Set<BundleFile> files = getBundleFiles(repositoryImplementation, desktopImplementation, pw, elem);
        setImplementationConfigurations(repositoryImplementation, pw, desktopImplementation, elem, files);

        desktopImplementation.setBundleFiles(files);

        pw.println("Returning Workflow Implementation; ID: " + desktopImplementation.getId() + " UUID:" + desktopImplementation.getUuid());
        return desktopImplementation;
    }

    public Mapping createConfiguration(
            ConfigurationRTO repositoryConfiguration, 
            Map<String, ReferableResource> resourceMap,
            RepoElement elem,
            PrintWriter pw) 
    {
        pw.println("Repo Config is null?: " + (repositoryConfiguration == null));
        pw.println("ResourceMap is null?: " + (resourceMap == null));
        pw.println("Repo Id: " + repositoryConfiguration.getId());
        pw.println("Repo UUID: " + repositoryConfiguration.getUuid());
        pw.println("Repo Type: " + repositoryConfiguration.getConfigurationType());
        
        
        Mapping desktopConfiguration;
        
        if (repositoryConfiguration.getConfigurationType() == ConfigurationRTO.ConfigurationType.PORT) {
            desktopConfiguration = new DataMapping(Integer.toString(repositoryConfiguration.getId()));
        } else {
            desktopConfiguration = new EnvironmentMapping(Integer.toString(repositoryConfiguration.getId()));
        }
        
        if (repositoryConfiguration.getUuid() != null && !repositoryConfiguration.getUuid().equals("")) {
            desktopConfiguration.setUuid(repositoryConfiguration.getUuid());
        }
        pw.println("Configuration generated with UUID " + desktopConfiguration.getUuid());

        desktopConfiguration.setTitle("Configruation " + repositoryConfiguration.getId());
        desktopConfiguration.setDescription("Configruation " + repositoryConfiguration.getId());

        //Set<BundleFile> inputFiles = new HashSet<BundleFile>();
        Set<BundleFile> attachedFiles = new HashSet<BundleFile>();

        for (BundleFileRTO repositoryFile : repositoryConfiguration.getFiles()) {
            String filename = repositoryFile.getTitle();
            pw.println("Creating Bundle File (title: " + filename + ")");
            
            filename = checkPrimaryMatch(PrimaryType.CONFIGURATION,repositoryConfiguration.getId(), elem) 
                        ? repositoryFile.getTitle() 
                        : desktopConfiguration.getUuid() + "/" + repositoryFile.getTitle();
            
            pw.println("Title Changed to: " + filename);

            BundleFile bf = new BundleFile(filename, repositoryFile.getFile().getAbsolutePath(), repositoryFile.getDescription(), BundleFile.FileType.BUNDLE_FILE);

            if (repositoryFile.getFileType() == BundleFileRTO.FileType.INPUT_FILE || repositoryFile.getFileType() == BundleFileRTO.FileType.DEPENDENCY_FILE) {
                bf.setType(BundleFile.FileType.DATA_FILE);
                attachedFiles.add(bf);attachedFiles.add(bf);
            }
        }

        int i = 0;

        pw.println("Generating Node Mapping");
        for (ConfigurationNodeRTO repositoryNode : repositoryConfiguration.getConfigurationNodes()) {
            ConfigurationResource configurationResource = new ConfigurationResource("node"+(i++));
            pw.println("Creating node: " + configurationResource.getId());

            if (repositoryNode.getType().equals(ConfigurationResource.RefTypes.FILE_REF.name())) {
                configurationResource.setRefType(ConfigurationResource.RefTypes.FILE_REF);
            } else if (repositoryNode.getType().equals(ConfigurationResource.RefTypes.URI_REF.name())) {
                configurationResource.setRefType(ConfigurationResource.RefTypes.URI_REF);
            } else {
                configurationResource.setRefType(ConfigurationResource.RefTypes.INLINE_REF);
            }

            pw.println("Node type: " + configurationResource.getRefType());

            configurationResource.setValue(repositoryNode.getValue());

            pw.println("Node value: " + configurationResource.getValue());

            pw.println("Mapping value to Node...");
            if (resourceMap != null && resourceMap.containsKey(repositoryNode.getSubjectId())) {

                pw.println("Setting subject...");
                configurationResource.setReferableResource(resourceMap.get(repositoryNode.getSubjectId()));

                if (configurationResource.getRefType() == ConfigurationResource.RefTypes.FILE_REF) {
                    attachFiles(pw, repositoryNode, attachedFiles, repositoryConfiguration, elem, desktopConfiguration, configurationResource);
                }

                desktopConfiguration.addResourceRef(configurationResource);
            }
        }

        desktopConfiguration.setBundleFiles(attachedFiles);
        pw.println("Returning Configuration; ID: " + desktopConfiguration.getId() + " UUID:" + desktopConfiguration.getUuid());

        return desktopConfiguration;
    }

    private boolean checkPrimaryMatch(PrimaryType type, Integer id, RepoElement elem) {
        boolean returnValue;

        //pw.println("Actual: " + this.primaryType + " \t Check: " + type);

        if (elem.primaryType != type) {
            //pw.println("Type mismatch");
            returnValue = false;
        } else {
            switch (type) {
                case CONFIGURATION: {
                    //pw.println("Actual: " + configurationId + " \t Check: " + id);
                    if (elem.configurationId == null) return false;
                    returnValue = elem.configurationId.equals(id);
                }
                break;
                case IMPLEMANTATION: {
                    //pw.println("Actual: " + implementationId + " \t Check: " + id);
                    if (elem.implementationId == null) return false;
                    returnValue = elem.implementationId.equals(id);
                }
                break;
                case WORKFLOW: {
                    //pw.println("Actual: " + workflowId + " \t Check: " + id);
                    if (elem.workflowId == null) return false;
                    returnValue = elem.workflowId.equals(id);
                }
                break;
                default: returnValue = false;
            }
        }

        //pw.println("Returning: " + returnValue);

        return returnValue;
    }

    public Integer getPrimaryId(RepoElement elem) {
        switch (elem.primaryType) {
            case CONFIGURATION: return elem.configurationId;
            case IMPLEMANTATION: return elem.implementationId;
            case WORKFLOW:return elem.workflowId;
            default: return 0;
        }
    }

    public static void addFileToExistingZip(File zipFile, File[] files) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk=zipFile.renameTo(tempFile);
        if (!renameOk)
        {
            throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (File f : files) {
                if (f.getName().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]);
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(files[i].getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }
}
