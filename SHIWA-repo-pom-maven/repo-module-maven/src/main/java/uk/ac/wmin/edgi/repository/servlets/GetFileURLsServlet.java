/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ejb.EJB;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;
import uk.ac.wmin.edgi.repository.transferobjects.AppFileTO;
import uk.ac.wmin.edgi.repository.transferobjects.ImpFileTO;

/**
 *
 * @author zsolt
 */
@WebServlet(name="GetFileURLsServlet", urlPatterns={"/mce/getfileurls"})
public class GetFileURLsServlet extends HttpServlet {

    @EJB ApplicationFacadeLocal af;

    private String URLPrefix;
    private Logger logger;

    @Override
    public void init() throws ServletException {
        super.init();
        logger = af.getEdgiLogger();
        URLPrefix = getServletConfig().getInitParameter("edgi-gridftp-url-prefix");
        if(URLPrefix == null){
            URLPrefix = "gsiftp://localhost/edgi/";
            logger.log(Level.WARNING, "The gridFTP URL prefix is not set, using default: ''{0}'' (change this with the 'edgi-gridftp-url-prefix' parameter of the GetFileURLsServlet in the web.xml)", URLPrefix);
        }else{
            if(!URLPrefix.endsWith("/")){
                URLPrefix = URLPrefix + "/";
            }
            logger.log(Level.INFO, "The gridFTP URL prefix is set to: ''{0}'' (you change this with the 'edgi-gridftp-url-prefix' parameter of the GetFileURLsServlet in the web.xml)", URLPrefix);
        }
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Pragma","no-cache"); //HTTP 1.0
        response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
        response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
        PrintWriter out = response.getWriter();
        try {
            String developer = request.getParameter("developer");
            String item;
            String itemIDs = null;


            Enumeration<String> pNames = request.getParameterNames();
            while(pNames.hasMoreElements()){
                item = pNames.nextElement();
                if(item.equals("appids")){
                    itemIDs = request.getParameter("appids");
                    String[] ids = itemIDs.split(" ");
                    List<Integer> idc = new ArrayList<Integer>(ids.length);
                    for(String s: ids){
                        idc.add(Integer.parseInt(s));
                    }

                    List<AppFileTO> result = af.getFilesForApps(idc);
                    if(developer == null){
                        for(AppFileTO i: result){
                            out.printf("%d %s%d/%s\n", i.getAppId(), URLPrefix, i.getAppId(), i.getPathName());
                        }
                    }else{
                        for(AppFileTO i: result){
                            out.printf("%s%d/%s\n", URLPrefix, i.getAppId(), i.getPathName());
                        }
                    }
                }
                if(item.equals("impids")){
                    itemIDs = request.getParameter("impids");
                    String[] ids = itemIDs.split(" ");
                    List<Integer> idc = new ArrayList<Integer>(ids.length);
                    for(String s: ids){
                        idc.add(Integer.parseInt(s));
                    }

                    List<ImpFileTO> result = af.getFilesForImps(idc);
                    if(developer == null){
                        for(ImpFileTO i: result){
                            out.printf("%d %s%d/%d/%s\n", i.getImpId(), URLPrefix, i.getAppId(), i.getImpId(), i.getPathName());
                        }
                    }else{
                        for(ImpFileTO i: result){
                            out.printf("%s%d/%d/%s\n", URLPrefix, i.getAppId(), i.getImpId(), i.getPathName());
                        }
                    }
                }
            }

            if(itemIDs == null){
                response.sendError(400, "missing parameter: 'appids' or 'impids' has to be provided");
            }
        } catch (NumberFormatException e){
            e.printStackTrace(out);
            response.setStatus(400);
            //response.sendError(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(out);
            response.setStatus(500);
            //response.sendError(500, e.getMessage());
        } finally {
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
