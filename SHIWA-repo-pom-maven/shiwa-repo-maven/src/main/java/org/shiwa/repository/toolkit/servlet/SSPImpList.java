package org.shiwa.repository.toolkit.servlet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.ejb.EJB;
import java.util.logging.*;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;
import uk.ac.wmin.edgi.repository.transferobjects.ImpEmbedTO;
import uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO;

/**
 *
 * @author kukla
 */
@WebServlet(name = "SSPImpList", urlPatterns = {"/sspimplist/*"})
public class SSPImpList extends HttpServlet {

    @EJB ApplicationFacadeLocal af;


    @Override
    public void init() throws ServletException {
        super.init();
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
            response.setContentType("text/html;charset=UTF-8");

        Logger.getLogger(SSPImpList.class.getName()).log(Level.INFO, "SSPLogin Params: sspUserId = " + request.getParameter("sspUserId") + ", sspServiceId = " + request.getParameter("sspServiceId"));
        if(request.getParameter("sspUserId")!=null && request.getParameter("sspServiceId")!=null){
            String sspUserId = request.getParameter("sspUserId");
            String sspServiceId = request.getParameter("sspServiceId");
            request.getSession().setAttribute("sspUserId", sspUserId);

            PrintWriter out = response.getWriter();

            try{

                Map<String,Boolean> resultMap = new TreeMap<String,Boolean>();
                List<Integer> gList = new ArrayList<Integer>();

                List<ImpEmbedTO> eList = af.listImpsForEmbedding(sspServiceId, sspUserId);


                //out.println("elements: "+eList.size());
                for(ImpEmbedTO e:eList){
                    gList.add(e.getImpId());
                    //out.println(e.getId()+", "+ e.getImpId()+", "+e.getExtUserId()+", "+e.getExtServiceId());
                }

                List<ImplementationTO> iList = new ArrayList<ImplementationTO>();
                //out.println("elements: "+iList.size());
                for(ImplementationTO i:iList){
                    // if gemlca id was not inserted before, then add
                    //out.println(i.getId()+"\tu"+ i.getGemlcaId());
                }

                //out.println("Selected GEMLCA IDs:");
                for(String gId:resultMap.keySet()){
                    out.println(gId+"\t"+resultMap.get(gId));
                }



            }catch(Exception e){
                out.println(e.getClass()+":"+e.getMessage());
            }


        }else{
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.print("did not receive parameter: '");
                if(request.getParameter("sspUserId")==null) {
                    out.print("sspUserId");
                }
                if(request.getParameter("sspServiceId")==null) {
                    out.print("sspServiceId");
                }
                out.println("'");
            } finally {
                out.close();
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