/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.toolkit.servlet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shiwa.desktop.data.description.ConcreteBundle;
import org.shiwa.desktop.data.description.core.AbstractWorkflow;
import org.shiwa.desktop.data.description.core.TaskSignature;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.validation.SignatureResult;
import org.shiwa.desktop.data.util.Base64;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.properties.Locations;
import org.shiwa.desktop.data.util.writer.XMLResourceWriter;
import org.shiwa.repository.toolkit.ToolkitImplementation;
import org.shiwa.repository.toolkit.ToolkitInterface;
import org.shiwa.repository.toolkit.exceptions.ForbiddenException;
import org.shiwa.repository.toolkit.exceptions.ItemExistsException;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;
import org.shiwa.repository.toolkit.exceptions.UnauthorizedException;
import org.shiwa.repository.toolkit.transferobjects.PortRTO;
import org.shiwa.repository.toolkit.transferobjects.SignatureRTO;
import org.shiwa.repository.toolkit.transferobjects.WorkflowRTO;
import org.shiwa.repository.toolkit.util.RepoUtil;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author zsolt
 */
@WebServlet(name="ValidateSignature", urlPatterns={"/validatesignature/*"})
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class ValidateSignature extends HttpServlet {
    
    @EJB ApplicationFacadeLocal af;    

    static ToolkitInterface repo;
    int userId=-1;

    private PrintWriter pw;

    @Override
    public void init() throws ServletException {
        super.init();
        //repo = Test.getRepo();
        repo = new ToolkitImplementation(af); 
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
        //System.out.println("######user: "+request.getRemoteUser());
        userId = af.loadUser(request.getRemoteUser()).getId();
        
        File log = File.createTempFile("validate-execution",".log");
        pw = new PrintWriter(new FileOutputStream(log));

        Integer workflowId = null;

        String path = request.getPathInfo();

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
                }
            }
        }

        String id = UUID.randomUUID().toString();

        File out = Locations.getTempFile("tmp"+id);
        File result = null;

        String status = "";
        int resposneValue = 201;

        try {
            result = validateSignature(request, out, workflowId);
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
            int numRead;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();

            response.sendError(resposneValue, Base64.encode(fileData.toString().getBytes()));
        } else if (result != null) {
            InputStream in = new FileInputStream(result);
            response.setStatus(resposneValue);

            response.setContentType("application/xml");
            response.addHeader("Content-Disposition", "attachment; filename="+DataUtils.METADATA_RDF);
            response.addHeader("Cache-Control", "no-cache");
            RepoUtil.write(in, response.getOutputStream());
        } else {
            InputStream in = new FileInputStream(log);
            response.setStatus(resposneValue);

            response.setContentType("application/xml");
            response.addHeader("Content-Disposition", "attachment; filename="+log.getName());
            response.addHeader("Cache-Control", "no-cache");
            RepoUtil.write(in, response.getOutputStream());
        }
    }

    private File validateSignature(HttpServletRequest request, File out, Integer workflowId) throws UnauthorizedException, NotFoundException, IOException, ItemExistsException, ForbiddenException {
        RepoUtil.write(request.getInputStream(), new FileOutputStream(out));
        pw.println("Writing Bundle to temp file: " + out.getName());
        ConcreteBundle bundle = new ConcreteBundle(out);
        pw.println("Bundle read... Passing to Workflow Controller");
        //WorkflowController controller = new WorkflowController(bundle);

        SignatureRTO signature = null;

        pw.println("Checking for Abstract Workflow...");
        if (bundle.getPrimaryAbstractTask() != null && bundle.getPrimaryAbstractTask() instanceof AbstractWorkflow) {
            AbstractWorkflow bundleWorkflow = (AbstractWorkflow) bundle.getPrimaryAbstractTask();
            pw.println("Reading Abstract Workflow details...");
            signature = setSignature(signature, bundleWorkflow);
        }

        WorkflowRTO parent = repo.getWorkflow(userId, workflowId);
        SignatureResult signatureResult = new SignatureResult(RepoUtil.validateSignature(parent.getSignature(), signature));

        File result = File.createTempFile("tmp", UUID.randomUUID().toString());

        FileOutputStream fos = new FileOutputStream(result);

        XMLResourceWriter w = new XMLResourceWriter();
        w.setBaseURI(DataUtils.BASE_URI);
        w.write(fos, signatureResult, "RDF/XML-ABBREV");

        return result;
    }

    public SignatureRTO setSignature(SignatureRTO signatureRTO, AbstractWorkflow workflow) throws NotFoundException {
        if (workflow.getSignature() == null) throw new NotFoundException("Cannot find Signature");

        pw.println("Looking for signature...");
        TaskSignature workflowSignature = workflow.getSignature();
        pw.println("Singatire is: " + workflowSignature);
        signatureRTO = new SignatureRTO();
        List<PortRTO> inputPorts = new ArrayList<PortRTO>();
        List<PortRTO> outputPorts = new ArrayList<PortRTO>();

        pw.println("SettingTaskType to: " + workflowSignature.getTasktype().getShortId());
        signatureRTO.setTaskType(workflowSignature.getTasktype().getShortId());

        pw.println("Generating ports...");
        for (ReferableResource port : workflowSignature.getPorts()) {
            PortRTO portRTO = new PortRTO();
            pw.println("Creating port with name: " + port.getId());
            pw.println("value: " + port.getTitle());
            pw.println("description: " + port.getDescription());
            pw.println("datatype: " + port.getDataType());
            pw.println("porttype: " + port.getResourceType());

            portRTO.setName(port.getId());
            portRTO.setValue(port.getTitle());
            portRTO.setDescription(port.getDescription());
            portRTO.setType(port.getDataType());

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
}