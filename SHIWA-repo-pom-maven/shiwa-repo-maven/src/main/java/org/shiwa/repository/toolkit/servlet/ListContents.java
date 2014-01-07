package org.shiwa.repository.toolkit.servlet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@WebServlet(name = "ListContents", urlPatterns = {"/workflows/*","/engines","/applications","/domains","/tasktypes","/groups", "/workflows_guest/*"})
public class ListContents extends HttpServlet {
    
    @EJB ApplicationFacadeLocal af;       

    // this holds the contents of the dummy repository
    ToolkitInterface repo;                  
    
    @Override
    public void init() throws ServletException {
        super.init();
        // building repo content
        repo = new ToolkitImplementation(af);
    }
    
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String path = request.getPathInfo();
        List<String> components = new ArrayList<String>();
        
        String resp = "";
        int resposneValue;
        
        if(path!=null){
            String[] cs = path.split("/");
            for (String component : cs) {
                if (component.length() > 0) {
                    components.add(component);
                }
            }
        }

        try {
            
            
            int userId = RepoUtil.getUserId(request, af);
        
            Iterator<String> pathIter = components.iterator();

            if(request.getServletPath().equals("/engines")) {
                resp += listEngines(userId);
            } else if (request.getServletPath().equals("/applications")) {
                resp += listAttribute("Application", userId);
            } else if (request.getServletPath().equals("/domains")) {
                resp += listAttribute("Domain", userId);
            } else if (request.getServletPath().equals("/tasktypes")) {
                resp += listAttribute("Tasktype", userId);
            } else if(request.getServletPath().equals("/groups")) {
                resp += listGroups(userId);            
            } else {
                resp += listItems(pathIter, userId);
            }
            
            resposneValue = 201;
        } catch (ForbiddenException e) {
            resp = e.getMessage();
            resposneValue = 403;
        } catch (NotFoundException e) {
            resp = e.getMessage();
            resposneValue = 404;
        } catch (UnauthorizedException e) {
            resp = e.getMessage();
            resposneValue = 401;
        } catch (Throwable e) {
            resp = e.getMessage();
            resposneValue = 401;
        } 

        if (resposneValue >= 400) {
            response.sendError(resposneValue, resp);
        } else {PrintWriter out = response.getWriter();
            try {
                out.println(resp);            
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
    }// </editor-fold>
    
    protected String listItems(Iterator<String> pathIter, int userId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        
//        if(af==null){
//            System.out.println("ListContents af is null");
//        }else{
//            System.out.println("ListContents af is not null");
//        }
       
        
        String item;
        if(pathIter==null || !pathIter.hasNext()) {
            //workflows to be listed
            return listWorkflows(false, userId);
        } else {
            item = pathIter.next();
            if(item.equals("modify")){
                //workflows to be listed that user can modify
                return listWorkflows(true, userId);                
            }
        }
        
        //imps or confs to be listed
        String workflowIdStr = item;
        if(pathIter.hasNext()) {
            item = pathIter.next();
            if(item.equals("imps")) {
                return listImplementations(workflowIdStr, userId);
            }
            if(item.equals("confs")) {
                return listConfigurations(workflowIdStr, userId);
            }
        }

        return "no items to list";
    }
    
    protected String listWorkflows(boolean modify, int userId) throws UnauthorizedException
    {
        String resp="";
        // generate workflow list

        List<WorkflowSummaryRTO> wfsList = repo.listWorkflows(userId, modify);
        Iterator<WorkflowSummaryRTO> iter = wfsList.iterator();

        resp += "ID"+RepoUtil.UNIT+"Title"+RepoUtil.UNIT+"Description"+RepoUtil.UNIT+"Keywords"+RepoUtil.RECORD;

        while(iter.hasNext()){
            resp += iter.next().toString()+ RepoUtil.RECORD;
        }
        
        return resp;
    }
    
    protected String listImplementations(String workflowIdStr, int userId) throws UnauthorizedException, ForbiddenException, NotFoundException
    {
        String resp="";
        // generate implementation list
        
        int workflowId = Integer.parseInt(workflowIdStr);
        List<ImplementationSummaryRTO> imsList = repo.listImplementations(userId, workflowId);
        Iterator<ImplementationSummaryRTO> iter = imsList.iterator();

        resp += "ID"+RepoUtil.UNIT+"Title"+RepoUtil.UNIT+"Version"+RepoUtil.UNIT+"Engine"+RepoUtil.UNIT+"Description"+RepoUtil.UNIT+"Language"+RepoUtil.UNIT+"Keywords"+RepoUtil.UNIT+"DCIs"+RepoUtil.RECORD;

        while(iter.hasNext()){
            resp += iter.next().toString()+RepoUtil.RECORD;
        }
        
        return resp;        
    }
    
    protected String listConfigurations(String workflowIdStr, int userId) {
        //TODO: change here, throw exception like other functions instead of try
        // - catch Exception 
        String resp = "";
        // generate implementation list
        try {
            int workflowId = Integer.parseInt(workflowIdStr);
            List<ConfigurationSummaryRTO> confsList = repo.listConfigurations(userId, workflowId);
            Iterator<ConfigurationSummaryRTO> iter = confsList.iterator();

            resp += "ID" + RepoUtil.UNIT + "Title" + RepoUtil.UNIT + "Description" + RepoUtil.RECORD;

            while (iter.hasNext()) {
                resp += iter.next().toString() + RepoUtil.RECORD;
            }
        } catch (Exception e) {
            resp += e.getClass() + ":" + e.getMessage();
        }
        
        return resp;
    }

    protected String listEngines(int userId) throws UnauthorizedException, NotFoundException
    {
        String resp="";
        // generate engine list
        
        List<EngineRTO> engsList = repo.getEngines(userId, null);
        if(engsList==null || engsList.isEmpty()) {
            resp += "No items found";
        } else {
            Iterator<EngineRTO> iter = engsList.iterator();
            resp += "ID"+RepoUtil.UNIT+"Title"+RepoUtil.RECORD+"Version"+RepoUtil.RECORD+"Description"+RepoUtil.RECORD;
            
            while(iter.hasNext()) {
                resp += iter.next().toString()+RepoUtil.RECORD;
            }
        } 
        
        return resp;
    }

    protected String listAttribute(String attribute, int userId) throws UnauthorizedException, NotFoundException {
        String resp="";
        
        List<String> aList = repo.getWFAttributeValues(userId, attribute.toLowerCase());
        
        if(aList==null || aList.isEmpty()) {
            resp += "No items found";
        } else {
            Iterator<String> iter = aList.iterator();
            resp += attribute + RepoUtil.RECORD;
            
            while(iter.hasNext()) {
                resp += iter.next() + RepoUtil.RECORD;
            }
        }
        return resp;
    }  

    protected String listGroups(int userId) throws UnauthorizedException, NotFoundException
    {
        String resp="";
        List<GroupRTO> aList = repo.getUsersGroups(userId);
        
        if (aList==null || aList.isEmpty()) {
            resp += "No items found";
        } else {
            for (GroupRTO anAList : aList) {
                resp += anAList.toString() + RepoUtil.RECORD;
            }
        }
        
        return resp;
    }
    
    
}