
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.toolkit.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;


/**
 *
 * @author zsolt
 */
@WebServlet(name="TestSevlet", urlPatterns={"/testservlet"})
public class TestServlet extends HttpServlet {

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
            out.println("Usign the following settings to access GEMLCA:");
            out.println("userpass: "+af.getGlobusUserpass());
            out.println("usercert: "+af.getGlobusUsercert());
            out.println("userkey: "+af.getGlobusUserkey());                                  


            
            // getting lc list

            /*
            System.out.println("deploying lc <BR/>");
            gc.DeployLC(testLCD, testFiles);            
            System.out.println("deployed");            
            */
            
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

    
    
    static String lcName = "Kepler-Subtract-td2";

    static File[] testFiles = new File[] {
        new File("/home/glassfish/testlocal1.txt"), 
        new File("/home/glassfish/testlocal2.txt"), 
        new File("/home/glassfish/testlocal3.txt"), 
        new File("/home/glassfish/testlocal4.txt")};
    
   
    
}
