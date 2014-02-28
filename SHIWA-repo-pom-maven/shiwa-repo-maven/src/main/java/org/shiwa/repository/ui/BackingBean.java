/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import java.io.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.shiwa.repository.configuration.Backend;
import org.shiwa.repository.toolkit.transferobjects.ConfigurationNodeRTO;
import org.shiwa.repository.toolkit.transferobjects.ConfigurationRTO;
import org.shiwa.repository.toolkit.transferobjects.DependencyRTO;
import org.shiwa.repository.toolkit.transferobjects.PortRTO;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;
import uk.ac.wmin.edgi.repository.common.*;
import uk.ac.wmin.edgi.repository.transferobjects.*;
import org.shiwa.repository.ui.AttrTree.Node;
import uk.ac.wmin.repo.myexperiment.client.MyExperimentClient;
import org.shiwa.repository.toolkit.wfengine.*;
import uk.ac.wmin.edgi.repository.entities.Implementation;
import uk.ac.wmin.edgi.repository.entities.Platform;

/**
 *
 * @author zsolt
 */
@Named
@ManagedBean(name = "backingBean")
@SessionScoped
public class BackingBean implements Serializable {

    //PersistenceContext(unitName = "repo_proto_1PU")
    @Inject
    ApplicationFacadeLocal af;
    PlatformTO selectedPlatform;
    UserTO selectedUser;
    UserTO selectedGroupUser;
    GroupTO selectedGroup;
    GroupTO selectedUserGroup;
    GroupTO selectedUserLedGroup;
    ApplicationTO selectedApp;
    AttributeTO selectedAppAttr;
    ImplementationTO selectedImp;
    ImplementationTO selectedImpToValidate;
    AttributeTO selectedImpAttr;
    CommentTO selectedAppComm;
    CommentTO selectedImpComm;
    ImpFileTO[] selectedImpFiles = null;
    AppFileTO[] selectedAppFiles = null;
    NewUserBean newUser = new NewUserBean();
    NewGroupBean newGroup = new NewGroupBean();
    NewPlatformBean newPlatform = new NewPlatformBean();
    NewApplicationBean newApplication = new NewApplicationBean();
    NewImplementationBean newImplementation = new NewImplementationBean();
    NewAttributeBean newAppAttribute = new NewAttributeBean();
    NewAttributeBean newImpAttribute = new NewAttributeBean();
    NewCommentBean newAppComment = new NewCommentBean();
    NewCommentBean newImpComment = new NewCommentBean();
    String password = "";
    String userLoginNameToAddToGroup = "";
    UserTO currentUser = null;
    UserTO prevUser = null;
    String prevAccessUser = null;
    String err = "";
    String errLogs = "";
    String appAttrNameFilter = "";
    String appAttrValueFilter = "";
    String impAttrNameFilter = "";
    String impAttrValueFilter = "";
    String wfSearchStr = "";
    String prevWfSearchStr = "";
    String impSearchStr = "";
    String prevImpSearchStr = "";
    String selectedWfApplication = "";
    final String defaultWfDomain = "All Domains";
    String selectedWfDomain = defaultWfDomain;
    String selectedImpDomain = defaultWfDomain;
    String prevPageUrl = "";
    List<ApplicationTO> appListCache = null;
    Date appListCacheTimeStamp = null;
    List<ImplementationTO> impListCache = null;
    Date impListCacheTimeStamp = null;
    int impListHash;
    int appListHash;
    long impViewCount;
    long appViewCount;
    List<WorkflowSummary> wfSummaryListCache = null;
    List<ImplementationSummary> impSummaryListCache = null;
    AppAttrTree aTree = null;
    ImpAttrTree iTree = null;
    String myExpWorkflowID = "";
    String myExpWorkflowName = "";

    /*
     * emk
     * TODO: Check usage of created vars!!
     */

    Node displayNode = null;

    /* Workflow Engine - emk */
    Platform selectedWorkflowEngine = null;
    WEImplementation selectedWEImp = null;
    WEImplementation newWEImplementation = new WEImplementation();
    Platform newWorkflowEngine = new Platform();
    Collection<String> selectedJobManagers = new ArrayList<String>();

    /* WEFiles Management */
    WEUploadedFile selectedWEFile = null;
    WEUploadedFile newWEFile = new WEUploadedFile();
    WEUploadedFile currentWEFile = null;
    UploadedFile primeWEFile = null;
    WEUploadedFile[] selectedWEFiles = null;

    /* WEImplementation */
    String beiName = "";
    String backendCreationType = "";
    String backendSelString="";
    List<NewAttributeBean> backendAttribs = null;
    BeInstance selectedBEInstance = null;
    boolean showExistingBEI = true;
    int selectedBEIJobTypeId = 0;
    int newBEAbstractId = 0;
    int selectedBEInstanceId = 0;
    int newBEIoperatingSysId = 0;
    int newWEIZipId = 0;
    int newShellFileId = 0;
    Boolean showBEIDetails;
    Boolean changeEngineExec = false;

    //for implementation table
    String impWEIdString;
    int impWEId;

    //for workflow engine management
    List<Platform> wfEngineList = null;

    private MyExperimentManager manager = new MyExperimentManager(this);

    private ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) FacesContext.getCurrentInstance().getApplication().getNavigationHandler();
    private RatingContext ratingContext = RatingContext.create();
    private DomainContext domainContext = DomainContext.create(this);
    private static final String REPO_VERSION = "3.1";

    public BackingBean() {
        if (prevUser != currentUser) {
            //logAccess();
        }

    }

    public String initBackingBean() {
        /*if (prevUser != currentUser) {
            logAccess();
        }*/
        return "";
    }



    private void addWorkflowSummaryList(WorkflowSummary wf, List<WorkflowSummary> newWfList) {
        newWfList.add(wf);
    }

    private void addImplementationSummaryList(ImplementationSummary wf, List<ImplementationSummary> newWfList) {
        newWfList.add(wf);
    }

    private void redirect(String target) throws IOException {
        getExtCtx()
                    .redirect(
                ((HttpServletRequest)getExtCtx().getRequest()).getRequestURL()
                 +
                target);
    }

    private ExternalContext getExtCtx() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public void setWorkflowVisibility(boolean isAppPublic) {
        if (isAppPublic) {
            selectedApp.setOthersRead(true);
            selectedApp.setOthersDownload(true);
        }
        else {
//            selectedApp.setOthersRead(false);
//            selectedApp.setOthersDownload(false);
        }
    }



    /**
     * @return the ratingContext
     */
    public RatingContext getRatingContext() {
        ratingContext.setAf(af);
        return ratingContext;
    }

    /**
     * @param ratingContext the ratingContext to set
     */
    public void setRatingContext(RatingContext ratingContext) {
    }

    /**
     * @return the domainContext
     */
    public DomainContext getDomainContext() {
        return domainContext;
    }

    /**
     * @param domainContext the domainContext to set
     */
    public void setDomainContext(DomainContext domainContext) {
        this.domainContext = domainContext;
    }

    private void updateWorkflowSummary() {
        ListIterator<WorkflowSummary> it = getWorkflowSummaries().listIterator();
        while (it.hasNext()) {
            WorkflowSummary  wf = it.next();
            if (wf.getWorkflowId() == selectedApp.getId()) {
                it.set(this.getWorkflowSummary(wf.to));
                break;
            }
        }
    }

    private boolean matchingDomainOrSubdomain(WorkflowSummary wf) {
        return (wf.getDomain() != null && wf.getDomain().equals(selectedWfDomain))
                  ||
              (wf.getSubdomain() != null && wf.getSubdomain().equals(selectedWfDomain));
    }



    class AppSort implements Comparator<ApplicationTO> {

        @Override
        public int compare(ApplicationTO one, ApplicationTO two) {
            if (one == null || two == null) {
                return 0;
            }
            if (one.getPublished() == null || two.getPublished() == null) {
                return 0;
            }
            return two.getPublished().compareTo(one.getPublished());
        }
    }

    class ImpSort implements Comparator<ImplementationTO> {

        @Override
        public int compare(ImplementationTO one, ImplementationTO two){
            if (one == null || two == null) {
                return 0;
            }
            return 0;
        }

    }

    public void resetCachesIfUserChanged() {
        if (prevUser != currentUser) {
            resetCaches();
        }
    }

    public boolean isUserWEDev(){
        if(getCurrentUser() == null)
            return false;
        else
            return getCurrentUser().getWEDev();
    }

    public void resetCaches() {
        appListCache = null;
        appListCacheTimeStamp = null;
        impListCache = null;
        impListCacheTimeStamp = null;

        wfSummaryListCache = null;
        impSummaryListCache = null;
        prevUser = currentUser;
    }

    public String getPrevPageUrl() {
        return prevPageUrl;
    }

    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }

    public String getSelectedImpDomain() {
        return selectedImpDomain;
    }

    public void setSelectedImpDomain(String selectedImpDomain) {
        this.selectedImpDomain = selectedImpDomain;
    }

    public String getSelectedWfApplication() {
        return selectedWfApplication;
    }

    public void setSelectedWfApplication(String selectedWfApplication) {
        this.selectedWfApplication = selectedWfApplication;
    }

    public String getSelectedWfDomain() {
        return selectedWfDomain;
    }

    public void setSelectedWfDomain(String selectedWfDomain) {
        this.selectedWfDomain = selectedWfDomain.replaceAll("^-  ", "");
    }

    public String getWfSearchStr() {
        return wfSearchStr;
    }

    public void setWfSearchStr(String wfSearchStr) {
        this.wfSearchStr = wfSearchStr;
    }

    public String getImpSearchStr() {
        return impSearchStr;
    }

    public void setImpSearchStr(String impSearchStr) {
        this.impSearchStr = impSearchStr;
    }

    public String getImpAttrNameFilter() {
        return impAttrNameFilter;
    }

    public void setImpAttrNameFilter(String impAttrNameFilter) {
        this.impAttrNameFilter = impAttrNameFilter;
    }

    public String getImpAttrValueFilter() {
        return impAttrValueFilter;
    }

    public void setImpAttrValueFilter(String impAttrValueFilter) {
        this.impAttrValueFilter = impAttrValueFilter;
    }

    public String getAppAttrNameFilter() {
        return appAttrNameFilter;
    }

    public void setAppAttrNameFilter(String appAttrNameFilter) {
        this.appAttrNameFilter = appAttrNameFilter;
    }

    public String getAppAttrValueFilter() {
        return appAttrValueFilter;
    }

    public void setAppAttrValueFilter(String appAttrValueFilter) {
        this.appAttrValueFilter = appAttrValueFilter;
    }

    public String getErrorMessage() {
        return err;
    }

    public boolean hasErrorLogs() {
        return errLogs != null && !errLogs.isEmpty();
    }

    public String getErrorLogs() {
        //System.out.println("getErrorLogs invoked");
        return errLogs;
    }

    public NewAttributeBean getNewAppAttribute() {
        return newAppAttribute;
    }

    public void setNewAppAttribute(NewAttributeBean newAppAttribute) {
        this.newAppAttribute = newAppAttribute;
    }

    public NewCommentBean getNewAppComment() {
        return newAppComment;
    }

    public NewApplicationBean getNewApplication() {
        return newApplication;
    }

    public NewGroupBean getNewGroup() {
        return newGroup;
    }

    public NewAttributeBean getNewImpAttribute() {
        return newImpAttribute;
    }

    public NewCommentBean getNewImpComment() {
        return newImpComment;
    }

    public NewImplementationBean getNewImplementation() {
        return newImplementation;
    }

    public void setNewImplementation(ImplementationTO imp) {
        newImplementation.setPlatformName(imp.getPlatformName());
        newImplementation.setPlatformVersion(imp.getPlatformVersion());
        newImplementation.setVersion(imp.getVersion());
    }

    //put a message into growl
    void addMessage(String clientID, Severity severity, String summary, String detail) {
        FacesMessage message = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(clientID, message);
    }

    public UserTO getCurrentUser() {
        if (currentUser == null) {
            String loginName = getExtCtx().getRemoteUser();
            if (loginName != null) {
                currentUser = af.findUserByLoginName(loginName);
            }
        }
        //System.out.println("user: "+currentUser);
        return currentUser;
    }

    public String getSspUserID() {
        HttpSession session = (HttpSession) getExtCtx().getSession(false);
        if (session != null
                && session.getAttribute("sspUserId") != null
                && session.getAttribute("sspUserId").getClass() == String.class) {
            return (String) session.getAttribute("sspUserId");
        }
        return "unknown";
    }

    public String getSspServiceID() {
        HttpSession session = (HttpSession) getExtCtx().getSession(false);
        if (session != null
                && session.getAttribute("sspServiceId") != null
                && session.getAttribute("sspServiceId").getClass() == String.class) {
            return (String) session.getAttribute("sspServiceId");
        }
        return "unknown";
    }

    public boolean hasSessionSspUser() {
        return !getSspUserID().equals("unknown");
    }

    public boolean hasSessionSspService() {
        return !getSspServiceID().equals("unknown");
    }

    public boolean isSspSession() {
        return hasSessionSspUser() && hasSessionSspService();
    }

    public boolean isUserGuest() {
        getCurrentUser();
        return currentUser == null || !currentUser.getActive();
    }

    public String getUserLoginNameToAddToGroup() {
        return userLoginNameToAddToGroup;
    }

    public void setUserLoginNameToAddToGroup(String userLoginNameToAddToGroup) {
        this.userLoginNameToAddToGroup = userLoginNameToAddToGroup;
    }

    public PlatformTO getSelectedPlatform() {
        return selectedPlatform;
    }

    public void setSelectedPlatform(PlatformTO selectedPlatform) {
        this.selectedPlatform = selectedPlatform;
    }

    public NewPlatformBean getNewPlatform() {
        return newPlatform;
    }

    public ImplementationTO getSelectedImpToValidate() {
        return selectedImpToValidate;
    }

    public void setSelectedImpToValidate(ImplementationTO selectedImpToValidate) {
        this.selectedImpToValidate = selectedImpToValidate;
    }

    public boolean getCanReadPlatform() {
        if (selectedPlatform == null) {
            err = "Please select a platform.";
            return false;
        }
        return (err = af.canUserReadPlatform(getCurrentUser().getId(), selectedPlatform.getId())) == null;
    }

    public boolean getCanCreatePlatform() {
        return (err = af.canUserCreatePlatforms(getCurrentUser().getId())) == null;
    }

    public String createPlatform() {
        try {
            selectedPlatform = af.createPlatform(newPlatform.getName(), newPlatform.getVersion(), newPlatform.getDescription());
            newPlatform.clear();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created platform '" + selectedPlatform.getName() + "(" + selectedPlatform.getVersion() + ")'", null);
            return "success";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanUpdatePlatformDetails() {
        if (selectedPlatform == null) {
            err = "Please select a platform.";
            return false;
        }
        return (err = af.canUserUpdatePlatform(getCurrentUser().getId(), selectedPlatform.getId())) == null;
    }

    public String updatePlatformDetails() {
        if (selectedPlatform == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a platform", null);
            return null;
        }
        try {
            af.updatePlatform(selectedPlatform.getId(), selectedPlatform.getDescription());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Updated details of platform '" + selectedPlatform.getName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanDeletePlatform() {
        if (selectedPlatform == null) {
            err = "Please select a platform.";
            return false;
        }
        return (err = af.canUserDeletePlatform(getCurrentUser().getId(), selectedPlatform.getId())) == null;
    }

    public String deletePlatform() {
        if (selectedPlatform == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a platform", null);
            return null;
        }
        try {
            af.deletePlatform(selectedPlatform.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted platform '" + selectedPlatform.getName() + "'", null);
            selectedPlatform = null;
            return "success";
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (NotSafeToDeleteException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanReadUser() {
        if (selectedUser == null) {
            err = "Please select a user.";
            return false;
        }
        return (err = af.canUserReadUser(getCurrentUser().getId(), selectedUser.getId())) == null;
    }

    public boolean getCanCreateUser() {
        return (err = af.canUserCreateUsers(getCurrentUser().getId())) == null;
    }

    public String createUser() {
        try {
            selectedUser = af.createUser(
                    newUser.getLoginName(),
                    newUser.getPassword(),
                    newUser.getFullName(),
                    newUser.getOrganization(),
                    newUser.getEmailAddr(),
                    newUser.isActive(),
                    newUser.isAdmin(),
                    newUser.isWEDev());
            newUser.clear();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created user '" + selectedUser.getLoginName() + "'", null);
            return "success";
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanUpdateUserDetails() {
        if (selectedUser == null) {
            err = "Please select a user.";
            return false;
        }
        return (err = af.canUserUpdateUser(getCurrentUser().getId(), selectedUser.getId())) == null;
    }

    public String updateUserDetails() {
        if (selectedUser == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a user", null);
            return null;
        }
        try {
            selectedUser = af.updateUser(selectedUser);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Updated details of user '" + selectedUser.getLoginName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanChangeUserPassword() {
        if (selectedUser == null) {
            err = "Please select a user.";
        }
        return (err = af.canUserChangeUserPassword(getCurrentUser().getId(), selectedUser.getId())) == null;
    }

    public String changeUserPassword() {
        if (selectedUser == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a user", null);
            return null;
        }
        try {
            selectedUser = af.changeUserPassword(selectedUser.getId(), password);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Password changed for user '" + selectedUser.getLoginName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    /*
     public String deactivateUser(){
     if(selectedUser == null){
     addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a user", null);
     return null;
     }
     try{
     selectedUser = af.deactivateUser(selectedUser.getId());
     addMessage(null, FacesMessage.SEVERITY_INFO, "Deactivated user '"+selectedUser.getLoginName()+"'", null);
     return null;
     }catch(EntityNotFoundException e){
     addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
     } catch (AuthorizationException e) {
     addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
     }
     return null;
     }
     */
    /*
     String getCanUserRemoveGroupFromUser(){
     if(selectedUserGroup == null){
     return "Please select a group.";
     }
     //TODO
     }

     public String removeGroupFromUser(){
     if(selectedUser == null){
     addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a user", null);
     return null;
     }
     if(selectedUserGroup == null){
     addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a group", null);
     return null;
     }
     try{
     af.removeUserFromGroup(selectedUserGroup.getId(), selectedUser.getId());
     addMessage(null, FacesMessage.SEVERITY_INFO, "Removed user '"+selectedUser.getLoginName()+"' from group '"+selectedUserGroup.getName()+"'", null);
     selectedUserGroup = null;
     return null;
     }catch(EntityNotFoundException e){
     addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
     } catch (AuthorizationException e) {
     addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
     }
     return null;
     }
     */
    public boolean getCanDeleteUser() {
        if (selectedUser == null) {
            err = "Please select a user.";
            return false;
        }
        return (err = af.canUserDeleteUser(getCurrentUser().getId(), selectedUser.getId())) == null;
    }

    public String deleteUser() {
        if (selectedUser == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a user", null);
            return null;
        }
        try {
            af.deleteUser(selectedUser.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted user '" + selectedUser.getLoginName() + "'", null);
            selectedUser = null;
            return "success";
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (NotSafeToDeleteException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanReadGroup() {
        if (selectedGroup == null) {
            err = "Please select a group.";
        }
        err = af.canUserReadGroup(getCurrentUser().getId(), selectedGroup.getId());
        return err == null;
    }

    public boolean getCanCreateGroup() {
        return (err = af.canUserCreateGroups(getCurrentUser().getId())) == null;
    }

    public String createGroup() {
        try {
            selectedGroup = af.createGroup(newGroup.getName());
            newGroup.clear();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created group '" + selectedGroup.getName() + "'", null);
            return "success";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanChangeGroupLeader() {
        if (selectedGroup == null) {
            err = "Please select a group.";
            return false;
        }
        return (err = af.canUserChangeGroupLeader(getCurrentUser().getId(), selectedGroup.getId())) == null;
    }

    public String changeGroupLeader() {
        if (selectedGroup == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a group", null);
            return null;
        }
        try {
            selectedGroup = af.changeGroupLeader(selectedGroup.getId(), selectedGroup.getLeaderLoginName());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Changed owner of group '" + selectedGroup.getName() + "' to '" + selectedGroup.getLeaderLoginName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanAddUsersToGroup() {
        if (selectedGroup == null) {
            err = "Please select a group.";
            return false;
        }
        return (err = af.canUserAddUsersToGroup(getCurrentUser().getId(), selectedGroup.getId())) == null;
    }

    public String addUserToGroup() {
        if (selectedGroup == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a group", null);
            return null;
        }
        if (userLoginNameToAddToGroup == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a user", null);
            return null;
        }
        try {
            af.addUserToGroup(selectedGroup.getId(), userLoginNameToAddToGroup);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Added user '" + userLoginNameToAddToGroup + "' to group '" + selectedGroup.getName() + "'", null);
            return null;
        } catch (ValidationFailedException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanRemoveUsersFromGroup() {
        if (selectedGroup == null) {
            err = "Please select a group.";
            return false;
        }
        return (err = af.canUserRemoveUsersFromGroup(getCurrentUser().getId(), selectedGroup.getId())) == null;
    }

    public String removeUserFromGroup() {
        if (selectedGroup == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a group", null);
            return null;
        }
        if (selectedGroupUser == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a user", null);
            return null;
        }
        try {
            af.removeUserFromGroup(selectedGroup.getId(), selectedGroupUser.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Removed user '" + selectedGroupUser.getLoginName() + "' from group '" + selectedGroup.getName() + "'", null);
            selectedGroupUser = null;
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanDeleteGroup() {
        if (selectedGroup == null) {
            err = "Please select a group.";
        } else {
            err = af.canUserDeleteGroup(getCurrentUser().getId(), selectedGroup.getId());
        }
        return err == null;
    }

    public String deleteGroup() {
        if (selectedGroup == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a group", null);
            return null;
        }
        try {
            af.deleteGroup(selectedGroup.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted group '" + selectedGroup.getName() + "'", null);
            selectedGroup = null;
            return "success";
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (NotSafeToDeleteException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanReadApplication() {
        if (selectedApp == null) {
            err = "Please select an application.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = af.canPublicReadApplication(selectedApp.getId());
            } else {
                err = af.canUserReadApplication(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public boolean getCanCreateApplications() {
        return (err = af.canUserCreateApplications(getCurrentUser().getId())) == null;
    }

    public String createApplication() {
        try {
            selectedApp = af.createApp(newApplication.getName(), newApplication.getDescription(), newApplication.getGroupName(), false, false, false, false, false, false);
            newApplication.clear();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created application '" + selectedApp.getName() + "'", null);
            return "success";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanUpdateApp(ApplicationTO app) {
        if (app == null) {
            err = "Please select an application.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot update application.";
            } else {
                err = af.canUserUpdateAppDetails(getCurrentUser().getId(), app.getId());
            }
        }
        return err == null;
    }

    public boolean getCanUpdateAppDetails() {
        if (selectedApp == null) {
            err = "Please select an application.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot update application.";
            } else {
                err = af.canUserUpdateAppDetails(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public String updateAppDetails() {
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application", null);
            return null;
        }
        try {
            selectedApp = af.updateAppDetails(selectedApp.getId(), selectedApp.getDescription(), selectedApp.getName());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Updated application '" + selectedApp.getName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanChangeAppOwner() {
        if (selectedApp == null) {
            err = "Please select an application.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot change application owner.";
            } else if (selectedApp.getOwnerLoginName().equals(getCurrentUser().getLoginName()) && selectedApp.getPublished()) {
                err = "Validated workflows cannot be modified. Please contact an administrator.";
            } else {
                err = af.canUserChangeAppOwner(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public String changeAppOwner() {
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application", null);
            return null;
        }
        try {
            selectedApp = af.changeAppOwner(selectedApp.getId(), selectedApp.getOwnerLoginName());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Changed owner of application '" + selectedApp.getName() + "' to '" + selectedApp.getOwnerLoginName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanUpdateAppAccess() {
        if (selectedApp == null) {
            err = "Please select a workflow.";
            return false;
        }

        if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot update workflow access.";
        } else {
                err = af.canUserUpdateAppAccess(getCurrentUser().getId(), selectedApp.getId());
        }
        return err == null;
    }

    public String getPublishedFriendlyName(boolean published) {
        return published == true ? "public" : "private";
    }

    public String updateAppAccess() {
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an workflow", null);
            return null;
        }

        boolean isPublic = selectedApp.getPublished();

        if ((selectedApp.getGroupDownload() && !selectedApp.getGroupRead())
                || selectedApp.getOthersDownload() && !selectedApp.getOthersRead()) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Cannot make workflow downloadable if it is not readable.", null);
            return null;
        }
        if (selectedApp.getGroupModify() && !selectedApp.getGroupRead()) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Cannot make workflow modifiable if it is not readable.", null);
            return null;
        }
        try {

            setWorkflowVisibility(isPublic);


            selectedApp = af.updateAppAccess(selectedApp.getId(),
                    selectedApp.getGroupName(),
                    selectedApp.getGroupRead(),
                    selectedApp.getOthersRead(),
                    selectedApp.getGroupDownload(),
                    selectedApp.getOthersDownload(),
                    selectedApp.getGroupModify(),
                    selectedApp.getPublished());

            addMessage(null, FacesMessage.SEVERITY_INFO, "Changed access rights of workflow '" + selectedApp.getName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanPublishApp() {
        return this.getCanUpdateAppAccess();
//        if (selectedApp == null) {
//            err = "Please select a workflow.";
//        } else {
//            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
//                err = "Cannot publish workflow.";
//            } else {
//                err = af.canUserPublishApp(getCurrentUser().getId(), selectedApp.getId());
//            }
//        }
//        return err == null;
    }

    public boolean getCanDeleteApplication() {
        if (selectedApp == null) {
            err = "Please select an application.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot delete application.";
            } else {
                err = af.canUserDeleteApplication(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public String deleteApplication() {
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application", null);
            return null;
        }
        try {
            af.deleteApp(selectedApp.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted application '" + selectedApp.getName() + "'", null);

            /*
             * Enable this code for performance increase on WE deletion
            if(appListCache.remove(selectedApp))
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Attempted quick removal of app from listCache: success!");
            else
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Attempted quick removal of app from listCache: failiure!");

            if(wfSummaryListCache.remove(getWorkflowSummary(selectedApp)))
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Attempted quick removal of app from summaryCache: success!");
            else
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Attempted quick removal of app from summaryCache: failiure!");
            */
            selectedApp = null;
            resetCaches();
            return "success";
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (NotSafeToDeleteException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (FileOperationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (Exception e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Server Error: " + e.getMessage(), null);
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Have you deleted associated implementations?", null);
        }
        return null;
    }

    public boolean getCanReadAppAttribute() {
        if (selectedAppAttr == null) {
            err = "Please select an application attribute.";
        } else {
            err = af.canUserReadAppAttr(getCurrentUser().getId(), selectedAppAttr.getId());
        }
        return err == null;
    }

    public boolean getCanCreateAppAttributes() {
        if (selectedApp == null) {
            err = "Please select an application.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot create application attributes.";
            } else {
                err = af.canUserCreateAppAttrs(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public String createAppAttribute() {
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application", null);
            return null;
        }
        try {
            selectedAppAttr = af.createAppAttr(selectedApp.getId(), newAppAttribute.getName(), newAppAttribute.getValue());
            newAppAttribute.clear();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created attribute '" + selectedAppAttr.getName() + "' for application '" + selectedApp.getName() + "'", null);
            return "success";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanUpdateAppAttribute() {
        if (selectedAppAttr == null) {
            err = "Please select an application attribute.";
        } else {
            err = af.canUserUpdateAppAttr(getCurrentUser().getId(), selectedAppAttr.getId());
        }
        return err == null;
    }

    public String updateAppAttribute() {
        if (selectedAppAttr == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application attribute", null);
            return null;
        }
        try {
            selectedAppAttr = af.updateAppAttr(selectedAppAttr.getId(), selectedAppAttr.getValue());

            addMessage(null, FacesMessage.SEVERITY_INFO, "Updated application attribute '" + selectedAppAttr.getName() + "'", null);
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanDeleteAppAttribute() {
        if (selectedAppAttr == null) {
            err = "Please select an application attribute.";
        } else {
            err = af.canUserDeleteAppAttr(getCurrentUser().getId(), selectedAppAttr.getId());
        }
        return err == null;
    }

    public String deleteAppAttribute() {
        if (selectedAppAttr == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application attribute", null);
            return null;
        }
        try {
            af.deleteAppAttr(selectedAppAttr.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted application attribute '" + selectedAppAttr.getName() + "'", null);
            selectedAppAttr = null;
            return "success";
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanReadImplementation() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = af.canPublicReadImplementation(selectedImp.getId());
            } else {
                err = af.canUserReadImp(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public boolean getCanCreateImplementations() {
        if (selectedApp == null) {
            err = "Please select an application.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot create implementations.";
            } else {
                err = af.canUserCreateImps(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public String getImpWEIdString() {
        return impWEIdString;
    }

    public void setImpWEIdString(String impWEIdString) {
        this.impWEIdString = impWEIdString;
    }

    public int getImpWEId() {
        return impWEId;
    }

    public void setImpWEId(int impWEId) {
        this.impWEId = impWEId;
    }

    public void handleImpWEIdString(){
        this.impWEIdString = newImplementation.getPlatformName();
    }

    public String createImplementation() {
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application", null);
            return null;
        }
        if (newImplementation.getPlatformName() != null && newImplementation.getPlatformName().length() == 0) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an engine", null);
            return null;
        }
        if (newImplementation.getPlatformVersion() != null && newImplementation.getPlatformVersion().length() == 0) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an engine version", null);
            return null;
        }
        if (newImplementation.getVersion() != null && newImplementation.getVersion().length() < 3) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Implementation version should be at least 3 character long", null);
            return null;
        }
        try {
            selectedImp = af.createImp(selectedApp.getId(), newImplementation.getPlatformName(), newImplementation.getPlatformVersion(), newImplementation.getVersion());
            newImplementation.clear();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created implementation version '" + selectedImp.getVersion() + "' on platform '" + selectedImp.getPlatformName() + "' for application '" + selectedApp.getName() + "'", null);
            return "success";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanUpdateImplementation(ImplementationTO imp) {
        if (imp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot update implementaion";
            } else {
                err = af.canUserUpdateImp(getCurrentUser().getId(), imp.getId());
            }
        }
        return err == null;
    }

    public boolean getCanUpdateImplementation() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot update implementaion";
            } else {
                err = af.canUserUpdateImp(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public String updateImplementation() {
        return updateImplementation(false);
    }

    public String updateImplementation(boolean silentOnSuccess) {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }

        try {
            selectedImp = af.updateVersionedImp(selectedImp.getId(), selectedImp.getPlatformName(), selectedImp.getPlatformVersion(), selectedImp.getVersion());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            if (!silentOnSuccess) {
                addMessage(null, FacesMessage.SEVERITY_INFO, "Updated details of implementation" + selectedImp.getVersion() + " (" + selectedImp.getPlatformName() + ")", null);
            }
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanSubmitImplementation() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot submit implementation";
            } else {
                err = af.canUserSubmitImp(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public String submitImplementation() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            selectedImp = af.submitImp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Submitted implementation for validation", null);
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public String validateImplementation() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            if (this.validateImp) {
                selectedImp = af.validateImp(selectedImp.getId());
                addMessage(null, FacesMessage.SEVERITY_INFO, "Implementation set to public", null);
            }
            else {
                selectedImp = af.failImp(selectedImp.getId());
                addMessage(null, FacesMessage.SEVERITY_INFO, "Implementation set to private", null);
            }

            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }

            if (!selectedApp.getPublished() && selectedImp.getPublic()){
                FacesContext.getCurrentInstance().validationFailed();
            }
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public String failImplementation() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            selectedImp = af.failImp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Failed implementation", null);
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanMarkImplementationOld() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot mark implementation old";
            } else {
                err = af.canUserMarkImpOld(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public String markImplementationOld() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            selectedImp = af.markImpOld(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Marked implementation as 'old'", null);
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanMarkImplementationDeprecated() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot mark implementaion deprecated";
            } else {
                err = af.canUserMarkImpDeprecated(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public String markImplementationDeprecated() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            selectedImp = af.markImpDeprecated(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Marked implementation as 'deprecated'", null);
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanMarkImplementationCompromised() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot mark implementaion comporomised";
            } else {
                err = af.canUserMarkImpCompromised(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public String markImplementationCompromised() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            selectedImp = af.markImpCompromised(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Marked implementation as 'compromised'", null);
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanDeleteImplementation() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot delete implementaion";
            } else {
                err = af.canUserDeleteImp(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public String deleteImplementation() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            af.deleteImp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }

            selectedImp = null;
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted implementation", null);
            resetCaches();
            return "success";
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (FileOperationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public boolean getCanReadImpAttribute() {
        if (selectedImpAttr == null) {
            err = "Please select an implementation attribute.";
        } else {

            err = af.canUserReadImpAttr(getCurrentUser().getId(), selectedImpAttr.getId());
        }
        return err == null;
    }

    public boolean getCanCreateImpAttributes() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot create implementaion attributes";
            } else {
                err = af.canUserCreateImpAttrs(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public String createImpAttribute() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            selectedImpAttr = af.createImpAttr(selectedImp.getId(), newImpAttribute.getName(), newImpAttribute.getValue());
            af.updateImpTimestamp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            newImpAttribute.clear();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created attribute '" + selectedImpAttr.getName() + "' for implementation", null);
            return "success";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanUpdateImpAttribute() {
        if (selectedImpAttr == null) {
            err = "Please select an implementation attribute.";
        } else {
            err = af.canUserUpdateImpAttr(getCurrentUser().getId(), selectedImpAttr.getId());
        }
        return err == null;
    }

    public String updateImpAttribute() {
        if (selectedImpAttr == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation attribute", null);
            return null;
        }
        try {
            selectedImpAttr = af.updateImpAttr(selectedImpAttr.getId(), selectedImpAttr.getValue());
            af.updateImpTimestamp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Updated implementation attribute '" + selectedImpAttr.getName() + "'", null);
            return null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanDeleteImpAttribute() {
        if (selectedImpAttr == null) {
            err = "Please select an implementation attribute.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot delete file.";
            } else {
                err = af.canUserDeleteImpAttr(getCurrentUser().getId(), selectedImpAttr.getId());
            }
        }
        return err == null;
    }

    public String deleteImpAttribute() {
        if (selectedImpAttr == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation attribute", null);
            return null;
        }
        try {
            af.deleteImpAttr(selectedImpAttr.getId());
            af.updateImpTimestamp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted implementation attribute '" + selectedImpAttr.getName() + "'", null);
            selectedImpAttr = null;
            return "success";
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public boolean getCanUploadImpFile() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot upload file.";
            } else {
                err = af.canUserUploadImpFile(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public boolean getCanUploadAppFile() {
        if (selectedApp == null) {
            err = "Please select a workflow.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot upload file.";
            } else {
                err = af.canUserUploadAppFile(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public void handleImpFileUpload(FileUploadEvent event) {
        //logger.info("Uploaded: {}", event.getFile().getFileName());
        //TODO: finish this
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return;
        }
        try {
            ImpFileTO f = af.uploadImpFile(selectedImp.getId(), event.getFile().getFileName(), event.getFile().getInputstream());
            af.updateImpTimestamp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Uploaded file '" + f.getPathName() + "'", null);
        } catch (IOException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: could not upload file", null);
        } catch (FileOperationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        //return null; //no navigation result
    }

    public void handleAppFileUpload(FileUploadEvent event) {
        //logger.info("Uploaded: {}", event.getFile().getFileName());
        //TODO: finish this
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application", null);
            return;
        }
        try {
            AppFileTO f = af.uploadAppFile(selectedApp.getId(), event.getFile().getFileName(), event.getFile().getInputstream());
            af.updateAppTimestamp(selectedApp.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Uploaded file '" + f.getPathName() + "'", null);
        } catch (IOException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: could not upload file", null);
        } catch (FileOperationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        //return null; //no navigation result
    }

    public boolean getCanDownloadImpFiles() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = af.canPublicDownloadImpFile(selectedImp.getId());
            } else {
                err = af.canUserDownloadImpFile(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public boolean getCanDownloadImpFiles(ImplementationSummary imp) {
        if (imp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = af.canPublicDownloadImpFile(imp.getId());
            } else {
                err = af.canUserDownloadImpFile(getCurrentUser().getId(), imp.getId());
            }
        }
        return err == null;
    }

    public boolean getCanDownloadWfFiles(WorkflowSummary wf) {
        if (wf == null) {
            err = "Please select a workflow.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = af.canPublicDownloadAppFile(wf.getWorkflowId());
            } else {
                err = af.canUserDownloadAppFile(getCurrentUser().getId(), wf.getWorkflowId());
            }
        }
        return err == null;
    }

    public boolean getCanDownloadAppFiles() {
        if (selectedApp == null) {
            err = "Please select a workflow.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = af.canPublicDownloadAppFile(selectedApp.getId());
            } else {
                err = af.canUserDownloadAppFile(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public ImpFileTO[] getSelectedImpSelectedFiles() {
        return selectedImpFiles;
    }

    public void setSelectedImpSelectedFiles(ImpFileTO[] selectedImpFiles) {
        this.selectedImpFiles = selectedImpFiles;
    }

    public AppFileTO[] getSelectedAppSelectedFiles() {
        return selectedAppFiles;
    }

    public void setSelectedAppSelectedFiles(AppFileTO[] selectedAppFiles) {
        this.selectedAppFiles = selectedAppFiles;
    }

    public boolean getCanDeleteImpFiles() {
        if (selectedImp == null) {
            err = "Please select an implementation.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot delete files.";
            } else {
                err = af.canUserDeleteImpFiles(getCurrentUser().getId(), selectedImp.getId());
            }
        }
        return err == null;
    }

    public boolean getCanDeleteAppFiles() {
        if (selectedApp == null) {
            err = "Please select a workflow.";
        } else {
            if (getCurrentUser() == null || getCurrentUser().getId() == null) {
                err = "Cannot delete files.";
            } else {
                err = af.canUserDeleteAppFiles(getCurrentUser().getId(), selectedApp.getId());
            }
        }
        return err == null;
    }

    public String deleteImpFiles() {
        if (selectedImpFiles == null || selectedImpFiles.length == 0) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select one or more files", null);
            return null;
        }
        int i = 0;
        try {
            for (ImpFileTO f : selectedImpFiles) {
                af.deleteImpFile(f.getImpId(), f.getPathName());
                i++;
            }
            af.updateImpTimestamp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted " + i + " files", null);
            selectedImpFiles = null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        } catch (FileOperationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        }
        return null; //no navigation result
    }

    public String deleteAppFiles() {
        if (selectedAppFiles == null || selectedAppFiles.length == 0) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select one or more files", null);
            return null;
        }
        int i = 0;
        try {
            for (AppFileTO f : selectedAppFiles) {
                af.deleteAppFile(f.getAppId(), f.getPathName());
                i++;
            }
            af.updateAppTimestamp(selectedApp.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted " + i + " files", null);
            selectedImpFiles = null;
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        } catch (FileOperationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage() + "(" + i + " files deleted)", null);
        }
        return null; //no navigation result
    }

    public UserTO getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(UserTO selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GroupTO getSelectedUserLedGroup() {
        return selectedUserLedGroup;
    }

    public void setSelectedUserLedGroup(GroupTO selectedUserLedGroup) {
        this.selectedUserLedGroup = selectedUserLedGroup;
    }

    public NewUserBean getNewUser() {
        return newUser;
    }

    public ApplicationTO getSelectedApp() {
        return selectedApp;
    }

    public void setSelectedApp(ApplicationTO selectedApp) {

        this.selectedApp = selectedApp;
    }

    public AttributeTO getSelectedAppAttr() {
        return selectedAppAttr;
    }

    public void setSelectedAppAttr(AttributeTO selectedAppAttr) {
        this.selectedAppAttr = selectedAppAttr;
    }

    public CommentTO getSelectedAppComm() {
        return selectedAppComm;
    }

    public void setSelectedAppComm(CommentTO selectedAppComm) {
        this.selectedAppComm = selectedAppComm;
    }

    public GroupTO getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(GroupTO selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public UserTO getSelectedGroupUser() {
        return selectedGroupUser;
    }

    public void setSelectedGroupUser(UserTO selectedGroupUser) {
        this.selectedGroupUser = selectedGroupUser;
    }

    public ImplementationTO getSelectedImp() {
        return selectedImp;
    }

    public void setSelectedImp(ImplementationTO selectedImp) {
        this.selectedImp = selectedImp;
        //System.out.print("selected imp:"+selectedImp.getId());

        if (selectedImp != null && selectedImp.getAppId() != null) {
            this.selectedApp = af.loadApplication(selectedImp.getAppId());
        }
    }

    public AttributeTO getSelectedImpAttr() {
        return selectedImpAttr;
    }

    public void setSelectedImpAttr(AttributeTO selectedImpAttr) {
        this.selectedImpAttr = selectedImpAttr;
    }

    public CommentTO getSelectedImpComm() {
        return selectedImpComm;
    }

    public void setSelectedImpComm(CommentTO selectedImpComm) {
        this.selectedImpComm = selectedImpComm;
    }

    public GroupTO getSelectedUserGroup() {
        return selectedUserGroup;
    }

    public void setSelectedUserGroup(GroupTO selectedUserGroup) {
        this.selectedUserGroup = selectedUserGroup;
    }

    //used by autocomplete when adding a user to a group
    public List<String> completeAddUserToGroupUserLoginName(String query) {
        return af.filterAddUserToGroupUserLoginNames(selectedGroup.getId(), query);
    }

    //used by autocomplete when creating a new application
    public List<String> completeNewAppGroupName(String query) {
        return af.filterAppGroupNames(query);
    }

    //used by autocomplete when changing app owner
    public List<String> completeAppOwnerName(String query) {
        return af.filterAppOwnerLoginNames(query);
    }

    public List<String> appOwnerByApp(String query){
        return af.filterAppOwnerLoginNamesByGroup(query, af.getGroup(selectedApp.getGroupName()).getId());
    }

    //used by autocomplete when filtering for AppAttrNames
    public List<String> completeAppAttrName(String query) {
        return af.filterAppAttrNames(query);
    }

    //used by autocomplete when filtering for ImpAttrNames
    public List<String> completeImpAttrName(String query) {
        return af.filterImpAttrNames(query);
    }

    //used by autocomplete when creating a new implementation
    public List<String> completeNewImpPlatformName(String query) {
        return af.filterImpPlatformNames(query);
    }

    //used by autocomplete when changing group leader name
    public List<String> completeGroupLeaderName(String query) {
        return af.filterGroupLeaderLoginNames(query);
    }

    public List<PlatformTO> getPlatforms() {
        return af.listPlatforms();
    }

    public UserTO loadUser(String username) {
        return af.loadUser(username);
    }

    public String[] getPlatformNames() {
        List<PlatformTO> platforms = getPlatforms();
        ArrayList<String> platformNames = new ArrayList<String>();
        Iterator<PlatformTO> iter = platforms.iterator();
        PlatformTO item;
        while (iter.hasNext()) {
            item = iter.next();
            if (item != null && !platformNames.contains(item.getName())) {
                platformNames.add(item.getName());
            }
        }
        return platformNames.toArray(new String[0]);
    }

    public void update() {
        //System.out.println("selected");
    }

    public String[] getPlatformVersionsForNew() {
        if (newImplementation == null) {
            //System.out.println("noNewImplementation");
            return null;
        }
        //System.out.println("versionList");
        List<PlatformTO> platforms = af.listPlatformsByName(newImplementation.getPlatformName());
        String[] platformNames = new String[platforms.size()];
        Iterator<PlatformTO> iter = platforms.iterator();
        PlatformTO item;
        for (int i = 0; i < platformNames.length; i++) {
            item = iter.next();
            if (item != null) {
                platformNames[i] = item.getVersion();
            }

        }
        return platformNames;
    }

    public String[] getPlatformVersionsForSelected() {
        if (selectedImp == null) {
            //System.out.println("noNewImplementation");
            return null;
        }
        //System.out.println("versionList");
        List<PlatformTO> platforms = af.listPlatformsByName(selectedImp.getPlatformName());
        String[] platformNames = new String[platforms.size()];
        Iterator<PlatformTO> iter = platforms.iterator();
        PlatformTO item;
        for (int i = 0; i < platformNames.length; i++) {
            item = iter.next();
            if (item != null) {
                platformNames[i] = item.getVersion();
            }

        }
        return platformNames;
    }

    public void onPlatformsRowSelectNavigate(SelectEvent event) {
        //TODO auth checks
        //af.findUserByLoginName()
        nav.performNavigation("/user/edit-platform?faces-redirect=true");
    }

    public List<UserTO> getUsers() {
        return af.listUsers();
    }

    public void onUsersRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/user/edit-user?faces-redirect=true");
    }

    public List<UserTO> getUsersOfSelectedGroup() {
        return af.listUsersOfGroup(selectedGroup.getId());
    }

    public void onUsersOfSelectedGroupRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/user/edit-user?faces-redirect=true");
    }

    public List<GroupTO> getGroups() {
        return af.listGroups();
    }

    public GroupTO getGroupByName(String name){
        return af.getGroup(name);
    }


    public void onGroupsRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/user/edit-group?faces-redirect=true");
    }

    public List<GroupTO> getGroupsOfSelectedUser() {
        return af.listGroupsOfUser(selectedUser.getId());
    }

    public void onGroupsOfSelectedUserRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/user/edit-group?faces-redirect=true");
    }

    public List<GroupTO> getOwnedGroupsOfSelectedUser() {
        return af.listOwnedGroupsOfUser(selectedUser.getId());
    }

    public void onOwnedGroupsOfSelectedUserRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/user/edit-group?faces-redirect=true");
    }

    public List<GroupTO> getMyGroups() {
        return af.listMyGroups();
    }

    public List<GroupTO> getMyOwnedGroups() {
        return af.listMyOwnedGroups();
    }

    public void onMyGroupsRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/user/edit-group?faces-redirect=true");
    }

    public String[] getWfApplications() {
        List<AttributeTO> aList = af.getAppAttributesByKey("application");
        List<String> appList = new ArrayList<String>();
        for (AttributeTO attr : aList) {
            if (attr != null
                    && !attr.getValue().equals("")
                    && !appList.contains(attr.getValue())) {
                appList.add(attr.getValue());
            }
        }
        return appList.toArray(new String[0]);
    }

    public String[] getWfDomains() {
//        List<AttributeTO> aList = af.getAppAttributesByKey("domain");


        List<String> appList = new ArrayList<String>();
        appList.add("All Domains");
//        appList.addAll(domainContext.getDomainHandler().extractDomains());
        for (String domain : domainContext.getDomainHandler().extractDomains())  {
            appList.add(domain);
            String[] subdomains = domainContext.getDomainHandler().extractSubdomains(domain).toArray(new String[0]);
            Arrays.sort(subdomains);

            for(String subdomain: subdomains) {
                appList.add("-  " + subdomain);
            }
        }

//        for (AttributeTO attr : aList) {
//            if (attr != null
//                    && !attr.getValue().equals("")
//                    && !appList.contains(attr.getValue())) {
//                appList.add(attr.getValue());
//            }
//        }
        return appList.toArray(new String[0]);
    }

    public List<String> completeAppAttribute(String query) {
        if (aTree == null
                || aTree.selectedNode == null
                || aTree.selectedNode.getValue() == null) {
            return null;
        }

        List<AttributeTO> aList = af.getAppAttributesByKeyAndFilter(aTree.selectedNode.getValue(), query);
        List<String> attList = new ArrayList<String>();
        for (AttributeTO attr : aList) {
            if (attr != null
                    && !attr.getValue().equals("")
                    && !attList.contains(attr.getValue())) {
                attList.add(attr.getValue());
            }
        }
        return attList;
    }

    public List<String> completeImpAttribute(String query) {
        if (iTree == null
                || iTree.selectedNode == null
                || iTree.selectedNode.getValue() == null) {
            return null;
        }

        List<AttributeTO> aList = af.getImpAttributesByKeyAndFilter(iTree.selectedNode.getValue(), query);
        List<String> attList = new ArrayList<String>();
        for (AttributeTO attr : aList) {
            if (attr != null
                    && !attr.getValue().equals("")
                    && !attList.contains(attr.getValue())) {
                attList.add(attr.getValue());
            }
        }
        return attList;
    }

    public void filterWfSummaries(ActionEvent actionEvent) {

        String searchStr = wfSearchStr;
        if (selectedWfDomain.equals(defaultWfDomain) && searchStr.isEmpty()) {
            wfSummaryListCache = getAllWorkflowSummaries();
        }else{
            if(!prevWfSearchStr.equals(wfSearchStr)){
                appListHash = 0;
            }
            List<WorkflowSummary> wfList = getAllWorkflowSummaries();
            List<WorkflowSummary> newWfList = new ArrayList<WorkflowSummary>();
            for (WorkflowSummary wf : wfList) {
                if (!selectedWfDomain.equals(defaultWfDomain)) {
                    if (matchingDomainOrSubdomain(wf)
                            &&
                         wf.find(searchStr)) {
                        addWorkflowSummaryList(wf, newWfList);
                    }
                }
                else if (wf.find(searchStr)) {
                    addWorkflowSummaryList(wf, newWfList);
                }
            }
            prevWfSearchStr = wfSearchStr;
            wfSummaryListCache = newWfList;
        }

        Collections.sort(wfSummaryListCache);
    }

    public void refreshWfSummaries(ActionEvent actionEvent) {
        //System.out.println("search: "+searchStr);
        appListHash = 0;
        selectedWfDomain = defaultWfDomain;
        wfSummaryListCache = getAllWorkflowSummaries();
    }

    public void filterImpSummaries(ActionEvent actionEvent) {
        //System.out.println("search: "+searchStr);
        String searchStr = impSearchStr;

        if(searchStr.isEmpty() && selectedImpDomain.equals(defaultWfDomain)){
            impSummaryListCache = getAllImpSummaries();
        }else{
            if(!prevImpSearchStr.equals(impSearchStr)){
                impListHash = 0;
            }
            if (!selectedImpDomain.equals(defaultWfDomain)) {
                searchStr += " " + selectedImpDomain;
            }

            List<ImplementationSummary> impList = getAllImpSummaries();
            List<ImplementationSummary> newImpList = new ArrayList<ImplementationSummary>();
            for (ImplementationSummary imp : impList) {
                if (imp.find(searchStr)) {
                    this.addImplementationSummaryList(imp, newImpList);
                }
            }
            prevImpSearchStr = impSearchStr;
            impSummaryListCache = newImpList;
        }
    }
    public void refreshImpSummaries(ActionEvent actionEvent) {
        //System.out.println("search: "+searchStr);
        impListHash = 0;
        selectedImpDomain = defaultWfDomain;
        impSummaryListCache = getAllImpSummaries();
    }

    public List<ApplicationTO> getApplicationsPrivate() {
        return appListCache = af.listApplicationsPrivate();
    }

    public List<ApplicationTO> getApplicationsReadyForValidation() {
        return appListCache = af.listApplicationsReadyForValidation();
    }

    public List<ApplicationTO> getApplications() {
        resetCachesIfUserChanged();
        Date appTimestamp = af.appTimestamp();
        if (appListCacheTimeStamp == null || appListCacheTimeStamp.before(appTimestamp)) {

            appListCacheTimeStamp = appTimestamp;
            appListCache = af.listApplicationsUserCanRead();

            // to put validated workflows first a sort has to be performed
            // in the jpql query sorting boolean threw an exception saying
            // boolean is not an orderable type
            Collections.sort(appListCache, new AppSort());

            if(isUserGuest()){
                ArrayList<ApplicationTO> guestAppListCache = new ArrayList<ApplicationTO>();
                for(ApplicationTO temp : appListCache){
                    if(getWorkflowSummary(temp).getStatus().equalsIgnoreCase("public")){
                        guestAppListCache.add(temp);
                    }
                }
                //swap lists to stop concurrent access exception
                appListCache = guestAppListCache;
            }
        }

        return appListCache;
    }

    public List<ImplementationTO> getImplementations() {

        resetCachesIfUserChanged();
        Date impTimestamp = af.impTimestamp();
        if (impListCacheTimeStamp == null || impListCacheTimeStamp.before(impTimestamp)) {
            impListCacheTimeStamp = impTimestamp;

            impListCache = af.listImplementationsUserCanRead();
            if(isUserGuest()){
                ArrayList<ImplementationTO> newImpListCache = new ArrayList<ImplementationTO>();
                for(ImplementationTO temp : impListCache){
                    if(getImpSummary(temp).getStatus().equalsIgnoreCase("public")){
                        newImpListCache.add(temp);
                    }
                }
                //swap lists to ensure concurrent access
                impListCache = newImpListCache;
                Collections.sort(impListCache, new ImpSort());
            }
        }

        return impListCache;
    }

    public String truncateForTable(String a){
        a = (a.length() < 30) ? a : a.substring(0, 29) + "...";
        return a;
    }

    public List<ImplementationSummary> getImplementationSummaries() {
        resetCachesIfUserChanged();

        filterImpSummaries(null);

        return impSummaryListCache;
    }

    public List<String> getImplementationFullNames(String workflowName) {
        List<String> impFullNames = new LinkedList<String>();
        if (workflowName == null || workflowName.isEmpty()) {
            return impFullNames;
        }
        for (ImplementationSummary impSummary : getImplementationSummaries()) {
            if (impSummary.getFullName().startsWith(workflowName)) {
                impFullNames.add(impSummary.getFullName());
            }
        }
        Collections.sort(impFullNames);
        return impFullNames;
    }

    public List<ImplementationSummary> getAllImpSummaries() {
        List<ImplementationTO> iList = getImplementations();

        /*
         * FIXME: This may mean that if a change occurs on the impListCache
         * then the user refreshes the table the impSummaryListCache will not
         * be refreshed when the summaries are loaded.
         */
        if(impSummaryListCache == null || impListHash != impListCache.hashCode()){
            Iterator<ImplementationTO> iIter = iList.iterator();
            ImplementationTO implementationTO;
            List<ImplementationSummary> isList = new ArrayList<ImplementationSummary>();

            while (iIter.hasNext()) {
                implementationTO = iIter.next();
                isList.add(getImpSummary(implementationTO));
            }
            impListHash = impListCache.hashCode();
            return isList;
        }else{
            return impSummaryListCache;
        }
    }

    public List<WorkflowSummary> getWorkflowSummaries() {
        resetCachesIfUserChanged();
        filterWfSummaries(null);
        return wfSummaryListCache;
    }

    public List<WorkflowSummary> getAllWorkflowSummaries() {
        List<ApplicationTO> aList = getApplications();
        if(wfSummaryListCache == null || appListHash != appListCache.hashCode()){
            Iterator<ApplicationTO> aIter = aList.iterator();
            ApplicationTO applicationTO;
            List<WorkflowSummary> wsList = new ArrayList<WorkflowSummary>();

            while (aIter.hasNext()) {
                applicationTO = aIter.next();
                wsList.add(getWorkflowSummary(applicationTO));
            }
            appListHash = appListCache.hashCode();
            return wsList;
        }else{
            return wfSummaryListCache;
        }

    }

    public ImplementationSummary getImpSummaryOfSelImp() {
        if (selectedImp == null) {
            return null;
        }
        return getImpSummary(selectedImp);
    }

    public ImplementationSummary getImpSummary(ImplementationTO implementationTO) {
        //get data from attribute table
        List<String> dciDeps = new ArrayList<String>();
        List<String> dciConfs = new ArrayList<String>();
        List<AttributeTO> attributes = af.listAttributesOfImplementation(implementationTO.getId());
        Iterator<AttributeTO> aIter = attributes.iterator();
        List<String> depIDs = new ArrayList<String>();
        List<DependencyRTO> deps = new ArrayList<DependencyRTO>();
        List<ConfigurationRTO> confs = new ArrayList<ConfigurationRTO>();
        List<Integer> confIDs = new ArrayList<Integer>();
        String confId;
        ConfigurationNodeRTO newNode;
        String depId;
        AttributeTO aItem;
        String title = "";
        String keywords = "";
        String description = "";
        String[] depKey;
        String graph = "";
        String definition = "";
        String licence = "";
        String rights = "";
        String language = "";
        String validator = "";

        while (aIter.hasNext()) {
            aItem = aIter.next();
            if (aItem.getValue().equals("DCI")
                    && aItem.getName().startsWith("dependencies.")
                    && aItem.getName().endsWith(".type")) {
                dciDeps.add(aItem.getName().split("\\.")[1]);
            }
            if (aItem.getName().equals("title")) {
                title = aItem.getValue();
            }
            if (aItem.getName().equals("description")) {
                description = aItem.getValue();
            }
            if (aItem.getName().equals("keywords")) {
                keywords = aItem.getValue();
            }
            if (aItem.getName().equals("graph")) {
                graph = aItem.getValue();
            }
            if (aItem.getName().equals("definition")) {
                definition = aItem.getValue();
            }
            if (aItem.getName().equals("licence")) {
                licence = aItem.getValue();
            }
            if (aItem.getName().equals("rights")) {
                rights = aItem.getValue();
            }
            if (aItem.getName().equals("language")) {
                language = aItem.getValue();
            }

            //see if we have a dependency
            if (aItem.getName().startsWith("dependencies.")
                    && aItem.getName().split("\\.").length > 2) {
                //get id
                depId = aItem.getName().split("\\.")[1];

                //if the dependency was not inserted before, then add
                if (!depIDs.contains(depId)) {
                    depIDs.add(depId);
                    deps.add(new DependencyRTO(depId, "", "", ""));
                }

                //insert value
                Iterator<DependencyRTO> dIter = deps.iterator();
                DependencyRTO dItem;
                while (dIter.hasNext()) {
                    dItem = dIter.next();
                    if (dItem.getName().equals(depId)) {
                        if (aItem.getName().endsWith(".type")) {
                            dItem.setType(aItem.getValue());
                        }
                        if (aItem.getName().endsWith(".description")) {
                            dItem.setDescription(aItem.getValue());
                        }
                        if (aItem.getName().endsWith(".title")) {
                            dItem.setTitle(aItem.getValue());
                        }
                    }
                }
            }

            // see if we have a configuration
            if (aItem.getName().startsWith("configurations.")
                    && aItem.getName().split("\\.").length > 2) {
                confId = aItem.getName().split("\\.")[1];
                //System.out.println(aItem.getName());
                try {
                    //map int to string for conf id
                    int cId = Integer.parseInt(confId.replaceFirst("^.*\\D", ""));

                    //if the conf was not inserted before, then add
                    if (!confIDs.contains(cId)) {
                        confIDs.add(cId);
                        confs.add(new ConfigurationRTO(cId, ConfigurationRTO.ConfigurationType.DEPENDENCY));
                    }

                    Iterator<ConfigurationRTO> cIter = confs.iterator();
                    ConfigurationRTO cItem;
                    while (cIter.hasNext()) {
                        cItem = cIter.next();
                        if (cItem.getId() == cId) {
                            // if description then add description to conf
                            if (aItem.getName().endsWith(".description")) {
                                cItem.setDescription(aItem.getValue());
                                // if not description then dependency, so insert a new dep
                            } else {
                                newNode = new ConfigurationNodeRTO(aItem.getName().split("\\.")[2], aItem.getValue(), "");
                                cItem.getConfigurationNodes().add(newNode);
                            }
                        }
                    }

                } catch (Exception e) {/*if cannot get integer, then don't insert the object*/

                }
            }
        }

        aIter = attributes.iterator();
        while (aIter.hasNext()) {
            aItem = aIter.next();
            if (aItem.getName().startsWith("configurations.")
                    && aItem.getName().split("\\.").length > 2
                    && dciDeps.contains(aItem.getName().split("\\.")[2])
                    && !dciConfs.contains(aItem.getValue())) {
                dciConfs.add(aItem.getValue());
            }
            if (aItem.getName().equals("keywords")) {
                keywords = aItem.getValue();
            }
        }

        ImplementationSummary implementationSummary = new ImplementationSummary(
                implementationTO.getId(),
                implementationTO.getAppName(),
                implementationTO.getVersion(),
                implementationTO.getPlatformName(),
                implementationTO.getPlatformVersion(),
                implementationTO.getCreated(),
                implementationTO.getUpdated(),
                title,
                description,
                keywords,
                dciConfs,
                graph,
                definition,
                licence,
                rights,
                language,
                "0",
                implementationTO.getStatusFriendlyName(),
                deps,
                confs,
                implementationTO,
                getWorkflowSummary(af.loadApplication(implementationTO.getAppId()), false),
                implementationTO.isSubmittable());
        return implementationSummary;
    }

    public WorkflowSummary getSelectedWf() {
        //System.out.println("selWf: "+selectedApp.getName());
        return getWorkflowSummary(selectedApp);
    }

    public WorkflowSummary getWorkflowSummary(ApplicationTO applicationTO) {
        return getWorkflowSummary(applicationTO, true);
    }

    public WorkflowSummary getWorkflowSummary(ApplicationTO applicationTO, boolean withImps) {
        if (applicationTO == null) {
            return null;
        }

        // getting keywords form app attrs
        List<AttributeTO> attrList = af.listAttributesOfApplication(applicationTO.getId());
        Iterator<AttributeTO> iter = attrList.iterator();
        List<String> inportIDs = new ArrayList<String>();
        List<PortRTO> inports = new ArrayList<PortRTO>();
        List<String> outportIDs = new ArrayList<String>();
        List<PortRTO> outports = new ArrayList<PortRTO>();
        List<ConfigurationRTO> confs = new ArrayList<ConfigurationRTO>();
        List<Integer> confIDs = new ArrayList<Integer>();
        String confId;
        ConfigurationNodeRTO newNode;
        String inportId;
        String outportId;
        AttributeTO item;
        String keywords = "";
        String domain = "";
        String subdomain = "";
        String application = "";
        String owner;
        String group;
        List<ImplementationSummary> isList = new ArrayList<ImplementationSummary>();

        while (iter.hasNext()) {
            item = iter.next();
            if (item.getName().equals("keywords")) {
                keywords = item.getValue();
            }
            if (item.getName().equals("application")) {
                application = item.getValue();
            }
            if (item.getName().equals("domain")) {
                domain = item.getValue();
            }
            if (item.getName().equals("subdomain")) {
                subdomain = item.getValue();
            }
            //see if we have an inport
            if (item.getName().startsWith("inports.")
                    && item.getName().split("\\.").length > 2) {
                //get id
                inportId = item.getName().split("\\.")[1];

                //if the port was not inserted before, then add
                if (!inportIDs.contains(inportId)) {
                    inportIDs.add(inportId);
                    inports.add(new PortRTO(inportId, "", "", "", false));
                }

                //insert value
                Iterator<PortRTO> pIter = inports.iterator();
                PortRTO pItem;
                while (pIter.hasNext()) {
                    pItem = pIter.next();
                    if (pItem.getName().equals(inportId)) {
                        if (item.getName().endsWith(".datatype")) {
                            pItem.setType(item.getValue());
                        }
                        if (item.getName().endsWith(".description")) {
                            pItem.setDescription(item.getValue());
                        }
                        if (item.getName().endsWith(".title")) {
                            pItem.setValue(item.getValue());
                        }
                    }
                }
            }

            //see if we have an outport
            if (item.getName().startsWith("outports.")
                    && item.getName().split("\\.").length > 2) {
                //get id
                outportId = item.getName().split("\\.")[1];

                //if the port was not inserted before, then add
                if (!outportIDs.contains(outportId)) {
                    outportIDs.add(outportId);
                    outports.add(new PortRTO(outportId, "", "", "", false));
                }

                //insert value
                Iterator<PortRTO> pIter = outports.iterator();
                PortRTO pItem;
                while (pIter.hasNext()) {
                    pItem = pIter.next();
                    if (pItem.getName().equals(outportId)) {
                        if (item.getName().endsWith(".datatype")) {
                            pItem.setType(item.getValue());
                        }
                        if (item.getName().endsWith(".description")) {
                            pItem.setDescription(item.getValue());
                        }
                        if (item.getName().endsWith(".title")) {
                            pItem.setValue(item.getValue());
                        }
                    }
                }
            }

            // see if we have a configuration
            if (item.getName().startsWith("configurations.")
                    && item.getName().split("\\.").length > 2) {
                confId = item.getName().split("\\.")[1];
                //System.out.println(aItem.getName());
                try {
                    //map int to string for conf id
                    int cId = Integer.parseInt(confId.replaceFirst("^.*\\D", ""));

                    //if the conf was not inserted before, then add
                    if (!confIDs.contains(cId)) {
                        confIDs.add(cId);
                        confs.add(new ConfigurationRTO(cId, ConfigurationRTO.ConfigurationType.PORT));
                    }

                    Iterator<ConfigurationRTO> cIter = confs.iterator();
                    ConfigurationRTO cItem;
                    while (cIter.hasNext()) {
                        cItem = cIter.next();
                        if (cItem.getId() == cId) {
                            // if description then add description to conf
                            if (item.getName().endsWith(".description")) {
                                cItem.setDescription(item.getValue());
                                // if not description then dependency, so insert a new dep
                            } else {
                                newNode = new ConfigurationNodeRTO(item.getName().split("\\.")[2], item.getValue(), "");
                                cItem.getConfigurationNodes().add(newNode);
                            }
                        }
                    }

                } catch (Exception e) {/*if cannot get integer, then don't insert the object*/

                }
            }
        }

        group = applicationTO.getGroupName();
        owner = applicationTO.getOwnerLoginName();

        // generating imp summary list
        if (withImps) {
            List<ImplementationTO> impToList = af.listImplementationsOfApplicationUserCanRead(applicationTO.getId());
            Iterator<ImplementationTO> impToIter = impToList.iterator();
            ImplementationTO impTO;
            while (impToIter.hasNext()) {
                impTO = impToIter.next();
                isList.add(getImpSummary(impTO));
            }
        }

        WorkflowSummary workflowSummary = new WorkflowSummary(
                applicationTO.getId(),
                applicationTO.getName(),
                applicationTO.getDescription(),
                getPublishedFriendlyName(applicationTO.getPublished()),
                applicationTO.getCreated(),
                applicationTO.getUpdated(),
                keywords,
                domain,
                application,
                owner,
                group,
                inports,
                outports,
                confs,
                isList,
                applicationTO);

        if (!subdomain.isEmpty()) {
            workflowSummary.setSubdomain(subdomain);
        }
        return workflowSummary;
    }

    public String duplicateImplementation() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "No implementation provided", null);
            return null;
        }
        /*
         if(selectedImp.getPlatformName()!=null && newImplementation.getPlatformName().length()==0){
         addMessage(null, FacesMessage.SEVERITY_WARN, "No engine provided", null);
         return null;
         }
         if(newImplementation.getPlatformVersion()!=null && newImplementation.getPlatformVersion().length()==0){
         addMessage(null, FacesMessage.SEVERITY_WARN, "No engine version provided", null);
         return null;
         }
         if(newImplementation.getVersion()!=null && newImplementation.getVersion().length()<3){
         addMessage(null, FacesMessage.SEVERITY_WARN, "Implementation version should be at least 3 character long", null);
         return null;
         }
         *
         */
        try {
            // generating new version name
            String newVersion;
            int oldImpId = selectedImp.getId();
            if (selectedImp.getVersion().length() >= 45) {
                newVersion = selectedImp.getVersion().substring(0, 45) + "_copy";
            } else {
                newVersion = selectedImp.getVersion() + "_copy";
            }

            //getting file list
            List<ImpFileTO> fileList = getFilesOfSelectedImplementation();
            // inserting new implementation
            selectedImp = af.createImp(selectedImp.getAppId(), selectedImp.getPlatformName(), selectedImp.getPlatformVersion(), newVersion);
            // colying attributes
            List<AttributeTO> iAttrList = af.listAttributesOfImplementation(oldImpId);
            List<AttributeTO> itemsToRemove = new ArrayList<AttributeTO>();
            for (AttributeTO iAttr : iAttrList) {
                if (iAttr.getName().contains("Submission Execution Node.deployer")) {
                    itemsToRemove.add(iAttr);
                }
            }
            iAttrList.removeAll(itemsToRemove);

            af.updateImpAttrList(selectedImp.getId(), iAttrList);
            // copying files
            for (ImpFileTO impFile : fileList) {
                af.uploadImpFile(selectedImp.getId(), impFile.getPathName(), af.getImpFile(oldImpId, impFile.getPathName()).getStream());
            }
            resetCaches();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Duplication successful. Created implementation version '" + selectedImp.getVersion() + "' on platform '" + selectedImp.getPlatformName() + "' for application '" + selectedApp.getName() + "'.", null);
            return "success";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (FileOperationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }

        return null;
    }

    public void onApplicationsRowSelectNavigate(Object app) {
        //TODO: auth checks
        if(app instanceof ApplicationTO){
            nav.performNavigation("/public/edit-application?appid=" + ((ApplicationTO)app).getId() + "&faces-redirect=true");
        }else if(app instanceof WorkflowSummary){
            nav.performNavigation("/public/edit-application?appid=" + ((WorkflowSummary)app).getWorkflowId() + "&faces-redirect=true");
        }else{
            nav.performNavigation("/public/edit-application?appid=" + selectedApp.getId() + "&faces-redirect=true");
        }
    }

    public void onWorkflowSummarySelectNavigate(WorkflowSummary app) {
        nav.performNavigation("/public/details-view?appid=" + app.getWorkflowId()  + "&faces-redirect=true");
    }

    public List<ApplicationTO> getApplicationsOfSelectedUser() {
        return af.listApplicationsOfUser(selectedUser.getId());
    }

    public void onApplicationsOfSelectedUserRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-application?appid=" + selectedApp.getId() + "&faces-redirect=true");
    }

    public List<ApplicationTO> getApplicationsOfSelectedGroup() {
        return af.listApplicationsOfGroup(selectedGroup.getId());
    }

    public void onApplicationsOfSelectedGroupRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-application?appid=" + selectedApp.getId() + "&faces-redirect=true");
    }

    public List<ApplicationTO> getMyApplications() {
        return af.listMyApplications();
    }

    public void onMyApplicationsRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-application?appid=" + selectedApp.getId() + "&faces-redirect=true");
    }

    public List<ImplementationTO> getImplementationsOfSelectedApplication() {
        return af.listImplementationsOfApplication(selectedApp.getId());
    }

    public Implementation getImpById(int id){
        return af.getImpById(id);
    }

    public void onImplementationsOfSelectedApplicationRowSelectNavigate(Object appImp) {
        //TODO: auth checks
        if(appImp instanceof ImplementationTO){
            nav.performNavigation("/public/edit-implementation?impid=" + ((ImplementationTO)appImp).getId() + "&faces-redirect=true");
        } else {
            nav.performNavigation("/public/edit-implementation?impid=" + selectedImp.getId() + "&faces-redirect=true");
        }
    }

    public List<ImplementationTO> getImplementationsOfSelectedPlatform() {
        return af.listImplementationsOfPlatform(selectedPlatform.getId());
    }

    public void onImplementationsOfSelectedPlatformRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-implementation?impid=" + selectedImp.getId() + "&faces-redirect=true");
    }

    public void onValidatedImplementationsOfSelectedUserRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-implementation?impid=" + selectedImp.getId() + "&faces-redirect=true");
    }

    public List<ImplementationTO> getImplementationsReadyForValidation() {
        return af.listImplementationsReadyForValidation();
    }
    /*
     public String onImplementationsReadyForValidationRowSelectNavigate(SelectEvent event){
     //TODO: auth checks
     return "/public/edit-implementation?faces-redirect=true";
     }
     */

    public void onImplementationsReadyForValidationRowSelectNavigate(SelectEvent event) {
        nav.performNavigation("/public/edit-implementation?impid=" + selectedImp.getId() + "&faces-redirect=true");
    }

    public List<ImplementationTO> getMyValidatedImplementations() {
        return af.listMyValidatedImplementations();
    }

    public void onMyValidatedImplementationsRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-implementation?impid=" + selectedImp.getId() + "&faces-redirect=true");
    }

    public List<AttributeTO> getAttributesOfSelectedApplication() {
        return af.listAttributesOfApplication(selectedApp.getId());
    }

    public void onAttributesOfSelectedApplicationRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-application-attribute?faces-redirect=true");
    }

    public List<AttributeTO> getAttributesOfSelectedImplementation() {
        return af.listAttributesOfImplementation(selectedImp.getId());
    }

    public void onAttributesOfSelectedImplementationRowSelectNavigate(SelectEvent event) {
        //TODO: auth checks
        nav.performNavigation("/public/edit-implementation-attribute?faces-redirect=true");
    }

    public List<ImpFileTO> getFilesOfSelectedImplementation() {
        if (selectedImp != null
                && selectedImp.getId() != null) {
            return af.listFilesOfImplementation(selectedImp.getId());
        }
        return null;
    }

    public List<AppFileTO> getFilesOfSelectedApplication() {
        if (selectedApp != null
                && selectedApp.getId() != null) {
            return af.listFilesOfApplication(selectedApp.getId());
        }
        return null;
    }

    public String[] getFileNamesOfSelectedAppication() {
        List<AppFileTO> fileTOList = getFilesOfSelectedApplication();
        // if no files
        if (fileTOList == null || fileTOList.size() < 1) {
            return null;
        }
        String[] fileNameList = new String[fileTOList.size()];
        Iterator<AppFileTO> fileTOIter = fileTOList.iterator();
        for (int i = 0; fileTOIter.hasNext(); i++) {
            //System.out.println("file"+i+": "+fileTOIter.next().getPathName());
            fileNameList[i] = fileTOIter.next().getPathName();
        }
        return fileNameList;
    }

    public String[] getFileNamesOfAppication(int id) {
        List<AppFileTO> fileTOList = af.listFilesOfApplication(id);
        // if no files
        if (fileTOList == null || fileTOList.size() < 1) {
            return null;
        }
        String[] fileNameList = new String[fileTOList.size()];
        Iterator<AppFileTO> fileTOIter = fileTOList.iterator();
        for (int i = 0; fileTOIter.hasNext(); i++) {
            //System.out.println("file"+i+": "+fileTOIter.next().getPathName());
            fileNameList[i] = fileTOIter.next().getPathName();
        }
        return fileNameList;
    }

    public String[] getFileNamesOfImplementation(int id) {
        List<ImpFileTO> fileTOList = af.listFilesOfImplementation(id);
        // if no files
        if (fileTOList == null || fileTOList.size() < 1) {
            return null;
        }
        String[] fileNameList = new String[fileTOList.size()];
        Iterator<ImpFileTO> fileTOIter = fileTOList.iterator();
        for (int i = 0; fileTOIter.hasNext(); i++) {
            //System.out.println("file"+i+": "+fileTOIter.next().getPathName());
            fileNameList[i] = fileTOIter.next().getPathName();
        }
        return fileNameList;
    }

    public String[] getFileNamesOfSelectedImplementation() {
        List<ImpFileTO> fileTOList = getFilesOfSelectedImplementation();
        // if no files
        if (fileTOList == null || fileTOList.size() < 1) {
            return null;
        }
        String[] fileNameList = new String[fileTOList.size()];
        Iterator<ImpFileTO> fileTOIter = fileTOList.iterator();
        for (int i = 0; fileTOIter.hasNext(); i++) {
            //System.out.println("file"+i+": "+fileTOIter.next().getPathName());
            fileNameList[i] = fileTOIter.next().getPathName();
        }
        return fileNameList;
    }

    public Boolean doesAppFileExist(Node node) {
        String[] fileNames = getFileNamesOfSelectedAppication();
        if (node != null
                && fileNames != null) {
            for (int i = 0; i < fileNames.length; i++) {
                if (fileNames[i] != null && fileNames[i].equals(node.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean doesWfFileExist(WorkflowSummary wf, String file) {
        String[] fileNames = getFileNamesOfAppication(wf.getWorkflowId());
        if (fileNames != null) {
            for (int i = 0; i < fileNames.length; i++) {
                if (fileNames[i] != null && fileNames[i].equals(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean doesImpFileExist(Node node) {
        String[] fileNames = getFileNamesOfSelectedImplementation();
        if (node != null
                && fileNames != null) {
            for (int i = 0; i < fileNames.length; i++) {
                if (fileNames[i] != null && fileNames[i].equals(node.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean doesImpFileExist(ImplementationSummary imp, String file) {
        String[] fileNames = getFileNamesOfImplementation(imp.getId());
        if (fileNames != null) {
            for (int i = 0; i < fileNames.length; i++) {
                if (fileNames[i] != null && fileNames[i].equals(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<CommentTO> getCommentsOfSelectedApplication() {
        return af.listCommentsOfApplication(selectedApp.getId());
    }

    public List<CommentTO> getCommentsOfSelectedImplementation() {
        return af.listCommentsOfImplementation(selectedImp.getId());
    }

    public String updateMyDetails() {
        if (currentUser == null) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "No current user", null);
            return null;
        }
        try {
            currentUser = af.updateUser(currentUser);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Updated your details", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public String changeMyPassword() {
        if (currentUser == null) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "No current user", null);
            return null;
        }
        try {
            currentUser = af.changeUserPassword(currentUser.getId(), password);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Password changed for user '" + currentUser.getLoginName() + "'", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null;
    }

    public AppAttrTree getAppAttrTree() {
        //update tree
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an application", null);
            return null;
        }
        if (aTree == null) {
            aTree = new AppAttrTree(af.listAttributesOfApplication(selectedApp.getId()));
        }
        if (aTree != null && aTree.getItemID() != selectedApp.getId()) {
            Boolean expanded = false;
            if (aTree != null) {
                expanded = aTree.getExpanded();
            }
            aTree = new AppAttrTree(af.listAttributesOfApplication(selectedApp.getId()));
            aTree.setExpanded(expanded);
            aTree.setItemID(selectedApp.getId());
        }

        return aTree;
    }

    public void reloadApplicationAttributeTree() {
        Boolean expanded = false;
        if (aTree != null) {
            expanded = aTree.getExpanded();
        }
        aTree = new AppAttrTree(af.listAttributesOfApplication(selectedApp.getId()));
        aTree.setExpanded(expanded);
        aTree.setItemID(selectedApp.getId());
        addMessage(null, FacesMessage.SEVERITY_INFO, "Attribute table reloaded", null);
    }

    public String updateAttributesOfSelectedApplication() {
        if (selectedApp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a workflow", null);
            return null;
        }
        try {

            af.updateAppAttrList(selectedApp.getId(), aTree.getAttrList());
            af.updateAppTimestamp(selectedApp.getId());
            updateWorkflowSummary();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Attribute table saved", null);
            return null;
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public String selectImpForEmbedding() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a workflow implementation", null);
            return null;
        }
        try {

            af.createImpEmbed(selectedImp.getId(), getSspUserID(), getSspServiceID());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Implementation selected for embedding", null);
            return null;
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public String removeImpForEmbedding() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a workflow implementation", null);
            return null;
        }
        try {

            af.removeImpEmbed(selectedImp.getId(), getSspUserID(), getSspServiceID());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Item removed from the list of workflow implementations for embedding", null);
            return null;
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public Boolean isImpSelectedForEmbedding(Integer impId) {
        return af.isImpSelectedForEmbedding(impId, getSspUserID(), getSspServiceID());
    }

    /*
     * TODO: Replace with code that validates execution node for use
     * by the submission service.
     */
    public Boolean isSelectedImpRunnable(){
        return selectedImp.isSubmittable();
    }

    public int getPercentPopImp(ImplementationTO imp){
        double x;

        if(imp.getViews() == 0 || impViewCount == 0){
            return 0;
        }else{
            x = ((double)imp.getViews() / (double)impViewCount) * 100;
        }

        if(x < 1)
            return 1;

        return (int) x;

    }

    public int getPercentPopApp(ApplicationTO app){
        double x;

        if(app.getViews() == 0 || appViewCount == 0){
            return 0;
        }else{
            x = ((double)app.getViews() / (double)appViewCount) * 100;
        }

        if(x < 1)
            return 1;

        return (int) x;
    }

    public void toggleSubmittable(){
        try {
            selectedImp = af.toggleSubmittable(selectedImp.getId());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Implementation " + selectedImp.getAppName() + " (" + selectedImp.getVersion() + ") Submittable: " + selectedImp.isSubmittable() , null);
        } catch (EntityNotFoundException ex) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + ex.getMessage(), null);
        } catch (AuthorizationException ex) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + ex.getMessage(), null);
        } catch (ValidationFailedException ex) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + ex.getMessage(), null);
        }
    }

    public boolean isPlatformSubmittable(){
        return af.getPlatformSubmittable(selectedImp.getPlatformName(), selectedImp.getPlatformVersion());
    }

    public void togglePlatformSubmittable(){
        try {
            af.togglePlatformSubmittable(selectedWorkflowEngine);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Workflow Engine " + selectedWorkflowEngine.getName() + "(" + selectedWorkflowEngine.getVersion() + ")'s submittable flag has been set to: " + selectedWorkflowEngine.isSubmittable(), null);
        } catch (AuthorizationException ex) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + ex.getMessage(), null);
        } catch (ValidationFailedException ex) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + ex.getMessage(), null);
        }
    }

    public void toggleWEImpEnabled(WEImplementation weimp){
        try{
            af.toggleWEImpEnabled(weimp);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Workflow Engine Implementation " + weimp.getNameWEImp() + "'s enabled flag has been set to: " + weimp.isEnabled(), null);
        }catch(AuthorizationException ex){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + ex.getMessage(), null);
        }catch (ValidationFailedException ex) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + ex.getMessage(), null);
        }
    }

    public ImpAttrTree getImpAttrTree() {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        if (iTree == null) {
            iTree = new ImpAttrTree(af.listAttributesOfImplementation(selectedImp.getId()));
        }
        if (iTree != null && iTree.getItemID() != selectedImp.getId()) {
            Boolean expanded = false;
            if (iTree != null) {
                expanded = iTree.getExpanded();
            }
            iTree = new ImpAttrTree(af.listAttributesOfImplementation(selectedImp.getId()));
            iTree.setExpanded(expanded);
            iTree.setItemID(selectedImp.getId());
            iTree.setItem(selectedImp);
            iTree.setApplication(selectedApp);
            iTree.setUser(currentUser);
            aTree = new AppAttrTree(af.listAttributesOfApplication(selectedImp.getAppId()));
            iTree.setApThree(aTree);
        }
        return iTree;
    }

    public void reloadImplementationAttributeTree() {
        Boolean expanded = false;
        if (iTree != null) {
            expanded = iTree.getExpanded();
        }
        iTree = new ImpAttrTree(af.listAttributesOfImplementation(selectedImp.getId()));
        iTree.setExpanded(expanded);
        iTree.setItemID(selectedImp.getId());
        iTree.setItem(selectedImp);
        iTree.setApplication(selectedApp);
        iTree.setUser(currentUser);
        aTree = new AppAttrTree(af.listAttributesOfApplication(selectedImp.getAppId()));
        iTree.setApThree(aTree);
        addMessage(null, FacesMessage.SEVERITY_INFO, "Attribute table reloaded", null);
    }

    public List<String> getImpFileArray() {
        List<String> impFileArray = new ArrayList<String>();
        if (getFileNamesOfSelectedImplementation() != null) {
            String[] fileNames = getFileNamesOfSelectedImplementation();
            String pathName = "/srv/shiwa/" + selectedApp.getId() + "/" + selectedImp.getId() + "/";
            Iterator<String> itr = iTree.getParameterIDList().iterator();
            while (itr.hasNext()) {
                String paraId = itr.next();
                for (int i = 0; i < fileNames.length; i++) {
                    if ((fileNames[i] != null && fileNames[i].equals(iTree.getOrderDefaultValue(paraId)))) {

                        impFileArray.add(pathName.concat(fileNames[i]));
                    }
                }
            }
        }
        return impFileArray;
    }

    public List<String> getAppFileArray() {
        List<String> appFileArray = new ArrayList<String>();
        if (getFileNamesOfSelectedAppication() != null) {
            String[] fileNames = getFileNamesOfSelectedAppication();
            String pathName = "/srv/shiwa/" + selectedApp.getId() + "/";
            Iterator<String> itr = iTree.getParameterIDList().iterator();
            while (itr.hasNext()) {
                String paraId = itr.next();
                for (int i = 0; i < fileNames.length; i++) {
                    if (fileNames[i] != null && fileNames[i].equals(iTree.getOrderDefaultValue(paraId))) {
                        appFileArray.add(pathName.concat(fileNames[i]));
                    }
                }
            }
        }

        return appFileArray;
    }

    public List<String> getFileList() {
        getAppFileArray();
        getImpFileArray();
        List<String> Flielist = new ArrayList<String>();

        Flielist.add("/srv/shiwa/" + selectedApp.getId() + "/" + selectedImp.getId() + "/" + iTree.getWfDefinition());
        Flielist.addAll(getAppFileArray());
        Flielist.addAll(getImpFileArray());
        return Flielist;
    }

    public static <T> T[] arrayMerge(T[]... arrays) {
        int count = 0;
        for (T[] array : arrays) {
            count += array.length;
        }
        T[] mergedArray = (T[]) Array.newInstance(arrays[0][0].getClass(), count);
        int start = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0, mergedArray, start, array.length);
            start += array.length;
        }
        return (T[]) mergedArray;
    }

    public File[] getArrayOfWorkflowFiles() {
        List<File> wfFileList = new ArrayList<File>();

        for (int i = 0; i < getFileList().size(); i++) {
            wfFileList.add(new File(getFileList().get(i)));
            System.out.println(getFileList().get(i));
        }

        File[] wfFiles = wfFileList.toArray(new File[0]);
        return wfFiles;
    }

    public String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void updateWorkflow() {
        errLogs = "";
        boolean updated = false;
        boolean deployed = false;
        try {
            //saving impattr table
            updateAttributesOfSelectedImplementation(true);

            // updating lc
            //it may happen that LC has been deployed before with another name. In this case we don't undeploy, just simply deploy the WF
            try {

                System.out.println("workflow is updated");

                // updating data in imp attr table
                iTree.removeDeployer();
                System.out.println("Deployer is removed");
                iTree.addDeployer();
                System.out.println("Deployer is created");
                updated = true;

                // providing info about the update
                errLogs += "Workflow implementation '" + iTree.getWfDefinition() + "' has been re-deployed successfully";
                addMessage(null, FacesMessage.SEVERITY_INFO, "Workflow implementation '" + iTree.getWfDefinition()+ "' has been re-deployed successfully", null);
            } catch (Exception ex) {
                errLogs += "Could not find LC with ID:" + iTree.getWfDefinition() + " \nAttempting deployment:\n";
                Logger.getLogger(ImpAttrTree.class.getName()).log(Level.SEVERE, null, ex);
                addMessage(null, FacesMessage.SEVERITY_INFO, "Could not find LC in GEMLCA with ID: " + iTree.getWfDefinition()+ ". Attempting deployment.", null);
            }

            // if update fails, we will try simple deployment
            if (!updated) {
                // updating data in imp attr table
                iTree.removeDeployer();
                System.out.println("Deployer is removed");
                iTree.addDeployer();
                System.out.println("Deployer is created");
                deployed = true;

                // providing info about the update
            }

        } catch (Exception ex) {
            Logger.getLogger(ImpAttrTree.class.getName()).log(Level.SEVERE, null, ex);
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Workflow has failed to re-deploy, please see the deployment logs by opening Actions/Show Deployemt Logs.", null);
        } finally {
            errLogs = getCurrentTime() + "\n" + errLogs;

            if (updated || deployed) {
                //saving data
                updateImplementation(true);
                updateAttributesOfSelectedImplementation(true);
            }
        }
    }

    public String updateAttributesOfSelectedImplementation() {
        return updateAttributesOfSelectedImplementation(false);
    }

    public String updateAttributesOfSelectedImplementation(boolean silentOnSuccess) {
        if (selectedImp == null) {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select an implementation", null);
            return null;
        }
        try {
            af.updateImpAttrList(selectedImp.getId(), iTree.getAttrList());
            af.updateImpTimestamp(selectedImp.getId());
            if (selectedApp != null) {
                af.updateAppTimestamp(selectedApp.getId());
            }
            if (!silentOnSuccess) {
                addMessage(null, FacesMessage.SEVERITY_INFO, "Attribute table saved", null);
            }
            return null;
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null);
        }
        return null; //no navigation result
    }

    public String getMyExpWorkflowID() {
        return myExpWorkflowID;
    }

    public void setMyExpWorkflowID(String myExpWorkflowID) {
        this.myExpWorkflowID = myExpWorkflowID;
    }

    public String getCurrentWorkflowName() {
        String workflowID = getMyExpWorkflowID();
        MyExperimentClient myClient = new MyExperimentClient();
        if (myClient.getWorkflow(workflowID) == null) {
            addMessage(null, FacesMessage.SEVERITY_INFO, "workflow with ID number " + workflowID + " does not exist or you are authorized to import it", null);
            return myExpWorkflowName;
        }

        myExpWorkflowName = myClient.getWorkflowTitle(workflowID);
        myExpWorkflowName = myExpWorkflowName.replaceAll("\\W", "");
        myExpWorkflowName = myExpWorkflowName.replaceAll("_", "");
        return myExpWorkflowName;
    }

    public String getMyExpWorkflowName() {
        return myExpWorkflowName;
    }

    public void setMyExpWorkflowName(String myExpWorkflowName) {
        this.myExpWorkflowName = myExpWorkflowName;
    }

    public void currentWorkflowName() {
        getCurrentWorkflowName();
    }

    public void createMyExpApplicationAndImplementation() {
        manager.createApplicationAndImplementation();
    }

    public List<String> getGroupNames() {

        List<String> groupNames = new ArrayList();
        for (int i = 0; i < getGroups().size(); i++) {
            groupNames.add(getGroups().get(i).getName());
        }
        return groupNames;
    }

    public Map<String, Integer> getGroupIDs() {

        Map<String, Integer> mMap = new HashMap<String, Integer>();
        for (int i = 0; i < getGroups().size(); i++) {
            mMap.put(getGroups().get(i).getName(), getGroups().get(i).getId());
        }
        return mMap;
    }

    public Map<String, Integer> getWorkflowApplicationNames() {

        Map<String, Integer> workflowApplicationNames = new HashMap<String, Integer>();
        for (int i = 0; i < getApplications().size(); i++) {
            workflowApplicationNames.put(getApplications().get(i).getName(), getApplications().get(i).getId());
        }
        return workflowApplicationNames;
    }

@ManagedProperty(value="#{param.impid}")
    private String implementationID = "undef";
    public String getImplementationID(){
        return implementationID;
    }

    Integer parseWorkflowID(String implementationID) {
        try {
            return Integer.parseInt(implementationID);
        }
        catch (NumberFormatException e){
            Logger.getAnonymousLogger().log(Level.INFO, "Error while parsing ID: {0}", e.getMessage());
        }
        return null;
    }

    public void setImplementationID(String implementationID) {
        Logger.getAnonymousLogger().log(Level.INFO, "navigating to imp: ".concat(implementationID));
        Integer impid = parseWorkflowID(implementationID);
        if (impid == null) {
            return;
        }
        try {
            af.viewImp(impid);
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthorizationException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.selectedImp != null && this.selectedImp.getId().equals(impid)) {
            return;
        }

        ImplementationTO imp = null;
        for (ImplementationTO i : getImplementations()) {
            if (i.getId().equals(impid)) {
                imp = i;
                break;
            }
        }

        if (imp != null) {
            this.selectedImp = imp;
            this.selectedApp = af.loadApplication(imp.getAppId());
            try {
                String target = "?impid=" + implementationID;
                redirect(target);

            } catch (IOException ex) {
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    @ManagedProperty(value="#{param.appid}")
    private String applicationID = "undef";
    public String getApplicationID(){
        return applicationID;
    }
    public void setApplicationID(String applicationID) {
        Logger.getAnonymousLogger().log(Level.INFO, "navigating to app: ".concat(applicationID));
        Integer appid = parseWorkflowID(applicationID);
        if (appid == null) {
            return;
        }

        try {
            af.viewApp(appid);
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthorizationException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.selectedApp != null && this.selectedApp.getId().equals(appid)) {
            return;
        }

        ApplicationTO app = null;
        for (ApplicationTO i : getApplications()) {
            if (i.getId().equals(appid)) {
                app = i;
            }
        }

        if (app != null) {
            this.selectedApp = app;
            try {
                String target = "?appid=" + applicationID;
                redirect(target);
            } catch (IOException ex) {
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    private boolean validateImp = false;
    public boolean getImplementationPublic() {
        return validateImp = selectedImp.getPublic();
    }

    public void  setImplementationPublic(boolean isImpPublic) {
        validateImp = isImpPublic;
    }

    /*
     * Workflow Engine Management Functions ------------------------------------------
     */

    /*
     * Allows user specific viewing rights over Worfklow Engine Management data
     */
    public boolean getWEManagementAccessLevel1(){
        return getCurrentUser().getActive();
    }

    /*
     * Allows user complete access and modification rights over Workflow Engine data
     */
    public boolean getWEManagementAccessLevel2(){
        return getCurrentUser().getAdmin();
    }

    //returns a list of all workflow engines
    public List<Platform> getMyWorkflowEngines() {
        return af.listWorkflowEngines();
    }

    public List<String> getWorkflowEnginesNoDupes(){
        return af.listWorkflowEnginesNoDupes();
    }

    public List<Platform> getWorkflowEnginesByString(){
        ArrayList<Platform> tempList = new ArrayList<Platform>();
        for(Platform temp : af.listWorkflowEngines()){
            if(temp.getName().equalsIgnoreCase(impWEIdString)){
                tempList.add(temp);
            }
        }
        return tempList;
    }

    public void handleImpWEId(){
        selectedWorkflowEngine = af.getWEById(impWEId);
    }

        //returns the current workflow engine
    public Platform getSelectedWorkflowEngine() {
        return selectedWorkflowEngine;
    }

    public void initWEImpUpdateVars(WEImplementation _weI ){
        selectedBEInstance =  _weI.getIdBackendInst();
        selectedWEImp = _weI;
    }

    public void initBEImpUpdateVars(BeInstance _beI ){
        selectedBEInstance =  _beI;
    }

    //sets the current workflow engine
    public void setSelectedWorkflowEngine(Platform _we) {
        this.selectedWorkflowEngine = _we;
    }

    //page to load if the user wants additional datas on the workflow engine
    public void onWERowSelectNavigate(SelectEvent event) {
        nav.performNavigation("/public/edit-WorkflowEngine?faces-redirect=true");
    }

    //defines if a register user or anonymous user can access workflow engine management
    public boolean getCanReadWorkflowEngine()
    {
        if(selectedWorkflowEngine == null){
            err = "Please select a workflow engine.";
        }
        else
        {
            if(getCurrentUser() == null || getCurrentUser().getId() == null)
                err = af.canPublicReadWorkflowEngine(selectedWorkflowEngine.getId());
            else
                err = af.canUserReadWorkflowEngine(getCurrentUser().getId(), selectedWorkflowEngine.getId());
        }
        return err == null;
    }

    /* Cyril?!
     * TODO: Check if this is ever used...if not, DELETE!
     */
    public void setImplementationsOfSelectedWE( WEImplementation _weImp ) {
        this.selectedWEImp = _weImp;
    }


    //to redirect a user who wants to edit workflow engine implementations
    public void onImplementationsOfSelectedWERowSelectNavigate( SelectEvent event ) {
        showBEIDetails = true;
        nav.performNavigation("/user/edit-WEImplementation?faces-redirect=true");
    }

    //defines if a register user can access workflow engine implementation management
    public boolean getCanCreateWorkflowEngine(){
        return (err = af.canUserCreateWorkflowEngines(getCurrentUser().getId())) == null;
    }

    public String createWorkflowEngine(){
        try{
            //if this works needs some validation/auth!!

            Platform temp = af.createWorkflowEngine(newWorkflowEngine.getName(), newWorkflowEngine.getVersion(), newWorkflowEngine.getDescription());
            Logger.getLogger(BackingBean.class.getName()).log(Level.INFO, "Created Workflow Engine '"+temp.getName()+"'");
            resetWEVars();
            selectedWorkflowEngine = temp;
            addMessage(null, FacesMessage.SEVERITY_INFO, "Created Workflow Engine '"+selectedWorkflowEngine.getName()+"'", null);
            return "/public/edit-WorkflowEngine.xhtml";

        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (ValidationFailedException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (WEBuildingException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        }

        return "";
    }

    public String deleteWE(){


        try{
            af.deleteWE(selectedWorkflowEngine);
            addMessage(null, FacesMessage.SEVERITY_INFO, "Workflow Engine Successfully deleted!", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Workflow Engine " + selectedWorkflowEngine.getName() + " " + selectedWorkflowEngine.getVersion() + " Successfully deleted!");
            resetWEVars();

            return "/user/home";

        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (EntityNotFoundException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotSafeToDeleteException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        }

        return "";

    }

    public Platform getNewWorkflowEngine(){
        return this.newWorkflowEngine;
    }

    public void resetWEVars(){
        displayNode = null;

        /* Workflow Engine - emk */
        selectedWorkflowEngine = null;
        selectedWEImp = null;
        newWEImplementation = new WEImplementation();
        newWorkflowEngine = new Platform();
        selectedJobManagers = new ArrayList<String>();

        /* WEFiles Management */
        selectedWEFile = null;
        newWEFile = new WEUploadedFile();
        currentWEFile = null;
        primeWEFile = null;
        selectedWEFiles = null;

        /* WEImplementation */
        backendAttribs = null;
        selectedBEInstance = null;
        showExistingBEI = true;
        selectedBEIJobTypeId = 0;
        newBEAbstractId = 0;
        selectedBEInstanceId = 0;
        newBEIoperatingSysId = 0;
        newWEIZipId = 0;
        showBEIDetails = false;
        changeEngineExec = false;

        //for workflow engine management
        wfEngineList = null;
    }


    /*
     * Workflow Engine Implementation Functions --------------------------------------
     */

    //returns the list of implementations relative to a workflow engine
    public List<WEImplementation> getMyImplementations(){
        return af.getWEImpByWE(selectedWorkflowEngine);
    }

    //returns the current workflow engine implementation
    public WEImplementation getSelectedWEImp(){
        return this.selectedWEImp;
    }

    public void setWEIUpdate(){
        try{
            af.updateWEImp(selectedWEImp);
            //resetWEVars();
            addMessage(null, FacesMessage.SEVERITY_INFO, "Updated Workflow Engine Implementation '"+selectedWEImp.getNameWEImp()+"'", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.INFO, "Updated Workflow Engine Implementation '"+selectedWEImp.getNameWEImp()+"'");
            }catch(ValidationFailedException e){
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            }catch(AuthorizationException e){
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            }catch(WEBuildingException e){
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
                Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            }
    }

    public void setSelectedWEImp( WEImplementation _weImp ){
        this.selectedWEImp = _weImp;
    }

    //defines if a register or anonymous user can access workflow engine implementation management
    public boolean getCanReadWEImplementation(){
        if(selectedWEImp == null){
            err = "Please select a workflow engine implementation.";
        }
        else
        {
            if(getCurrentUser() == null || getCurrentUser().getId() == null)
                err = af.canPublicReadWEImplementation(selectedWEImp.getIdWEImp());
            else
                err = af.canUserReadWEImplementation(getCurrentUser().getId(), selectedWEImp.getIdWEImp());
        }
        return err == null;
    }

    //defines if a register user can access workflow engine implementation management
    public boolean getCanCreateWEImplementations(){
        return (err = af.canUserCreateWEImplementations(getCurrentUser().getId())) == null;
    }

    public boolean getIsWEImpOwner(WEImplementation weimp){
        return af.isWEImpOwner(getCurrentUser().getId(), weimp);
    }

    public boolean getCanModifyWEImplementations(WEImplementation weimp){
        return (err = af.canUserModifyWEImplementations(getCurrentUser().getId(), weimp)) == null;
    }

    public WEImplementation getNewWEImplementation(){
        return this.newWEImplementation;
    }

    public String createWEImplementation(){

        boolean error = false;
        String r = "";
        /*
         * check for null status of objects WE, BE, SH and ZIP
         */

        try{
            if(!newWEImplementation.getPreDeployed()){
                selectedWEImp = af.createWEImp(newWEImplementation.getNameWEImp(), newWEImplementation.getDescriptionWEImp(), newWEImplementation.getPreDeployed(), newWEImplementation.getPrefixData(), selectedWorkflowEngine, newWEImplementation.getIdBackendInst(), newWEImplementation.getZipWEFileId(), null, newWEImplementation.getShellWEFileId());
            } else if (newWEImplementation.getPreDeployed()){
                selectedWEImp = af.createWEImp(newWEImplementation.getNameWEImp(), newWEImplementation.getDescriptionWEImp(), newWEImplementation.getPreDeployed(), newWEImplementation.getPrefixData(), selectedWorkflowEngine, newWEImplementation.getIdBackendInst(), null, newWEImplementation.getShellPath(), null);
            }

            addMessage(null, FacesMessage.SEVERITY_INFO, "Created Workflow Engine Implementation '"+selectedWEImp.getNameWEImp()+"'", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.INFO, "Created Workflow Engine Implementation '"+selectedWEImp.getNameWEImp()+"'");
            r = "/public/edit-WorkflowEngine.xhtml";
        } catch (EntityAlreadyExistsException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            error = true;
        }catch(ValidationFailedException e){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            error = true;
        }catch(AuthorizationException e){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            error = true;
        }catch(WEBuildingException e){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            error = true;
        }
        /*
         * to ensure that the next use of this pointer is ok as long
         * as there have been no validation errors during adding the WEImp
         */
        if(error != true){
            Platform temp2 = selectedWorkflowEngine;
            resetWEVars();
            selectedWorkflowEngine = temp2;
        }
        //TODO: write a redirect to a more appropriate screen
        return r;
    }

    public void dupeWEImp(){
        try {
            selectedWEImp = af.dupeWEImp(selectedWEImp, newWEImplementation.getNameWEImp());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Successfully created Workflow Engine Implementation " + selectedWEImp.getNameWEImp(), null);
        } catch (EntityAlreadyExistsException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
            addMessage(null, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (ValidationFailedException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
            addMessage(null, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (AuthorizationException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
            addMessage(null, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (WEBuildingException ex) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, ex);
            addMessage(null, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public boolean getCanDeleteWEImplementation(){
        return (err = af.canUserCreateWEImplementations(getCurrentUser().getId())) == null;
    }

    public String deleteWEImplementation(){
        /*
         * TODO: Add message to ensure deletion of correct WEImp
         */
        if(selectedWEImp == null){
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a workflow engine implementation", null);
            return null;
        }
        if(!getCanModifyWEImplementations(selectedWEImp)){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot delete, incorrect privilages or bad request. User is not the owner!", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Cannot delete, incorrect privilages or bad request. User is not the owner!");
            return null;
        }


        try{
            af.deleteWEImplementation(selectedWEImp.getIdWEImp());
            addMessage(null, FacesMessage.SEVERITY_INFO, "Deleted workflow engine implementation '"+selectedWEImp.getNameWEImp()+"'", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.INFO, "Deleted workflow engine implementation '"+selectedWEImp.getNameWEImp()+"'");
            Platform temp = selectedWorkflowEngine;
            resetWEVars();
            selectedWorkflowEngine = temp;
            return "/public/edit-WorkflowEngine.xhtml";
        }catch(EntityNotFoundException e){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        }catch(NotSafeToDeleteException e){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        } catch (AuthorizationException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /*
     * Backend Functions -------------------------------------------------------------
     */
    public String getBeiName() {
        return beiName;
    }

    public void setBeiName(String beiName) {
        this.beiName = beiName;
    }

    public String getBackendCreationType() {
        return backendCreationType;
    }

    public void setBackendCreationType(String backendCreationType) {
        this.backendCreationType = backendCreationType;
    }

    public String getBackendSelString(){
        return "";
    }

    public void setBackendSelString(String selString){
        backendSelString = selString;
        addBackendAttribute(selString);
    }

    public void addBackendAttribute(String s){
        if(backendAttribs == null){
            backendAttribs = new ArrayList<NewAttributeBean>();
        }

        if(s != null && !s.isEmpty()){
            String kv[] = s.split("\\:");

            backendAttribs.add(new NewAttributeBean(kv[0], kv[1], kv[2]));
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Type = " + kv[0] + " Key = " + kv[1] + " Value = " + kv[2]);
        }
    }

    public void createBEInstance(){
        HashMap<String, String> attribMap = new HashMap<String, String>();

        if(backendCreationType != null && !backendCreationType.isEmpty() && backendAttribs != null && !backendAttribs.isEmpty()){
            //get only attributes for creation type
            for(NewAttributeBean a : backendAttribs){
                if(a.getType().equalsIgnoreCase(backendCreationType)){
                    attribMap.put(a.getName(), a.getValue());
                }
            }
            try {
                BeInstance b = af.createBeInstance(beiName, attribMap, getCurrentUser().getId(), backendCreationType);
                addMessage(null, FacesMessage.SEVERITY_INFO, "Backend Configuration " + b.getName() + " has been created successfully!" , null);
            } catch (AuthorizationException ex) {
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+ex.getMessage(), null);
            } catch (ValidationFailedException ex) {
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+ex.getMessage(), null);
            } catch (EntityAlreadyExistsException ex) {
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+ex.getMessage(), null);
            }
        }
    }

    public void onBackendRowSelectNavigate(SelectEvent event) {
        nav.performNavigation("/user/edit-backend?faces-redirect=true");
    }

    public List<Backend> getBackends(){
        return af.listBackendAll();
    }

    public String createBackend(){
        return null;
    }

    public String updateBackend(){
        return null;
    }

    public String deleteBackend(){
        return null;
    }

    public int getNewBEAbstractId() {
        return newBEAbstractId;
    }

    public void setNewBEAbstractId(int newBEAbstractId) {
        this.newBEAbstractId = newBEAbstractId;
    }

    /*
     * Backend Instance Functions ----------------------------------------------------
     */

    public List<BeInstance> getBeInstanceAll(){
        return af.getBeInstanceAll();
    }

    public boolean getCanCreateBackend(){
        return (err = af.canUserCreateBackends(getCurrentUser().getId())) == null;
    }

    public String getBackendName(WEImplementation _WEImp){
        return _WEImp.getIdBackendInst().getBackend();
    }

    public boolean getCanViewBEI(){
        if(getCurrentUser().getAdmin() || getCurrentUser().getWEDev())
            return true;
        else
            return false;

    }

    public boolean getCanDeleteBEI(){
        if(getCurrentUser().getAdmin() || getCurrentUser().getWEDev()){
            return true;
        }
        return false;
    }

    public BeInstance getSelectedBEInstance() {
        return selectedBEInstance;
    }

    public void setSelectedBEInstance(BeInstance selectedBEInstance) {
        this.selectedBEInstance = selectedBEInstance;
    }

    public Boolean getShowBEIDetails() {
        return showBEIDetails;
    }

    public void setShowBEIDetails(Boolean showBEIDetails) {
        this.showBEIDetails = showBEIDetails;
    }

    public boolean isShowExistingBEI() {
        return showExistingBEI;
    }

    public void setShowExistingBEI(boolean showExistingBEI) {
        this.showExistingBEI = showExistingBEI;
    }

    public int getSelectedBEInstanceId() {
        return selectedBEInstanceId;
    }

    public void setSelectedBEInstanceId(int selectedBEInstanceId) {
        this.selectedBEInstanceId = selectedBEInstanceId;
    }

    public void handleExistingBEI(boolean edit){

    }

    public int getNewBEIoperatingSysId() {
        return newBEIoperatingSysId;
    }

    public void setNewBEIoperatingSysId(int newBEIoperatingSysId) {
        this.newBEIoperatingSysId = newBEIoperatingSysId;
    }

    public boolean canModifyBEInst(BeInstance beinst){
        return (err = af.canUserModifyBEInst(getCurrentUser().getId(), beinst)) == null;

    }

    public void logAccess(){


        /*
         * A way of counting popularity on a semi-regular basis
         */

        impViewCount = af.countAllViews(true);
        appViewCount = af.countAllViews(false);

        String user;

        if( getCurrentUser() == null){
            user = "guest";
        }else{
            user = getCurrentUser().getLoginName();
        }

        if(prevAccessUser == null || !prevAccessUser.equals(user)){
            Date date = new Date();
            String longDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
            String shortDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            String name = "/srv/shiwa/access.log" + shortDate;
            File access_log = new File(name);

            FileWriter fstream = null;
            BufferedWriter out = null;

            Boolean beg = access_log.exists();

            try{
                fstream = new FileWriter(access_log, beg);
                out = new BufferedWriter(fstream);
                out.write( longDate + " " + user + "\n");
                out.close();
            }catch(IOException e){
                Logger.getAnonymousLogger().log(Level.SEVERE, null, e);
            }finally{
                prevAccessUser = user;
                Logger.getAnonymousLogger().log(Level.INFO, "Logging session for " + user);

            }
        }
    }

    /*
     * WEFiles functions ------------------------------------------------------------------
     */

    public boolean getCanModifyWEFiles(){
        return (err = af.canUserModifyWEFiles(getCurrentUser().getId())) == null;
    }

    public boolean getCanDownloadWEFiles(){
        return (err = af.canUserDownloadWEFiles(getCurrentUser().getId())) == null;
    }

    public Collection<WEUploadedFile> getFilesForWE(){
        //return af.getFilesByWE(selectedWorkflowEngine);
        return selectedWorkflowEngine.getUploadedFileCollection();
    }

    public WEUploadedFile getSelectedWEFile() {
        return selectedWEFile;
    }

    public void setSelectedWEFile(WEUploadedFile selectedWEFile) {
        this.selectedWEFile = selectedWEFile;
    }

    public WEUploadedFile getNewWEFile() {
        return newWEFile;
    }

    public void setNewWEFile(WEUploadedFile newWEFile) {
        this.newWEFile = newWEFile;
    }

    public WEUploadedFile[] getSelectedWEFiles() {
        return selectedWEFiles;
    }

    public void setSelectedWEFiles(WEUploadedFile[] selectedWEFiles) {
        this.selectedWEFiles = selectedWEFiles;
    }

    public void handleWEFileUpload(FileUploadEvent event) {
         primeWEFile = event.getFile();
    }

    public String handleWEFileAttribs(){

        boolean error = false;

        err = "";
        if(!getCanModifyWEFiles()){
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, err);
            addMessage(null, FacesMessage.SEVERITY_ERROR, err, null);
            return "";
        }

        if(selectedWorkflowEngine == null){
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please select a Workflow Engine", null);
            return "/public/edit-WorkflowEngine?faces-redirect=true";
        }
        if(primeWEFile == null)
        {
            addMessage(null, FacesMessage.SEVERITY_WARN, "Please upload a workflow engine file", null);
            return "";
        }

        //ensure file is of right type
        if(!newWEFile.getIsData()){
            //if file is executable
            if(primeWEFile.getFileName().matches("([^\\\\s]+(\\\\.(?i)(sh|bat))$)")){
                addMessage(null, FacesMessage.SEVERITY_INFO, "An executable needs to be in the form of a batch or shell file (.bat or .sh extension)", null);
                Logger.getLogger(BackingBean.class.getName()).log(Level.INFO, "An executable needs to be in the form of a batch or shell file (.bat or .sh extension)");
                return "";
            }
        }else{
            //if file is data
            if(primeWEFile.getFileName().matches("([^\\\\s]+(\\\\.(?i)(zip|tar))$)")){
               addMessage(null, FacesMessage.SEVERITY_INFO, "An engine data archive needs to be in the form of a tarball or a zip file (.tar or .zip extension)", null);
                               Logger.getLogger(BackingBean.class.getName()).log(Level.INFO, "An engine data archive needs to be in the form of a tarball or a zip file (.tar or .zip extension)");
               return "";
            }
        }

        try{
            //why the hell is currentWEFile being set?!?!
            currentWEFile = af.uploadWEFile(newWEFile.getName(), newWEFile.getDescription(), selectedWorkflowEngine, primeWEFile.getFileName(), primeWEFile.getInputstream(), newWEFile.getIsData());
            newWEFile = new WEUploadedFile(); //sets fields to empty
            addMessage(null, FacesMessage.SEVERITY_INFO, "Uploaded file '"+currentWEFile.getFilePath()+"'", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.INFO,"Uploaded file '"+currentWEFile.getFilePath()+"'");

        } catch (IOException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: could not upload file", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            error = true;
        } catch (FileOperationFailedException e) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            error = true;
        } catch (EntityAlreadyExistsException e) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            error = true;
        } catch (ValidationFailedException e) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            error = true;
        }catch(EntityNotFoundException e){
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            error = true;
        } catch (AuthorizationException e) {
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
            error = true;
        }

        /*
         * on success remove link to this object for use in other functions later
         */
        currentWEFile = null;

        if(error)
            return "";

        return "/public/edit-WorkflowEngine";
    }

    public String handleWEFileDeletion(){

        err = "";
        if(!getCanModifyWEFiles()){
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, err);
            addMessage(null, FacesMessage.SEVERITY_ERROR, err, null);
            return "";
        }

        if(selectedWEFile == null) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, "To delete a Uploaded File please select at least one in the leftmost column", null);
            return "";
        }

        /*
         * TODO: Double up this validation protection?
         */
        if(!this.getAffectedEnginesByFiles().equals(" 0 ")){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Workflow Engine file is used by at least one Workflow Engine Implementations", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, "Workflow Engine file is used by at least one Workflow Engine Implementations");
            return "";
        }

        try{
            Platform temp = af.deleteWEFiles(selectedWEFile);
            addMessage(null, FacesMessage.SEVERITY_INFO, "File successfully deleted", null);
            Logger.getLogger(BackingBean.class.getName()).log(Level.INFO, selectedWEFile.getName() + " successfully deleted");
            resetWEVars();
            selectedWorkflowEngine = temp;
           return "/public/edit-WorkflowEngine";
       } catch (AuthorizationException e) {
           Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
           addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
       } catch (EntityNotFoundException e) {
           Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
           addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
       } catch (FileOperationFailedException e){
           Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
           addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
       } catch (ValidationFailedException e){
           Logger.getLogger(BackingBean.class.getName()).log(Level.SEVERE, null, e);
           addMessage(null, FacesMessage.SEVERITY_ERROR, "Error: "+e.getMessage(), null);
       }

       return "/public/home?faces-redirect=true";
    }

    /*
     * Returns a string of all of the WEImplementation names that are affected by the possible deletion
     * of a WEFile. If no WEImps are affected. Then "0" is returned.
     */
    public String getAffectedEnginesByFiles(){
        if(selectedWEFile != null){

            String r = " ";
            //Extract the names of all the WEImplementations that are affected by the possible deletion of a give WEFile
            /*
             * TODO: convert this to a entity manager request.
             */
            List<WEImplementation> list = af.getAfftectedWEImps(selectedWEFile);
            Iterator<WEImplementation> temp = list.iterator();
            while(temp.hasNext()){
                r = r + temp.next().getNameWEImp();
            }
            if(r.equals(" "))
                return " 0 ";

            return r;
        }
        return " 0 ";
    }

    public Collection<WEUploadedFile> getZipWEFiles(){
        return af.listFilesByWE(selectedWorkflowEngine, true);
    }

    public Collection<WEUploadedFile> getZipWEFilesEdit(){
        Collection<WEUploadedFile> tempCol = af.listFilesByWE(selectedWorkflowEngine, true);
        tempCol.remove(selectedWEImp.getZipWEFileId());
        return tempCol;
    }

    public Collection<WEUploadedFile> getShellWEFilesEdit(){
        Collection<WEUploadedFile> tempCol = af.listFilesByWE(selectedWorkflowEngine, false);
        tempCol.remove(selectedWEImp.getShellWEFileId());
        return tempCol;
    }

    public int getNewWEIZipId() {
        return newWEIZipId;
    }

    public void setNewWEIZipId(int newWEIZipId) {
        this.newWEIZipId = newWEIZipId;
    }

    public void handleEngineZipSelect(boolean edit){
        if (!edit)
            newWEImplementation.setZipWEFileId(af.getWEFileById(newWEIZipId));
        else
            selectedWEImp.setZipWEFileId(af.getWEFileById(newWEIZipId));
    }

    public int getNewShellFileId() {
        return newShellFileId;
    }

    public void setNewShellFileId(int newShellFileId) {
        this.newShellFileId = newShellFileId;
    }

    public void handleShellFileSelect(boolean edit){
        if(!edit)
            newWEImplementation.setShellWEFileId(af.getWEFileById(newShellFileId));
        else
            selectedWEImp.setShellWEFileId(af.getWEFileById(newShellFileId));
    }

    public Collection<WEUploadedFile> getShellWEFiles(){
        return af.listFilesByWE(selectedWorkflowEngine, false);
    }
    public String getRepoVersion() {
        return REPO_VERSION;
    }

}
