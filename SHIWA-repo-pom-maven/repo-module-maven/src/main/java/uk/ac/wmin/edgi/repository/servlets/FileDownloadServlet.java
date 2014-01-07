/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.servlets;

import java.io.*;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.wmin.edgi.repository.common.ImpFile;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author zsolt
 */
@WebServlet(name="FileDownloadServlet", urlPatterns={"/download"})
public class FileDownloadServlet extends HttpServlet {

    @EJB ApplicationFacadeLocal af;

    private static final int BUFSIZE = 102400;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        try{
            //workflow engine file
            File wf = null;
            ImpFile f = null;
            String item;

            String fileName = request.getParameter("filename");
            Enumeration<String> pNames = request.getParameterNames();
            while(pNames.hasMoreElements()){
                item = pNames.nextElement();
                if(item.equals("appid")){
                    String appIdParam = request.getParameter("appid");
                    int appId = Integer.parseInt(appIdParam);
                    f = af.getAppFile(appId, fileName);
                    break;
                }
                if(item.equals("impid")){
                    String impIdParam = request.getParameter("impid");
                    int impId = Integer.parseInt(impIdParam);
                    f = af.getImpFile(impId, fileName);
                    break;
                }
                if(item.equals("idWE")){
                    String weIdParam = request.getParameter("idWE");
                    int idWE = Integer.parseInt(weIdParam);
                    wf = af.getWEFile(idWE, fileName);
                    break;
                }
            }
            
            if(f != null){
                response.setContentType("application/octet-stream");
                if(f.getSize() <= Integer.MAX_VALUE){
                    response.setContentLength(new Long(f.getSize()).intValue());
                }else{
                    //do nothing?
                }
                response.setHeader("Content-Disposition", "attachment; filename=\""+f.getFileName()+"\"");
                is = new BufferedInputStream(f.getStream(), BUFSIZE);
                os = new BufferedOutputStream(response.getOutputStream(), BUFSIZE);
                byte[] buf = new byte[BUFSIZE];
                int c = 0;
                while((c = is.read(buf)) >= 0){
                    if(c == 0){
                        try {
                            Thread.sleep(10); //sleep a little
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                    }
                    os.write(buf, 0, c);
                }
                os.flush();
            }else if (wf != null) {
                response.setContentType("application/octet-stream");
                FileInputStream fis = new FileInputStream(wf.getAbsolutePath());
                if(wf.length() <= Integer.MAX_VALUE){
                    response.setContentLength(((Long)wf.length()).intValue());
                }else{
                    //do nothing?
                }
                response.setHeader("Content-Disposition", "attachment; filename=\""+wf.getName()+"\"");
                is = new BufferedInputStream(fis, BUFSIZE);
                os = new BufferedOutputStream(response.getOutputStream(), BUFSIZE);
                byte[] buf = new byte[BUFSIZE];
                int c = 0;
                while((c = is.read(buf)) >= 0){
                    if(c == 0){
                        try {
                            Thread.sleep(10); //sleep a little
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                    }
                    os.write(buf, 0, c);
                }
                os.flush();
            }
            os.flush();
        } catch(NumberFormatException e){
            response.setStatus(400);                    
        } catch(Exception e){            
            // handle all EJB exceptions ValidationExcpetion, etc together
            Logger.getAnonymousLogger().severe(e.getMessage());
        } finally{            
            try{
                if(is != null){
                    is.close();
                }
                if(os != null){
                    os.close();
                }
            }catch(IOException e){
                Logger.getAnonymousLogger().severe(e.getMessage());
            }
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


