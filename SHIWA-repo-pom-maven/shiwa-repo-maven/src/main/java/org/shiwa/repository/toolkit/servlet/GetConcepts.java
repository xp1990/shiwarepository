/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.toolkit.servlet;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import org.shiwa.desktop.data.description.bundle.Concepts;
import org.shiwa.desktop.data.description.workflow.Application;
import org.shiwa.desktop.data.description.workflow.Engine;
import org.shiwa.desktop.data.description.workflow.Tasktype;
import org.shiwa.desktop.data.util.Base64;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.writer.XMLResourceWriter;
import org.shiwa.repository.toolkit.ToolkitImplementation;
import org.shiwa.repository.toolkit.ToolkitInterface;
import org.shiwa.repository.toolkit.exceptions.ForbiddenException;
import org.shiwa.repository.toolkit.exceptions.ItemExistsException;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;
import org.shiwa.repository.toolkit.exceptions.UnauthorizedException;
import org.shiwa.repository.toolkit.transferobjects.EngineRTO;
import org.shiwa.repository.toolkit.util.RepoUtil;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author dmckrogers
 */
@WebServlet(name="GetConcepts", urlPatterns={"/getconcepts/*"})
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class GetConcepts extends HttpServlet {

    @EJB ApplicationFacadeLocal af;

    static ToolkitInterface repo;
    int userId=-1;

    //private Logger logger;

    private PrintWriter pw;

    @Override
    public void init() throws ServletException {
        super.init();
        repo = new ToolkitImplementation(af);
        //this.logger = Logger.getLogger(GetConcepts.class.getName());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //logger.log(Level.INFO, "Starting servlet... " + this.getClass().getName());

        File log = File.createTempFile("execution",".log");
        pw = new PrintWriter(new FileOutputStream(log));
        String id = UUID.randomUUID().toString();

        File out = File.createTempFile("tmp", id);
        File result = null;

        String status = "";
        int resposneValue = 201;

        try {
            userId = af.loadUser(request.getRemoteUser()).getId();
            result = getConcepts(request, out);
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

    private File getConcepts(HttpServletRequest request, File out) throws UnauthorizedException, NotFoundException, IOException, ItemExistsException, ForbiddenException {
        RepoUtil.write(request.getInputStream(), new FileOutputStream(out));
        pw.println("Generating Concepts...");

        Concepts concepts = new Concepts();

        for (EngineRTO engineRTO : repo.getEngines(userId, null))  {
            Engine engine = null;
            for (Engine e : concepts.getEngines()) {
                if (e.getTitle().equalsIgnoreCase(engineRTO.getTitle())) {
                    engine = e;
                    break;
                }
            }

            if (engine == null) {
                engine = new Engine(engineRTO.getTitle());
                concepts.getEngines().add(engine);
            }

            boolean versionPresent = false;

            for (String v : engine.getVersions()) {
                if (v.equals(engineRTO.getVersion())) {
                    versionPresent = true;
                }
            }

            if (!versionPresent) {
                engine.getVersions().add(engineRTO.getVersion());
            }
        }

        for (String title : repo.getWFAttributeValues(userId, "application")) {
            Application application = new Application(title);
            concepts.getApplications().add(application);
        }

        for (String title : repo.getWFAttributeValues(userId, "tasktype")) {
            Tasktype tasktype = new Tasktype(title);
            concepts.getTasktypes().add(tasktype);
        }

        for (String title : repo.getWFAttributeValues(userId, "domain")) {
            String value = title.replace(", ", "/").replace(",","/");
            if (value != null && !value.isEmpty() && !value.trim().isEmpty()) {
                DataUtils.addDomain(concepts, value);
            }
        }

        File result = File.createTempFile("tmp", UUID.randomUUID().toString());
        FileOutputStream fos = new FileOutputStream(result);
        XMLResourceWriter w = new XMLResourceWriter();
        w.setBaseURI(DataUtils.BASE_URI);
        w.write(fos, concepts.toModel(), "RDF/XML-ABBREV");

        return result;
    }
}