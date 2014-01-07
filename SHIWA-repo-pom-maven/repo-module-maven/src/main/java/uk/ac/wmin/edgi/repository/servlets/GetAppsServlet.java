/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.wmin.edgi.repository.transferobjects.AppListItemTO;
import uk.ac.wmin.edgi.repository.common.IllegalAttributeNameException;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author zsolt
 */
@WebServlet(name="GetAppsServlet", urlPatterns={"/mce/getapps"})
public class GetAppsServlet extends HttpServlet {

    @EJB ApplicationFacadeLocal af;

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
            String impAttrName = request.getParameter("impattrname");
            String impAttrVal = request.getParameter("impattrval");
            List<AppListItemTO> result;
            if(impAttrName == null || impAttrVal == null){
                result = af.getAppList();
            }else{
                result = af.getAppList(impAttrName, impAttrVal);
            }
            for(AppListItemTO i: result){
                out.println(i.getID() + " " +i.getName());
            }
        } catch (IllegalAttributeNameException e){
            e.printStackTrace(out);
            response.setStatus(400);
            //response.sendError(400, "failed to convert firstresult or maxresults to int");
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
