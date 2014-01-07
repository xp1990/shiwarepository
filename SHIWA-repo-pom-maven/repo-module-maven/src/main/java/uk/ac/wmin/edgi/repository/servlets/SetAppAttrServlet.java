/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author zsolt
 */
@WebServlet(name="SetAppAttrServlet", urlPatterns={"/mce/setappattr"})
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5, maxRequestSize=1024*1024*5*5)
public class SetAppAttrServlet extends HttpServlet {

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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Collection<Part> parts = request.getParts();

        out.write("<h2> Total parts : "+parts.size()+"</h2>");

        for(Part part : parts) {
                printPart(part, out);
                part.write("samplefile");
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
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }


    private void printPart(Part part, PrintWriter pw) {
            StringBuffer sb = new StringBuffer();
            sb.append("<p>");
            sb.append("Name : "+part.getName());
            sb.append("<br>");
            sb.append("Content Type : "+part.getContentType());
            sb.append("<br>");
            sb.append("Size : "+part.getSize());
            sb.append("<br>");
            for(String header : part.getHeaderNames()) {
                    sb.append(header + " : "+part.getHeader(header));
                    sb.append("<br>");
            }
            sb.append("</p>");
            try {
                InputStream is = part.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                sb.append("\n");
                while ((line = reader.readLine()) != null) {
                  sb.append(line + "\n");
                }
            }catch(Exception ex){
                sb.append("Error: Cannot read file: "+ex.getMessage());
            }
            pw.write(sb.toString());
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
