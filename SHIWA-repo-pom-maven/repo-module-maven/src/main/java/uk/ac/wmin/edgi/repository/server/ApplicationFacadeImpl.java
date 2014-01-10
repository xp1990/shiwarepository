/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.shiwa.repository.submission.SubmissionHelpers;
import org.shiwa.repository.submission.SubmissionRequests;
import org.shiwa.repository.submission.objects.EngineData;
import org.shiwa.repository.submission.objects.ImplShort;
import org.shiwa.repository.submission.objects.JSDL.ImplJSDL;
import org.shiwa.repository.submission.objects.Parameter;
import org.shiwa.repository.submission.objects.workflowengines.WorkflowEngineInstance;
import org.shiwa.repository.toolkit.wfengine.*;
import uk.ac.wmin.edgi.repository.common.*;
import uk.ac.wmin.edgi.repository.entities.*;
import uk.ac.wmin.edgi.repository.transferobjects.*;

/**
 *
 * @author zsolt
 */
@Stateless
public class ApplicationFacadeImpl implements ApplicationFacadeLocal, Serializable {

    @PersistenceContext(unitName = "repo_proto_1PU") EntityManager em;
    @Resource javax.ejb.SessionContext context;

    private String repoPath;
    private Logger logger;
    private String globusUserpass;
    private String globusUsercert;
    private String globusUserkey;

    @PostConstruct
    public void initialize () {
        try{
            String p = (String)context.lookup("java:comp/env/edgi-logger");
            if(p.length() == 0){
                this.logger = Logger.getLogger("edgi-logger");
                System.out.println("WARNING: EDGI AR logger (java:comp/env/edgi-logger) not set, using default (edgi-logger)");
                this.logger.log(Level.WARNING, "EDGI AR logger (java:comp/env/edgi-logger) not valid (zero length), using default (edgi-logger)");
            }else{
                this.logger = Logger.getLogger(p);
            }
        }catch(IllegalArgumentException e){
            this.logger = Logger.getLogger("edgi-logger");
            System.out.println("WARNING: EDGI AR logger (java:comp/env/edgi-logger) not set, using default (edgi-logger)");
            this.logger.log(Level.WARNING, "EDGI AR logger (java:comp/env/edgi-logger) not set, using default (edgi-logger)");
        }
        try{
            String p = (String)context.lookup("java:comp/env/edgi-repo-path");
            if(p.length() == 0){
                this.repoPath="/srv/repo/";
                this.logger.log(Level.WARNING, "Repository path (java:comp/env/edgi-repo-path) not set, using default (/srv/repo/)");
            }else{
                this.repoPath = (p.endsWith("/") ? p : p + "/");
                this.logger.log(Level.INFO, "Repository path (java:comp/env/edgi-repo-path) set to ({0})", repoPath);
            }
        }catch(IllegalArgumentException e){
            this.repoPath="/srv/repo/";
            this.logger.log(Level.WARNING, "Repository path (java:comp/env/edgi-repo-path) not set, using default (/srv/repo/)");
        }
    }

    @Override
    public Logger getEdgiLogger(){
        return logger;
    }

    @Override
    public String getGlobusUserpass(){
        return globusUserpass;
    }

    @Override
    public String getGlobusUsercert(){
        return globusUsercert;
    }

    @Override
    public String getGlobusUserkey(){
        return globusUserkey;
    }

    private String asHex(byte[] buf) {
        final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    @Override
    public boolean isAuthorizedUser(){
        String callerName = context.getCallerPrincipal().getName();
        try{
            User caller = em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", callerName).getSingleResult();
            return (caller != null && caller.isActive());
        }catch(NoResultException e){
            return false;
        }
    }

    private User getListCaller() {
        String callerName = context.getCallerPrincipal().getName();
        try{
            User caller = em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", callerName).getSingleResult();
            return caller;
        }catch(NoResultException e){
            return null;
        }
    }

    @Override
    public List<String> filterAppGroupNames(String query) {
        User caller = getCallerUser();
        if (caller == null || !caller.isActive()) {
            return new ArrayList<String>(0);
        } else if (caller.isAdmin()) { //admins may assign any group
            return em.createNamedQuery("UserGroup.filterNames", String.class).setParameter("name", "%"+query+"%").getResultList();
        } else {
            //users may only assign one of their led groups
            //return em.createNamedQuery("UserGroup.filterNamesByLeader", String.class).setParameter("name", "%"+query+"%").setParameter("leaderName", caller.getLoginName()).getResultList();
            //users may only assign a group they are a member of
            return em.createNamedQuery("UserGroup.filterNamesByMember", String.class).setParameter("name", "%"+query+"%").setParameter("userId", caller.getId()).getResultList();
        }
    }

    @Override
    public List<String> filterImpPlatformNames(String query) {
        User caller = getCallerUser();
        if (caller == null || !caller.isActive()) {
            return new ArrayList<String>(0);
        }
        return em.createNamedQuery("Platform.filterNames", String.class).setParameter("name", "%"+query+"%").getResultList();
    }

    @Override
    public List<String> filterAddUserToGroupUserLoginNames(Integer groupId, String query) {
        User caller = getCallerUser();
        if (caller == null || !caller.isActive()) {
            return new ArrayList<String>(0);
        }
        return em.createNamedQuery("User.filterNamesNotInGroup", String.class).setParameter("loginName", "%"+query+"%").setParameter("groupId", groupId).getResultList();
    }

    @Override
    public List<String> filterGroupLeaderLoginNames(String query) {
        User caller = getCallerUser();
        if (caller == null || !caller.isActive()) {
            return new ArrayList<String>(0);
        }
        return em.createNamedQuery("User.filterNames", String.class).setParameter("loginName", "%"+query+"%").getResultList();
    }

    @Override
    public List<String> filterAppOwnerLoginNames(String query){
        User caller = getCallerUser();
        if (caller == null || !caller.isActive()) {
            return new ArrayList<String>(0);
        }
        return em.createNamedQuery("User.filterNames", String.class).setParameter("loginName", "%"+query+"%").getResultList();
    }

    @Override
    public List<String> filterAppAttrNames(String query) {
        List<String> list = em.createNamedQuery("AppAttribute.filterNames", String.class).setParameter("name", "%"+query+"%").getResultList();
        return list;
    }

    @Override
    public List<String> filterImpAttrNames(String query) {
        List<String> list = em.createNamedQuery("ImpAttribute.filterNames", String.class).setParameter("name", "%"+query+"%").getResultList();
        return list;
    }

    // use case diagram: Administrator: create user
    @Override
    public UserTO createUser(String loginName, String password, String fullName, String organization, String emailAddr, Boolean isActive, Boolean isAdmin, Boolean isValidator) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        String err = canUserCreateUsers(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //already exists?
        if(em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", loginName).getResultList().size() > 0){
            throw new EntityAlreadyExistsException("user '"+loginName+"' already exists");
        }
        String passHash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(password.getBytes());
            byte d[] = md.digest();
            passHash = asHex(d);
        } catch (NoSuchAlgorithmException ex) {
            //Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            //context.setRollbackOnly();
            throw new RuntimeException("fatal: the MD5 MessageDigest is not available"); //this will be logged automatically and the transaction will roll back
        }
        User u = new User(loginName, passHash, fullName, organization, emailAddr, isActive, isAdmin, isValidator);
        em.persist(u);
        return new UserTO(u);
    }

    // use case diagram: Administrator: update user profile
    // use case diagram: User: update profile
    @Override
    public UserTO updateUser(UserTO user) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        User u = em.find(User.class, user.getId());
        String err = canUserUpdateUser(caller, u);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate input
        if (user.getFullName() != null) {
            u.setFullName(user.getFullName());
        }
        if (user.getOrganization() != null) {
            u.setOrganization(user.getOrganization());
        }
        if (user.getEmail() != null) {
            u.setEmail(user.getEmail());
        }
        if (user.getActive() != null) {
            u.setActive(user.getActive());
        }
        if (user.getWEDev() != null) {
            u.setWEDev(user.getWEDev());
        }
        if (user.getAdmin() != null) {
            u.setAdmin(user.getAdmin());
        }
        u = em.merge(u);
        return new UserTO(u);
    }

    // use case diagram: Administrator: change user password
    // use case diagram: User: change password
    @Override
    public UserTO changeUserPassword(Integer userId, String password) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        User u = em.find(User.class, userId);
        String err = canUserChangeUserPassword(caller, u);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate password (eg minimum length)
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(password.getBytes());
            byte d[] = md.digest();
            u.setPassHash(asHex(d));
        } catch (NoSuchAlgorithmException ex) {
            //Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            //context.setRollbackOnly();
            throw new RuntimeException("fatal: the MD5 MessageDigest is not available");
        }
        em.merge(u);
        return new UserTO(u);
    }

/*
    // use case diagram: Administrator: deactivate user account
    // use case diagram: User: deactivate account
    @Override
    public UserTO deactivateUser(Integer userId) throws EntityNotFoundException, AuthorizationException {
        //only admins and the user itself may deactivate a user
        User caller = getCaller();
        if(!(caller.isAdmin() || (caller.getId().equals(userId)))){
            //context.setRollbackOnly();
            throw new AuthorizationException("only administrators may deactivate other users");
        }
        User u = em.find(User.class, userId);
        if (u == null) {
            //context.setRollbackOnly();
            throw new EntityNotFoundException("user '"+userId+"' does not exist");
        }
        u.setUser(false);
        u.setValidator(false);
        u.setAdmin(false);
        em.merge(u);
        return new UserTO(u);
    }
*/

    // use case diagram: Administrator: delete user
    @Override
    public void deleteUser(Integer userId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException {
        User caller = getCallerUser();
        User u = em.find(User.class, userId);
        String err = canUserDeleteUser(caller, u);
        if(err != null){
            throw new AuthorizationException(err);
        }
        /*
        if (!u.isSafeToDelete()) {
            //context.setRollbackOnly();
            throw new NotSafeToDeleteException("user '" + u.getLoginName() + "' can not be deleted, please remove all of its associated entities first");
        }
        */
        em.remove(u);
    }

    // use case diagram: All: create group
    @Override
    public UserTO findUserByLoginName(String loginName) {
        try{
            return em.createNamedQuery("User.loadByLoginName", UserTO.class).setParameter("loginName", loginName).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    @Override
    public UserTO findValidatorOfImp(int impId) {
        try{
            return em.createNamedQuery("User.findValidatorOfImplementation", UserTO.class).setParameter("impId", impId).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    @Override
    public GroupTO createGroup(String groupName) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        String err = canUserCreateGroups(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //validate group name
        if(!groupName.matches("[A-Za-z0-9]{3,70}")){
            //context.setRollbackOnly();
            throw new ValidationFailedException("group names can only contain alphanumeric characters and must be between 3 and 50 charaters long");
        }
        //check it does not exist yet
        if(em.createNamedQuery("UserGroup.findByName", UserGroup.class).setParameter("name", groupName).getResultList().size() > 0){
            throw new EntityAlreadyExistsException("group '"+groupName+"' already exists");
        }
        UserGroup g = new UserGroup(groupName, caller);
        em.persist(g);
        return new GroupTO(g);
    }

    // use case diagram: Admin: change group leader
    // use case diagram: User(group leader): change group leader
    @Override
    public GroupTO changeGroupLeader(Integer groupId, String leaderLoginName) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        UserGroup g = em.find(UserGroup.class, groupId);
        String err = canUserChangeGroupLeader(caller, g);
        if(err != null){
            throw new AuthorizationException(err);
        }
        try{
            User u = em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", leaderLoginName).getSingleResult();
            User ou = g.getLeader();
            g.setLeader(u);
            g = em.merge(g);
            em.merge(g);
            em.refresh(ou); //refresh original leader
            return new GroupTO(g);
        }catch (NoResultException e){
            throw new EntityNotFoundException("invalid leader name: user with login name '"+leaderLoginName+"' does not exist");
        }
    }

    // use case diagram: Admin: add user to group
    // use case diagram: User(group leader): add user to group
    @Override
    public void addUserToGroup(Integer groupId, String userLoginName) throws AuthorizationException, EntityNotFoundException {
        User caller = getCallerUser();
        UserGroup g = em.find(UserGroup.class, groupId);
        String err = canUserAddUsersToGroup(caller, g);
        if(err != null){
            throw new AuthorizationException(err);
        }
        try{
            User u = em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", userLoginName).getSingleResult();
            u.addGroup(g);
            g.addUser(u);
            em.merge(u);
            em.merge(g);
        }catch(NoResultException e){
            throw new EntityNotFoundException("invalid user name: user with login name '"+userLoginName+"' does not exist");
        }
    }

    // use case diagram: Admin: remove user from group
    // use case diagram: User(group leader): remove user from group
    @Override
    public void removeUserFromGroup(Integer groupId, Integer userId) throws AuthorizationException, EntityNotFoundException {
        User caller = getCallerUser();
        UserGroup g = em.find(UserGroup.class, groupId);
        User u = em.find(User.class, userId);
        String err = canUserRemoveUserFromGroup(caller, g, u);
        if(err != null){
            throw new AuthorizationException(err);
        }
        u.removeGroup(g);
        g.removeUser(u);
        em.merge(u);
        em.merge(g);
    }

    // use case diagram: Admin: delete group
    // use case diagram: User(group leader): delete group
    @Override
    public void deleteGroup(Integer groupId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException {
        User caller = getCallerUser();
        UserGroup g = em.find(UserGroup.class, groupId);
        String err = canUserDeleteGroup(caller, g);
        if (err != null) {
            throw new AuthorizationException(err);
        }

        List<User> listUser = g.getUsers();

        if (!listUser.isEmpty()) {
            throw new NotSafeToDeleteException("Please remove users first");
        }

        for (int i = 0; i < listUser.size(); i++) {

            User user = listUser.get(i);
            removeUserFromGroup(g.getId(), user.getId());
        }

        em.remove(g);
    }

    // use case diagram: Administrator: create platform
    @Override
    public PlatformTO createPlatform(String platName, String description) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        String err = canUserCreatePlatforms(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //does platform exist?
        if(em.createNamedQuery("Platform.findByName", Platform.class).setParameter("name", platName).getResultList().size() > 0){
            throw new EntityAlreadyExistsException("platform '"+platName+"' already exists");
        }
        Platform p = new Platform(platName, description);
        em.persist(p);
        return new PlatformTO(p);
    }

    @Override
    public PlatformTO createPlatform(String platName, String platVersion, String description) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        String err = canUserCreatePlatforms(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //does platform exist?
        if(em.createNamedQuery("Platform.findByNameAndVersion", Platform.class).setParameter("name", platName).setParameter("version", platVersion).getResultList().size() > 0){
            throw new EntityAlreadyExistsException("platform '"+platName+"' already exists");
        }
        Platform p = new Platform(platName, platVersion, description);
        em.persist(p);
        return new PlatformTO(p);
    }

    // use case diagram: Administrator: update platform
    @Override
    public PlatformTO updatePlatform(Integer platId, String description) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Platform p = em.find(Platform.class, platId);
        String err = canUserUpdatePlatform(caller, p);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //validate description
        if (description.length() > 5000) {
            //context.setRollbackOnly();
            throw new ValidationFailedException("description can not be more than 5000 charaters long");
        }
        p.setDescription(description);
        p = em.merge(p);
        return new PlatformTO(p);
    }

    // use case diagram: Administrator: delete platform
    @Override
    public void deletePlatform(Integer platId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException {
        User caller = getCallerUser();
        Platform p = em.find(Platform.class, platId);
        String err = canUserDeletePlatform(caller, p);
        if(err != null){
            throw new AuthorizationException(err);
        }
        em.remove(p);
    }


    @Override
    public ImpEmbedTO createImpEmbed(Integer impId, String extUserId, String extServiceId) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException {

        logger.log(Level.INFO, "Attempting to embed imp: {0} for external user: {1}...", new Object[]{impId, extUserId});
        Implementation i = em.find(Implementation.class, impId);
        if(i == null){
            //context.setRollbackOnly();
            logger.log(Level.SEVERE, "Implementation: {0} not found in DB; external user: {1}", new Object[]{impId, extUserId});
            throw new EntityNotFoundException("implementation '"+impId+"' does not exist");
        }

        ImpEmbed e = new ImpEmbed(i, extUserId, extServiceId);
        em.persist(e);
        logger.log(Level.INFO, "Embeded imp: {0} for external user: {1} for use in the SSP.", new Object[]{impId, extUserId});
        return new ImpEmbedTO(e);
    }

    @Override
    public Boolean isImpSelectedForEmbedding(Integer impId, String extUserId, String extServiceId){
        List<ImpEmbedTO> result = em.createNamedQuery("ImpEmbed.loadImpEmbed", ImpEmbedTO.class).setParameter("extServiceId", extServiceId).setParameter("extUserId", extUserId).setParameter("impId", impId).getResultList();
        //System.out.println("af: "+impId+": "+ (result != null && !result.isEmpty()));
        return result != null && !result.isEmpty();
    }

    @Override
    public void removeImpEmbed(Integer impId, String extUserId, String extServiceId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {

        ImpEmbedTO impEmbedTo = null;

        try{
            logger.log(Level.INFO, "Attempting to remove from embedding imp: {0} for external user: {1}...", new Object[]{impId, extUserId});
            impEmbedTo = em.createNamedQuery("ImpEmbed.loadImpEmbed", ImpEmbedTO.class).setParameter("extServiceId", extServiceId).setParameter("extUserId", extUserId).setParameter("impId", impId).getSingleResult();
        }catch(NoResultException e){
            logger.log(Level.SEVERE, "Implementation: {0} not found in DB; external user: {1}", new Object[]{impId, extUserId});
            throw new EntityNotFoundException(e.getMessage());
        }catch(Exception e){
            logger.log(Level.SEVERE, "Something went wrong{0}Implementation: {1}, external user: {2}", new Object[]{e.getMessage(), impId, extUserId});
            throw new ValidationFailedException(e.getMessage());
        }

        ImpEmbed impEmbed = em.find(ImpEmbed.class, impEmbedTo.getId());
        em.remove(impEmbed);
        logger.log(Level.INFO, "Successfully removed Embeded imp: {0} for external user: {1}", new Object[]{impId, extUserId});
    }


    // use case diagram: All: create application
    @Override
    public ApplicationTO createApp(String appName, String description, String groupName, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, EntityNotFoundException {
        try{

            User caller = getCallerUser();
            logger.log(Level.INFO, "Attempting to create worklfow for user: {0}", caller.getLoginName());
            UserGroup g = em.createNamedQuery("UserGroup.findByName", UserGroup.class).setParameter("name", groupName).getSingleResult();
            String err = canUserCreateApplication(caller, g);
            if(err != null){
                logger.log(Level.SEVERE, err);
                throw new AuthorizationException(err);
            }
            //validate app name
            if(!appName.matches("[A-Za-z0-9_-]{3,250}")){
                //context.setRollbackOnly();
                logger.log(Level.SEVERE, "Application name: {0} failed validation for user {1}", new Object[]{appName, caller.getLoginName()});
                throw new ValidationFailedException("application names can only contain alphanumeric, - and _ characters and must be between 3 and 250 charaters long");
            }
            if(em.createNamedQuery("Application.findByName", Application.class).setParameter("name", appName).getResultList().size() > 0){
                logger.log(Level.SEVERE, "User {0} attempted to create a workflow that already exists with name {1}", new Object[]{caller.getLoginName(), appName});
                throw new EntityAlreadyExistsException("application'"+appName+"' already exists");
            }
            Application a = new Application(appName, description, caller, g, groupRead, othersRead, groupDownload, othersDownload, groupModify, published);
            em.persist(a);
            logger.log(Level.INFO, "Created worklfow {0} for user: {1}", new Object[]{a.getName(), caller.getLoginName()});
            return new ApplicationTO(a);
        }catch(NoResultException e){
            logger.log(Level.SEVERE, "group ''{0}'' does not exist for creating application {1}", new Object[]{groupName, appName});
            throw new EntityNotFoundException("group '"+groupName+"' does not exist");
        }
    }

    @Override
    public ApplicationTO createApp(String appName, String description, Date created, Date updated, int groupId, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, EntityNotFoundException {
        try{
            User caller = getCallerUser();
            UserGroup g = em.createNamedQuery("UserGroup.findById", UserGroup.class).setParameter("id", groupId).getSingleResult();
            String err = canUserCreateApplication(caller, g);
            if(err != null){
                throw new AuthorizationException(err);
            }
            //validate app name
            if(!appName.matches("[A-Za-z0-9_-]{3,250}")){
                //context.setRollbackOnly();
                throw new ValidationFailedException("application names can only contain alphanumeric, - and _  characters and must be between 3 and 250 charaters long");
            }
            if(em.createNamedQuery("Application.findByName", Application.class).setParameter("name", appName).getResultList().size() > 0){
                throw new EntityAlreadyExistsException("application'"+appName+"' already exists");
            }
            Application a = new Application(appName, description, created, updated, caller, g, groupRead, othersRead, groupDownload, othersDownload, groupModify, published);
            em.persist(a);
            return new ApplicationTO(a);
        }catch(NoResultException e){
            throw new EntityNotFoundException("group '"+groupId+"' does not exist");
        }
    }

    // use case diagram: Admin: update application
    // use case diagram: User(owner or group+modify flag): update application
    @Override
    public ApplicationTO updateAppDetails(Integer appId, String description) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserModifyApplication(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate description
        if(description != null){
            a.setDescription(description);
        }
        a = em.merge(a);
        return new ApplicationTO(a);
    }

    @Override
    public ApplicationTO updateAppTimestamp(Integer appId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserModifyApplication(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate description
        a.setUpdated(null);
        a = em.merge(a);
        return new ApplicationTO(a);
    }

    @Override
    public ImplementationTO updateImpTimestamp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserUpdateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate description
        i.setUpdated(null);
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    // use case diagram: Admin: update application access
    // use case diagram: User(owner): update application access
    @Override
    public ApplicationTO updateAppAccess(Integer appId, String groupName, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        UserGroup g;
        try{
            g = em.createNamedQuery("UserGroup.findByName", UserGroup.class).setParameter("name", groupName).getSingleResult();
        }catch(NoResultException e){
            g = null;
        }
        String err = canUserUpdateAppAccess(caller, a, g);
        if(err != null){
            throw new AuthorizationException(err);
        }
        a.setGroup(g); //application is the owning side
        if(groupRead != null){
            a.setGroupRead(groupRead);
        }
        if(othersRead != null){
            a.setOthersRead(othersRead);
        }
        if(groupDownload != null){
            a.setGroupDownload(groupDownload);
        }
        if(othersDownload != null){
            a.setOthersDownload(othersDownload);
        }
        if(groupModify != null){
            a.setGroupModify(groupModify);
        }
        if(published != null){
            a.setPublished(published);
        }
        em.merge(a);
        return new ApplicationTO(a);
    }

    // use case diagram: Admin: change application owner
    // use case diagram: User(owner): change application owner
    @Override
    public ApplicationTO changeAppOwner(Integer appId, String newOwnerLoginName) throws EntityNotFoundException, AuthorizationException, ValidationFailedException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        User u;
        try{
            u = em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", newOwnerLoginName).getSingleResult();
        }catch(NoResultException e){
            u = null;
        }
        String err = canUserChangeAppOwner(caller, a, u);
        if(err != null){
            throw new AuthorizationException(err);
        }
        a.setOwner(u);
        a = em.merge(a);
        return new ApplicationTO(a);
    }

    // use case diagram: Admin: delete application
    // use case diagram: User(owner): delete application
    @Override
    public void deleteApp(Integer appId) throws EntityNotFoundException, AuthorizationException, NotSafeToDeleteException, FileOperationFailedException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserDeleteApplication(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        deleteAllAppFiles(a);
        for(Implementation i: a.getImplementations()){
            deleteImp(i.getId());
            /*
            for(ImpAttribute ia: i.getAttributes().values()){
                em.remove(ia);
            }
            for(ImpComment ic: i.getComments()){
                em.remove(ic);
            }*/
        }
        /*
        for(AppAttribute aa: a.getAttributes().values()){
            em.remove(aa);
        }
        for(AppComment ac: a.getComments()){
            em.remove(ac);
        }*/

        // delete existing attributes
        em.createNamedQuery("AppAttribute.deleteAllByAppID").setParameter("appId", a.getId()).executeUpdate();


        em.remove(a);
    }

    @Override
    public List<AppListItemTO> getAppList() {
        return em.createNamedQuery("Application.listAll", AppListItemTO.class).getResultList();
    }

    @Override
    public List<AppListItemTO> getAppList(String impAttrName, String impAttrVal) throws IllegalAttributeNameException {
        if(!impAttrName.matches("(field|attribute)\\.[a-zA-Z0-9_\\-]+")){
            throw new IllegalAttributeNameException("invalid attribute name: '"+impAttrName+"', it should match (field|attribute)\\.[a-zA-Z0-9_-]+");
        }
        return em.createNamedQuery("Application.listByImpAttr", AppListItemTO.class).setParameter("impAttrName", impAttrName.substring(impAttrName.indexOf('.')+1)).setParameter("impAttrValue", impAttrVal).getResultList();
    }

    // use case diagram: Administrator: create application attribute
    // use case diagram: User(owner or group+flag): create application attribute
    @Override
    public AttributeTO createAppAttr(Integer appId, String attrName, String attrValue) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserModifyApplication(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //check to see if attribute exists
        if(a.getAttributes().containsKey(attrName)){
            //context.setRollbackOnly();
            throw new EntityAlreadyExistsException("attribute '"+attrName+"' already exists");
        }
        AppAttribute attr = new AppAttribute(a, attrName, attrValue);
        em.persist(attr);
        return new AttributeTO(attr);
    }

    @Override
    public void updateAppAttrList(Integer appId, List<AttributeTO> aList) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException{
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserModifyApplication(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        // delete existing attributes
        em.createNamedQuery("AppAttribute.deleteAllByAppID").setParameter("appId", a.getId()).executeUpdate();

        // adding new attributes
        AttributeTO item;
        Iterator<AttributeTO> iterator = aList.iterator();
        while(iterator.hasNext()){
            item = iterator.next();
            //check to see if attribute exists
            if(item!=null)
            {
                if(a.getAttributes().containsKey(item.getName())){
                    //context.setRollbackOnly();
                    throw new EntityAlreadyExistsException("attribute '"+item.getName()+"' already exists");
                }
                em.persist(new AppAttribute(a, item.getName(), item.getValue()));
            }
        }
    }

    @Override
    public void updateImpAttrList(Integer impId, List<AttributeTO> iList) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException{
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserUpdateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        // delete existing attributes
        em.flush();
        em.createNamedQuery("ImpAttribute.deleteAllByImpID").setParameter("impId", i.getId()).executeUpdate();
        em.flush();
        // adding new attributes
        AttributeTO item;
        Iterator<AttributeTO> iterator = iList.iterator();
        while(iterator.hasNext()){
            item = iterator.next();
            //check to see if attribute exists
            if(item!=null)
            {
                if(i.getAttributes().containsKey(item.getName())){
                    //context.setRollbackOnly();
                    throw new EntityAlreadyExistsException("attribute '"+item.getName()+"' already exists");
                }
                if(item.getName() == null && item.getValue() == null){
                    logger.log(Level.SEVERE, "Imp: " + i.getId() + " Value of attribute has a completely null attribute");
                    throw new ValidationFailedException("Value of an attribute was null");
                }
                if(item.getValue() == null){
                    logger.log(Level.SEVERE, "Imp: " + i.getId() + " Value of attribute: " + item.getName() + " is null");
                    throw new ValidationFailedException("Value of: " + item.getName() + " was null.");
                }
                if(item.getName() == null){
                    logger.log(Level.SEVERE, "Imp: " + i.getId() + " attribute item has no name with value: " + item.getValue());
                    throw new ValidationFailedException("Attribute with value: " + item.getValue()+ " has no name");
                }
                em.persist(new ImpAttribute(i, item.getName(), item.getValue()));
            }
        }
    }


    // use case diagram: Administrator: update application attribute
    // use case diagram: User(owner or group+flag): update application attribute
    @Override
    public AttributeTO updateAppAttr(Integer appAttrId, String attrValue) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        AppAttribute a = em.find(AppAttribute.class, appAttrId);
        String err = canUserModifyAppAttr(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate attribute value
        a.setValue(attrValue);
        a = em.merge(a);
        return new AttributeTO(a);
    }

    // use case diagram: Administrator: delte application attribute
    // use case diagram: User(owner or group+flag): delete application attribute
    @Override
    public void deleteAppAttr(Integer appAttrId) throws EntityNotFoundException, AuthorizationException {
        User caller = getCallerUser();
        AppAttribute a = em.find(AppAttribute.class, appAttrId);
        String err = canUserModifyAppAttr(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        em.remove(a);
    }

    @Override
    public List<AttributeTO> getAppAttributesByKey(String attrKey) {
        return em.createNamedQuery("AppAttribute.listAttributesByKey", AttributeTO.class).setParameter("attrKey", attrKey).getResultList();
    }

    @Override
    public List<AttrListItemTO> getAppAttributeList(Collection<Integer> appIDs) {
        return em.createNamedQuery("AppAttribute.listByAppIDs", AttrListItemTO.class).setParameter("appIDs", appIDs).getResultList();
    }

    @Override
    public List<AttrListItemTO> getAppAttributeList(Collection<Integer> appIDs, Collection<String> attrNames) {
        return em.createNamedQuery("AppAttribute.listByAppIDsNames", AttrListItemTO.class).setParameter("appIDs", appIDs).setParameter("attrNames", attrNames).getResultList();
    }

    @Override
    public List<AttributeTO> getAppAttributesByKeyAndFilter(String attrKey, String query) {
        return em.createNamedQuery("AppAttribute.listAttributesByKeyAndFilter", AttributeTO.class).setParameter("attrKey", attrKey).setParameter("filter", query).getResultList();
    }

    @Override
    public List<AttributeTO> getImpAttributesByKeyAndFilter(String attrKey, String query) {
        return em.createNamedQuery("ImpAttribute.listAttributesByKeyAndFilter", AttributeTO.class).setParameter("attrKey", attrKey).setParameter("filter", query).getResultList();
    }

    // use case diagram: All: create application comment
    @Override
    public CommentTO createAppComm(Integer appId, String message) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        if(a == null){
            //context.setRollbackOnly();
            throw new EntityNotFoundException("application '"+appId+"' does not exist");
        }
        AppComment c = new AppComment(a, caller, message);
        em.persist(c);
        return new CommentTO(c);
    }

/*
    // use case diagram: Administrator: delte application comment
    // use case diagram: User(owner): delete appliction comment
    @Override
    public void deleteAppComm(Integer appCommId) throws EntityNotFoundException, AuthorizationException {
        User caller = getCaller();
        AppComment c = em.find(AppComment.class, appCommId);
        if(c == null){
            //context.setRollbackOnly();
            throw new EntityNotFoundException("comment '"+appCommId+"' does not exist");
        }
        if(!(caller.isAdmin() || caller.equals(c.getUser()))){
            //context.setRollbackOnly();
            throw new AuthorizationException("you are not allowed to remove comment +'"+appCommId+"'");
        }
        em.remove(c);
    }
*/

    // use case diagram: Administrator: create implementation
    // use case diagram: User(owner or group+flag): create implementation
    @Override
    public ImplementationTO createImp(Integer appId, String platformName, String version) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserCreateImps(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        try{
            Platform p = em.createNamedQuery("Platform.findByName", Platform.class).setParameter("name", platformName).getSingleResult();
            //check to see if it already exists
            if(em.createNamedQuery("Implementation.findByAppIDAndVersionAndPlatformID", Implementation.class).setParameter("appId", appId).setParameter("version", version).setParameter("platId", p.getId()).getResultList().size() > 0){
                throw new EntityAlreadyExistsException("implementation '"+version+"' ("+p.getName()+") already exists");
            }
            Implementation i = new Implementation(version, p, a);
            em.persist(i);
            return new ImplementationTO(i);
        }catch(NoResultException e){
            //context.setRollbackOnly();
            throw new ValidationFailedException("platform '"+platformName+"' does not exist");
        }
    }

    @Override
    public ImplementationTO createImp(Integer appId, int platformId, String version, Date created, Date updated) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserCreateImps(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        try{
            Platform p = em.createNamedQuery("Platform.findById", Platform.class).setParameter("platformId", platformId).getSingleResult();
            //check to see if it already exists
            if(em.createNamedQuery("Implementation.findByAppIDAndVersionAndPlatformID", Implementation.class).setParameter("appId", appId).setParameter("version", version).setParameter("platId", p.getId()).getResultList().size() > 0){
                throw new EntityAlreadyExistsException("implementation '"+version+"' ("+p.getName()+") already exists");
            }
            Implementation i = new Implementation(version, p, a, created, updated);
            em.persist(i);
            return new ImplementationTO(i);
        }catch(NoResultException e){
            //context.setRollbackOnly();
            throw new ValidationFailedException("platform '"+platformId+"' does not exist");
        }
    }


    @Override
    public ImplementationTO createImp(Integer appId, String platformName, String platformVersion, String version) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = canUserCreateImps(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        try{
            Platform p = em.createNamedQuery("Platform.findByNameAndVersion", Platform.class).setParameter("name", platformName).setParameter("version", platformVersion).getSingleResult();
            //check to see if it already exists
            if(em.createNamedQuery("Implementation.findByAppIDAndVersionAndPlatformID", Implementation.class).setParameter("appId", appId).setParameter("version", version).setParameter("platId", p.getId()).getResultList().size() > 0){
                throw new EntityAlreadyExistsException("implementation '"+version+"' ("+p.getName()+") already exists");
            }
            Implementation i = new Implementation(version, p, a);
            em.persist(i);
            return new ImplementationTO(i);
        }catch(NoResultException e){
            //context.setRollbackOnly();
            throw new ValidationFailedException("platform '"+platformName+"' does not exist");
        }
    }

    // use case diagram: Administrator: update implementation
    // use case diagram: User(owner or group+flag): update implementation
    @Override
    public ImplementationTO updateImp(Integer impId, String platformName, String version) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserUpdateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        if(platformName != null){
            try{
                Platform p = em.createNamedQuery("Platform.findByName", Platform.class).setParameter("name", platformName).getSingleResult();
                i.setPlatform(p);
            }catch(NoResultException e){
                //context.setRollbackOnly();
                throw new ValidationFailedException("platform does not exist");
            }
        }
        if(version != null){
            i.setVersion(version);
        }
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    @Override
    public ImplementationTO updateVersionedImp(Integer impId, String platformName, String platformVersion, String version) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserUpdateImp(caller, i);
        System.out.println(impId + " " + platformName  + " " + platformVersion + " " + version);
        if(err != null){
            throw new AuthorizationException(err);
        }
        if(platformName != null){
            try{
                Platform p = em.createNamedQuery("Platform.findByNameAndVersion", Platform.class).setParameter("name", platformName).setParameter("version", platformVersion).getSingleResult();
                i.setPlatform(p);
            }catch(NoResultException e){
                //context.setRollbackOnly();
                throw new ValidationFailedException("platform does not exist");
            }
        }
        if(version != null){
            i.setVersion(version);
        }
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    @Override
    public ImplementationTO submitImp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserSubmitImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        i.setStatus(ImplementationStatus.READY);
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    // use case diagram: Administrator: validate implementation
    // use case diagram: Validator: validate implementation
    @Override
    public ImplementationTO validateImp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserValidateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        i.setStatus(ImplementationStatus.PUBLIC);
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    // use case diagram: Administrator: fail implementation validation
    // use case diagram: Validator: fail implementation validation
    @Override
    public ImplementationTO failImp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserValidateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        i.setStatus(ImplementationStatus.FAILED);
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    // use case diagram: Administrator: mark implementation old
    // use case diagram: User(owner): mark implementation old
    @Override
    public ImplementationTO markImpOld(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserMarkImpOld(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        i.setStatus(ImplementationStatus.OLD);
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    // use case diagram: Administrator: mark implementation obsolete
    // use case diagram: User(owner): mark implementation obsolete
    @Override
    public ImplementationTO markImpDeprecated(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserMarkImpDeprecated(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        i.setStatus(ImplementationStatus.DEPRECATED);
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    // use case diagram: Administrator: mark implementation compromised
    // use case diagram: User(owner): mark implementation compromised
    @Override
    public ImplementationTO markImpCompromised(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserMarkImpCompromised(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        i.setStatus(ImplementationStatus.COMPROMISED);
        i = em.merge(i);
        return new ImplementationTO(i);
    }

    // use case diagram: Administrator: delete implementation
    // use case diagram: User(owner or group+flag): delete implementation
    @Override
    public void deleteImp(Integer impId) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        //System.out.println("Deleting imp: "+i.getId()+", of version "+i.getVersion());

        String err = canUserDeleteImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //System.out.println("User can delete imp: "+impId);

        deleteAllImpFiles(i);
        //System.out.println("Files deleted: "+impId);

        // delete existing attributes
        em.createNamedQuery("ImpAttribute.deleteAllByImpID").setParameter("impId", i.getId()).executeUpdate();

        /*for(ImpAttribute ia: i.getAttributes().values()){
            System.out.println("Deleting attr: "+ia.getName());

            em.remove(ia);
        }*/

        //System.out.println("Imps attrs deleted: "+i.getAttributes().values().size());

        for(ImpComment ic: i.getComments()){
            em.remove(ic);
        }

        //System.out.println("Comments deleted: "+impId);

        em.remove(i);

        //System.out.println("Imp deleted: "+impId);
    }

    // use case diagram: Administrator: create implementation attribute
    // use case diagram: User(owner or group+flag): create implementation attribute
    @Override
    public AttributeTO createImpAttr(Integer impId, String attrName, String attrValue) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err = canUserUpdateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //check to see if attribute exists
        if(i.getAttributes().containsKey(attrName)){
            //context.setRollbackOnly();
            throw new EntityAlreadyExistsException("attribute '"+attrName+"' already exists");
        }
        ImpAttribute attr = new ImpAttribute(i, attrName, attrValue);
        em.persist(attr);
        return new AttributeTO(attr);
    }

    // use case diagram: Administrator: update implementation attribute
    // use case diagram: User(owner or group+flag): update implementation attribute
    @Override
    public AttributeTO updateImpAttr(Integer impAttrId, String attrValue) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        ImpAttribute a = em.find(ImpAttribute.class, impAttrId);
        String err = canUserUpdateImpAttr(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate attribute value
        if(attrValue != null){
            a.setValue(attrValue);
        }
        em.merge(a);
        return new AttributeTO(a);
    }

    // use case diagram: Administrator: delete implementation attribute
    // use case diagram: User(owner or group+flag): delete implementation attribute
    @Override
    public void deleteImpAttr(Integer impAttrId) throws EntityNotFoundException, AuthorizationException {
        User caller = getCallerUser();
        ImpAttribute a = em.find(ImpAttribute.class, impAttrId);
        String err = canUserUpdateImpAttr(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate attribute value
        em.remove(a);
    }

    @Override
    public List<AttrListItemTO> getImpAttributeList(Collection<Integer> impIDs) {
        return em.createNamedQuery("ImpAttribute.listByImpIDs", AttrListItemTO.class).setParameter("impIDs", impIDs).getResultList();
    }

    @Override
    public List<AttrListItemTO> getImpAttributeList(Collection<Integer> impIDs, Collection<String> attrNames) {
        return em.createNamedQuery("ImpAttribute.listByImpIDsNames", AttrListItemTO.class).setParameter("impIDs", impIDs).setParameter("attrNames", attrNames).getResultList();
    }

    @Override
    public List<ImpListItemTO> getImpListForApps(Integer appID, String attrName, String attrValue) {
        return em.createNamedQuery("Implementation.listByAppIDsAttr", ImpListItemTO.class).setParameter("appID", appID).setParameter("attrName", attrName).setParameter("attrValue", attrValue).getResultList();
    }

    @Override
    public List<ImpListItemTO> getImpListForApps(Collection<Integer> appIDs) {
        return em.createNamedQuery("Implementation.listByAppIDs", ImpListItemTO.class).setParameter("appIDs", appIDs).getResultList();
    }

    @Override
    public List<ImpListItemTO> getImpList() {
        return em.createNamedQuery("Implementaion.listAll", ImpListItemTO.class).getResultList();
    }

    // use case diagram: All: create implementation comment
    @Override
    public CommentTO createImpComm(Integer impId, String message) throws EntityNotFoundException, ValidationFailedException, AuthorizationException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        if(i == null){
            //context.setRollbackOnly();
            throw new EntityNotFoundException("implementation does not exist");
        }
        ImpComment c = new ImpComment(i, caller, message);
        em.persist(c);
        return new CommentTO(c);
    }

    // use case diagram: Administrator: upload implementation file
    // use case diagram: User(owner or group+flag): upload implementation file
    @Override
    public ImpFileTO uploadImpFile(Integer impId, String pathName, InputStream source) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, FileOperationFailedException{
        Implementation i = em.find(Implementation.class, impId);

        User caller = getCallerUser();
        String err = canUserUpdateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }

        validatePath(pathName);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try{
            File appDir = new File(this.repoPath + i.getApplication().getId().toString());
            validateAppDir(appDir, source);

            File impDir = new File(appDir.getAbsolutePath()+"/"+i.getId().toString());
            validateImpDir(impDir, source);

            File f = new File(impDir.getAbsolutePath()+"/"+pathName);
            checkFileExists(f, pathName, source);

            bis = new BufferedInputStream(source);
            bos = new BufferedOutputStream(new FileOutputStream(f));

            copyBytesOfUploadFile(bis, bos);

            return new ImpFileTO(i.getApplication().getId(), i.getId(), pathName);
        }catch(IOException e){
            logger.log(Level.SEVERE, "internal error when uploading file: {0}", e.getMessage()); //log error
            throw new FileOperationFailedException("could not upload file (server error)");
        }finally{
            closeStreams(bis, source, bos);
        }
    }

    @Override
    public AppFileTO uploadAppFile(Integer appId, String pathName, InputStream source) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, FileOperationFailedException{
        Application a = em.find(Application.class, appId);

        User caller = getCallerUser();
        String err = canUserModifyApplication(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }

        validatePath(pathName);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try{
            File appDir = new File(this.repoPath + a.getId());
            validateAppDir(appDir, source);

            File f = new File(appDir.getAbsolutePath()+"/"+pathName);
            checkFileExists(f, pathName, source);

            bis = new BufferedInputStream(source);
            bos = new BufferedOutputStream(new FileOutputStream(f));

            copyBytesOfUploadFile(bis, bos);

            return new AppFileTO(a.getId(), pathName);
        }catch(IOException e){
            logger.log(Level.SEVERE, "internal error when uploading file: {0}", e.getMessage()); //log error
            throw new FileOperationFailedException("could not upload file (server error)");
        }finally{
            closeStreams(bis, source, bos);
        }
    }

    @Override
    public ImpFile getImpFile(Integer impId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException {
        User caller = getCallerUser();
        Implementation i = em.find(Implementation.class, impId);
        String err;
        if(caller != null){
            err = canUserDownloadImpFile(caller, i);
        }else{
            err = canPublicDownloadImpFile(i);
        }
        if(err != null){
            throw new AuthorizationException(err);
        }

        validatePath(pathName);

        try{
            FileInputStream is = new FileInputStream(this.repoPath + i.getApplication().getId().toString()+"/"+i.getId().toString()+"/"+pathName);
            File f = new File(this.repoPath + i.getApplication().getId().toString()+"/"+i.getId().toString()+"/"+pathName);
            return new ImpFile(pathName, is, f.length());
        }catch(FileNotFoundException e){
            throw new FileOperationFailedException("file not found");
        }
    }

    @Override
    public ImpFile getAppFile(Integer appId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException {
        User caller = getCallerUser();
        Application a = em.find(Application.class, appId);
        String err = null;
        if(caller != null){
            err = canUserDownloadAppFile(caller, a);
//        }else{
//            err = canPublicDownloadAppFile(i);
        }
        if(err != null){
            throw new AuthorizationException(err);
        }

        validatePath(pathName);

        try{
            FileInputStream is = new FileInputStream(this.repoPath + a.getId().toString()+"/"+pathName);
            File f = new File(this.repoPath + a.getId()+"/"+pathName);
            return new ImpFile(pathName, is, f.length());
        }catch(FileNotFoundException e){
            throw new FileOperationFailedException("file not found");
        }
    }

    // use case diagram: Administrator: delete implementation file
    // use case diagram: User(owner or group+flag): delete implementation file
    @Override
    public void deleteImpFile(Integer impId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException {
        Implementation i = em.find(Implementation.class, impId);

        User caller = getCallerUser();
        String err = canUserUpdateImp(caller, i);
        if(err != null){
            throw new AuthorizationException(err);
        }

        validatePath(pathName);

        File appDir = new File(this.repoPath + i.getApplication().getId().toString());
        File impDir = new File(appDir.getAbsolutePath() + "/" + i.getId().toString());
        File f = new File(impDir.getAbsolutePath() + "/" + pathName);

        deleteFile(f);
    }

    @Override
    public void deleteAppFile(Integer appId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException {
        Application a = em.find(Application.class, appId);

        User caller = getCallerUser();
        String err = canUserModifyApplication(caller, a);
        if(err != null){
            throw new AuthorizationException(err);
        }

        validatePath(pathName);

        File appDir = new File(this.repoPath + a.getId().toString());
        File f = new File(appDir.getAbsolutePath() + "/" + pathName);

        deleteFile(f);
    }

    // use case diagram: Administrator: submit implementation for validation
    // use case diagram: User(owner): submit implementation for validation
    @Override
    public List<ImpFileTO> getFilesForImps(List<Integer> idc) {
        List<ImpFileTO> fl = new LinkedList<ImpFileTO>();
        for(Integer i: idc){
            Implementation imp = em.find(Implementation.class, i);
            if(imp == null){
                continue;
            }
            File appDir = new File(this.repoPath + imp.getApplication().getId().toString());
            File impDir = new File(appDir.getAbsolutePath() + "/" + imp.getId().toString());
            File[] fs = impDir.listFiles();
            if(fs != null && fs.length > 0){
                Arrays.sort(fs);
                for(File f: fs){
                    if(f.isFile()){
                    fl.add(new ImpFileTO(imp.getApplication().getId(), imp.getId(), f.getName()));
                    }
                }
            }
        }
        return fl;
    }

    @Override
    public List<AppFileTO> getFilesForApps(List<Integer> idc) {
        List<AppFileTO> fl = new LinkedList<AppFileTO>();
        for(Integer i: idc){
            Application app = em.find(Application.class, i);
            if(app == null){
                continue;
            }
            File appDir = new File(this.repoPath + app.getId().toString());
            File[] fs = appDir.listFiles();
            if(fs != null && fs.length > 0){
                Arrays.sort(fs);
                for(File f: fs){
                    if(f.isFile()){
                        fl.add(new AppFileTO(app.getId(), f.getName()));
                    }
                }
            }
        }
        return fl;
    }

    @Override
    public List<UserTO> listUsers() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list users
            return new ArrayList<UserTO>(0);
        }
        return em.createNamedQuery("User.loadAllUsers", UserTO.class).getResultList();
    }

    @Override
    public List<UserTO> listUsersOfGroup(int groupId) {
        User caller = getListCaller();
        UserGroup g = em.find(UserGroup.class, groupId);
        if(caller == null || g == null || !caller.isActive()){ //only active users may list users of a group
            return new ArrayList<UserTO>(0);
        }
        return em.createNamedQuery("User.loadUsersOfGroup", UserTO.class).setParameter("groupId", groupId).getResultList();
    }

    @Override
    public List<PlatformTO> listPlatforms() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return new ArrayList<PlatformTO>(0);
        }
        return getPlatforms();
    }

    @Override
    public ImplementationTO toggleSubmittable(int _imp) throws EntityNotFoundException, AuthorizationException, ValidationFailedException
    {

        if(this.canUserUpdateImp(getCallerUser().getId(), _imp) != null){
            logger.log(Level.SEVERE, "User not logged in or not permitted to perform action, id: {0}", getCallerUser().getLoginName());
            throw new AuthorizationException("User not logged in or not permitted to perform action");
        }

        Implementation imp = em.find(Implementation.class, _imp);
        if(imp == null){
            logger.log(Level.SEVERE, "Implementation with id: {0} not found in db", _imp);
            throw new EntityNotFoundException("Implementation not found in database");
        }

        /*
         * Only perform checks if the imnplementation is not submittable already
         */
        if(!imp.isSubmittable()){
            if(!imp.getStatus().equals(ImplementationStatus.PUBLIC)){
                logger.log(Level.SEVERE, "Cannot toggle submittable; Implementation with id: {0} is not public.", _imp);
                throw new ValidationFailedException("Implementation must be public to be marked as submittable");
            }

            /*
             * Check if the implementation has an execution node
             */
            Query q = em.createNamedQuery("ImpAttributes.loadLikeAttributesOfImplementationByName");
            q.setParameter("impId", _imp);
            q.setParameter("attrName", "execution.parameters%");

            List<AttributeTO> list = q.getResultList();

            if(list.isEmpty()){
                logger.log(Level.SEVERE, "Implementation: {0} needs an execution node to be marked as submittable", _imp);
                throw new ValidationFailedException("Implementation needs an execution node to be marked as submittable");
            }
        }

        imp.setSubmittable(!imp.isSubmittable());

        ImplementationTO temp = new ImplementationTO(em.merge(imp));
        em.flush();

        logger.log(Level.INFO, "Implementation: {0}({1}) has submission = {2}", new Object[]{imp.getApplication().getName(), imp.getVersion(), imp.isSubmittable()});

        return temp;
    }

    @Override
    public List<ImpEmbedTO> listImpsForEmbedding() {
        return em.createNamedQuery("ImpEmbed.listAll", ImpEmbedTO.class).getResultList();
    }

    @Override
    public List<ImpEmbedTO> listImpsForEmbedding(String extServiceId) {
        return em.createNamedQuery("ImpEmbed.listAllByExtServiceId", ImpEmbedTO.class).setParameter("extServiceId", extServiceId).getResultList();
    }

    @Override
    public List<ImpEmbedTO> listImpsForEmbedding(String extServiceId, String extUserId) {
        return em.createNamedQuery("ImpEmbed.listAllByExtServiceIdAndExtUserId", ImpEmbedTO.class).setParameter("extServiceId", extServiceId).setParameter("extUserId", extUserId).getResultList();
    }

    @Override
    public List<AttributeTO> listImpAttrsByKey(String key){
        return em.createNamedQuery("ImpAttribute.loadAttributesByKey", AttributeTO.class).setParameter("attrKey", key).getResultList();
    }

    @Override
    public List<PlatformTO> listPlatformsByName(String name) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return new ArrayList<PlatformTO>(0);
        }
        return em.createNamedQuery("Platform.listPlatformsByName", PlatformTO.class).setParameter("name", name).getResultList();
    }

    @Override
    public PlatformTO loadPlatform(String name, String version) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return null;
        }
        return getPlatform(name, version);
    }

    @Override
    public PlatformTO loadPlatform(int id) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return null;
        }
        return getPlatform(id);
    }

    @Override
    public UserTO loadUser(String name){
        return em.createNamedQuery("User.loadByLoginName", UserTO.class).setParameter("loginName", name).getSingleResult();
    }

    @Override
    public GroupTO loadGroup(String name){
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return null;
        }
        return getGroup(name);
    }

    @Override
    public GroupTO getGroup(String name){
        UserGroup group = em.createNamedQuery("UserGroup.findByName", UserGroup.class).setParameter("name", name).getSingleResult();
        if(group!=null){
            return new GroupTO(group);
        }
        return null;
    }

    @Override
    public List<GroupTO> listGroups() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list groups
            return new ArrayList<GroupTO>(0);
        }
        return em.createNamedQuery("UserGroup.loadAllUserGroups", GroupTO.class).getResultList();
    }

    @Override
    public List<GroupTO> listGroupsOfUser(int userId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list groups of a user
            return new ArrayList<GroupTO>(0);
        }
        return em.createNamedQuery("UserGroup.loadUserGroupsOfUser", GroupTO.class).setParameter("userId", userId).getResultList();
    }

    @Override
    public List<GroupTO> listOwnedGroupsOfUser(int userId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list owned groups of a user
            return new ArrayList<GroupTO>(0);
        }
        return em.createNamedQuery("UserGroup.loadOwnedUserGroupsOfUser", GroupTO.class).setParameter("leaderId", userId).getResultList();
    }

    @Override
    public List<ApplicationTO> listApplications() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            //return new ArrayList<ApplicationTO>(0);
            return em.createNamedQuery("Application.loadPublishedApplications", ApplicationTO.class).getResultList();
        }
        return em.createNamedQuery("Application.loadAllApplications", ApplicationTO.class).getResultList();
    }

    @Override
    public List<ApplicationTO> listApplicationsPrivate() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return null;
        }
        return em.createNamedQuery("Application.loadNonPublishedApplications", ApplicationTO.class).getResultList();
    }

    @Override
    public List<ApplicationTO> listApplicationsReadyForValidation() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return null;
        }
        return em.createNamedQuery("Application.loadApplicationsReadyForValidation", ApplicationTO.class).getResultList();
    }


    @Override
    public List<ApplicationTO> listApplicationsUserCanRead() {
        System.out.println("getAppsReadInvoked");
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            //return new ArrayList<ApplicationTO>(0);

            // the intended functionality is not to show non-validated wfs for guest users,
            // but since there are only a few validated wfs and we want to show some content
            // to attract users, temporaily non-validated wfs can be seen as well
            //return em.createNamedQuery("Application.loadPublishedApplications", ApplicationTO.class).getResultList();
            return em.createNamedQuery("Application.loadAppsReadableByOthers", ApplicationTO.class).getResultList();
        }else{
            if(caller.isAdmin()){
                return em.createNamedQuery("Application.loadAllApplications", ApplicationTO.class).getResultList();
            }else{
                return em.createNamedQuery("Application.loadAppsUserCanRead", ApplicationTO.class).setParameter("userId", caller.getId()).getResultList();
            }
        }
    }

    @Override
    public List<ApplicationTO> listApplicationsUserCanModify() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<ApplicationTO>(0);
        }else{
            if(caller.isAdmin()){
                return em.createNamedQuery("Application.loadAllApplications", ApplicationTO.class).getResultList();
            }else{
                return em.createNamedQuery("Application.loadAppsUserCanModify", ApplicationTO.class).setParameter("userId", caller.getId()).getResultList();
            }
        }
    }

    @Override
    public Date appTimestamp() {
        return em.createNamedQuery("Application.lastUpdated", Date.class).getSingleResult();
    }

    @Override
    public Date impTimestamp() {
        return em.createNamedQuery("Implementation.lastUpdated", Date.class).getSingleResult();
    }

    @Override
    public ApplicationTO loadApplication(int appId) {
        return em.createNamedQuery("Application.loadApplication", ApplicationTO.class).setParameter("appId", appId).getSingleResult();
    }

    @Override
    public List<ApplicationTO> listApplications(String appAttrNameFilter, String appAttrValueFilter) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            //return new ArrayList<ApplicationTO>(0);
            return em.createNamedQuery("Application.loadFilteredPublishedApplications", ApplicationTO.class).setParameter("appAttrNameFilter", "%"+appAttrNameFilter+"%").setParameter("appAttrValueFilter", "%"+appAttrValueFilter+"%").getResultList();
        }
        return em.createNamedQuery("Application.loadFilteredApplications", ApplicationTO.class).setParameter("appAttrNameFilter", "%"+appAttrNameFilter+"%").setParameter("appAttrValueFilter", "%"+appAttrValueFilter+"%").setParameter("userId", caller.getId()).getResultList();
    }

    @Override
    public List<ApplicationTO> listApplicationsOfUser(int userId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<ApplicationTO>(0);
        }
        return em.createNamedQuery("Application.loadApplicationsOfUser", ApplicationTO.class).setParameter("ownerId", userId).getResultList();
    }

    @Override
    public List<ApplicationTO> listApplicationsOfGroup(int groupId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<ApplicationTO>(0);
        }
        return em.createNamedQuery("Application.loadApplicationsOfGroup", ApplicationTO.class).setParameter("groupId", groupId).getResultList();
    }

    @Override
    public List<AttributeTO> listAttributesOfApplication(int appId) {
        /*User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<AttributeTO>(0);
        }*/
        return em.createNamedQuery("AppAttribute.loadAttributesOfApplication", AttributeTO.class).setParameter("appId", appId).getResultList();
    }

    @Override
    public ImplementationTO loadImplementation(int impId) {
        return em.createNamedQuery("Implementation.loadImplementation", ImplementationTO.class).setParameter("impId", impId).getSingleResult();
    }

    @Override
    public List<ImplementationTO> listImplementationsOfApplication(int appId) {
        /*User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<ImplementationTO>(0);
        }*/
        return em.createNamedQuery("Implementation.loadImplementationsOfApplication", ImplementationTO.class).setParameter("appId", appId).getResultList();
    }

    @Override
    public List<ImplementationTO> listImplementationsOfPlatform(int platId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<ImplementationTO>(0);
        }
        return em.createNamedQuery("Implementation.loadImplementationsOfPlatform", ImplementationTO.class).setParameter("platId", platId).getResultList();
    }

    @Override
    public List<ImplementationTO> listImplementationsOfValidator(int userId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<ImplementationTO>(0);
        }
        return em.createNamedQuery("Implementation.loadImplementationsOfValidator", ImplementationTO.class).setParameter("validatorId", userId).getResultList();
    }

    @Override
    public List<ImplementationTO> listImplementationsReadyForValidation() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications ready for validation
            return new ArrayList<ImplementationTO>(0);
        }
        return em.createNamedQuery("Implementation.loadImplementationsReadyForValidation", ImplementationTO.class).getResultList();
    }

    @Override
    public List<ImplementationTO> listImplementations() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return em.createNamedQuery("Implementation.loadPublishedImplementations", ImplementationTO.class).getResultList();
        }
        if(caller.isAdmin()){ //admin can read all
            return em.createNamedQuery("Implementation.loadAllImplementations", ImplementationTO.class).getResultList();
        }
        return em.createNamedQuery("Implementation.loadImplementations", ImplementationTO.class).setParameter("userId", caller.getId()).getResultList();
    }

    @Override
    public List<ImplementationTO> listImplementationsUserCanRead() {

        List<ImplementationTO> tempList = new ArrayList<ImplementationTO>();

        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            //return new ArrayList<ApplicationTO>(0);

            // the intended functionality is not to show non-validated imps for guest users,
            // but since there are only a few validated imps and we want to show some content
            // to attract users, temporaily non-validated imps can be seen as well
            // return em.createNamedQuery("Implementation.loadPublishedValidatedImplementations", ImplementationTO.class).getResultList();
            tempList = em.createNamedQuery("Implementation.loadImplementationsReadableByOthers", ImplementationTO.class).getResultList();
        }else{
            if(caller.isAdmin()){
                tempList = em.createNamedQuery("Implementation.loadAllImplementations", ImplementationTO.class).getResultList();
            }else{
                tempList = em.createNamedQuery("Implementation.loadImplementations", ImplementationTO.class).setParameter("userId", caller.getId()).getResultList();
            }
        }
        return tempList;
    }

    @Override
    public List<ImplementationTO> listImplementationsOfApplicationUserCanRead(int appId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            //return new ArrayList<ApplicationTO>(0);

            // the intended functionality is not to show non-validated imps for guest users,
            // but since there are only a few validated imps and we want to show some content
            // to attract users, temporaily non-validated imps can be seen as well
            //return em.createNamedQuery("Implementation.loadValidatedImplementationsOfApplication", ImplementationTO.class).setParameter("appId", appId).getResultList();
            return em.createNamedQuery("Implementation.loadImplementationsOfApplicationReadableByOthers", ImplementationTO.class).setParameter("appId", appId).getResultList();
        }else{
            if(caller.isAdmin()){
                return em.createNamedQuery("Implementation.loadImplementationsOfApplication", ImplementationTO.class).setParameter("appId", appId).getResultList();
            }else{
                return em.createNamedQuery("Implementation.loadImplementationsOfApplicationUserCanRead", ImplementationTO.class).setParameter("userId", caller.getId()).setParameter("appId", appId).getResultList();
            }
        }
    }


    @Override
    public List<ImplementationTO> listImplementations(String impAttrNameFilter, String impAttrValueFilter) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return em.createNamedQuery("Implementation.loadFilteredPublishedImplementations", ImplementationTO.class).setParameter("impAttrNameFilter","%"+impAttrNameFilter+"%").setParameter("impAttrValueFilter","%"+impAttrValueFilter+"%").getResultList();
        }
        return em.createNamedQuery("Implementation.loadFilteredImplementations", ImplementationTO.class).setParameter("impAttrNameFilter","%"+impAttrNameFilter+"%").setParameter("impAttrValueFilter","%"+impAttrValueFilter+"%").setParameter("userId", caller.getId()).getResultList();
    }


    @Override
    public List<CommentTO> listCommentsOfApplication(int appId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<CommentTO>(0);
        }
        return em.createNamedQuery("AppComment.loadCommentsOfApplication", CommentTO.class).setParameter("appId", appId).getResultList();
    }

    @Override
    public List<AttributeTO> listAttributesOfImplementation(int impId) {
        /*User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<AttributeTO>(0);
        }*/
        return em.createNamedQuery("ImpAttributes.loadAttributesOfImplementation", AttributeTO.class).setParameter("impId", impId).getResultList();
    }

    @Override
    public List<CommentTO> listCommentsOfImplementation(int impId) {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<CommentTO>(0);
        }
        return em.createNamedQuery("ImpComment.loadCommentsOfImplementation", CommentTO.class).setParameter("impd", impId).getResultList();
    }

    @Override
    public List<ImpFileTO> listFilesOfImplementation(int impId) {
        /*User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return fl;
        }*/
        List<ImpFileTO> fl = new LinkedList<ImpFileTO>();
        Implementation imp = em.find(Implementation.class, impId);
        if(imp == null){
            return fl;
        }
        File appDir = new File(this.repoPath + imp.getApplication().getId().toString());
        File impDir = new File(appDir.getAbsolutePath() + "/" + imp.getId().toString());
        String[] fs = impDir.list();
        if(fs != null && fs.length > 0){
            Arrays.sort(fs);
            for(String f: fs){
                fl.add(new ImpFileTO(imp.getApplication().getId(), imp.getId(), f));
            }
        }
        return fl;
    }

    @Override
    public List<AppFileTO> listFilesOfApplication(int appId) {
        List<AppFileTO> fl = new LinkedList<AppFileTO>();
        /*User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return fl;
        }*/
        Application app = em.find(Application.class, appId);
        if(app == null){
            return fl;
        }
        File appDir = new File(this.repoPath + app.getId().toString());
        File[] fs = appDir.listFiles();
        if(fs != null && fs.length > 0){
            Arrays.sort(fs);
            for(File f: fs){
                if(f.isFile()){
                    fl.add(new AppFileTO(app.getId(), f.getName()));
                }
            }
        }
        return fl;
    }

    @Override
    public List<GroupTO> listMyGroups() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list groups of a user
            return new ArrayList<GroupTO>(0);
        }
        return em.createNamedQuery("UserGroup.loadUserGroupsOfUser", GroupTO.class).setParameter("userId", caller.getId()).getResultList();
    }

    @Override
    public List<GroupTO> listMyOwnedGroups() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list owned groups of a user
            return new ArrayList<GroupTO>(0);
        }
        return em.createNamedQuery("UserGroup.loadOwnedUserGroupsOfUser", GroupTO.class).setParameter("leaderId", caller.getId()).getResultList();
    }

    @Override
    public List<ApplicationTO> listMyApplications() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<ApplicationTO>(0);
        }
        return em.createNamedQuery("Application.loadApplicationsOfUser", ApplicationTO.class).setParameter("ownerId", caller.getId()).getResultList();
    }

    @Override
    public List<ImplementationTO> listMyValidatedImplementations() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list implementations
            return new ArrayList<ImplementationTO>(0);
        }
        return em.createNamedQuery("Implementation.loadImplementationsOfValidator", ImplementationTO.class).setParameter("validatorId", caller.getId()).getResultList();
    }

    private User getCallerUser(){
        try{
            String callerName = context.getCallerPrincipal().getName(); //getCallerPrincipal never returns null
            if(callerName == null){
                return null;
            }
            return em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", callerName).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    @Override
    public String canUserReadUser(Integer callerId, Integer targetUserId) {
        return canUserReadUser(em.find(User.class, callerId), em.find(User.class, targetUserId));
    }

    private String canUserReadUser(User caller, User target){
        //admins can and users can read themselves
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(target == null){
            return "user does not exist";
        }
        if(!(caller.isAdmin() || caller.equals(target))){
            return "only administrators can view other users";
        }
        return null;
    }

    @Override
    public String canUserCreateUsers(Integer callerId) {
        return canUserCreateUsers(em.find(User.class, callerId));
    }

    private String canUserCreateUsers(User caller){
        //only admins can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(!caller.isAdmin()){
            return "only administrators can create users";
        }
        return null;
    }

    @Override
    public String canUserUpdateUser(Integer callerId, Integer targetUserId) {
        return canUserUpdateUser(em.find(User.class, callerId), em.find(User.class, targetUserId));
    }

    private String canUserUpdateUser(User caller, User target){
        //admins can and users can update themselves
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(target == null){
            return "user does not exist";
        }
        if(!(caller.isAdmin() || caller.equals(target))){
            return "only administrators can update other users";
        }
        return null;
    }

    @Override
    public String canUserChangeUserPassword(Integer callerId, Integer targetUserId) {
        return canUserChangeUserPassword(em.find(User.class, callerId), em.find(User.class, targetUserId));
    }

    private String canUserChangeUserPassword(User caller, User target) {
        //admins can and users can update themselves
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(target == null){
            return "user does not exist";
        }
        if(!(caller.isAdmin() || caller.equals(target))){
            return "only administrators can change the password of other users";
        }
        return null;
    }


    @Override
    public String canUserDeleteUser(Integer callerId, Integer targetUserId) {
        return canUserDeleteUser(em.find(User.class, callerId), em.find(User.class, targetUserId));
    }

    private String canUserDeleteUser(User caller, User target){
        //only admins can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(target == null){
            return "user does not exist";
        }
        if(caller.equals(target)){
            return "you can not delete yourself, deactivate your account instead";
        }
        if(!caller.isAdmin()){
            return "only administrators can delete users";
        }
        if(target.getApplications().size() > 0 || target.getLedGroups().size() > 0 || target.getGroups().size() > 0){
            return "the user can not be deleted right now, deactivate the account instead or remove all owned applications, owned groups and validated applications and remove the user from all groups";
        }
        return null;
    }

    @Override
    public String canUserReadGroup(Integer callerId, Integer groupId) {
        return canUserReadGroup(em.find(User.class, callerId), em.find(UserGroup.class, groupId));
    }

    private String canUserReadGroup(User caller, UserGroup group){
        //admins can and group members and group leaders can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(group == null){
            return "group does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(group.getLeader()) || group.getUsers().contains(caller)){
            return null;
        }
        return "only administrators, the group leader and group members can view groups";
    }

    @Override
    public String canUserCreateGroups(Integer callerId) {
        return canUserCreateGroups(em.find(User.class, callerId));
    }

    private String canUserCreateGroups(User caller){
        //everybody can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        return null;
    }

    @Override
    public String canUserChangeGroupLeader(Integer callerId, Integer groupId) {
        return canUserChangeGroupLeader(em.find(User.class, callerId), em.find(UserGroup.class, groupId));
    }

    private String canUserChangeGroupLeader(User caller, UserGroup group){
        //only admins and group leaders can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(group == null){
            return "group does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(group.getLeader())){
            return null;
        }
        return "only administrators and the current group leader can assign a new group leader";
    }

    @Override
    public String canUserAddUsersToGroup(Integer callerId, Integer groupId) {
        return canUserAddUsersToGroup(em.find(User.class, callerId), em.find(UserGroup.class, groupId));
    }

    private String canUserAddUsersToGroup(User caller, UserGroup group) {
        //only admins and group leaders can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(group == null){
            return "group does not exist";
        }
        if(!(caller.isAdmin() || caller.equals(group.getLeader()))){
            return "only administrators and the current group leader can add new members to a group";
        }
        return null;
    }

    @Override
    public String canUserRemoveUsersFromGroup(Integer callerId, Integer groupId) {
        return canUserRemoveUsersFromGroup(em.find(User.class, callerId), em.find(UserGroup.class, groupId));
    }

    private String canUserRemoveUsersFromGroup(User caller, UserGroup group){
        //only admins and group leaders can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(group == null){
            return "group does not exist";
        }
        if(!(caller.isAdmin() || caller.equals(group.getLeader()))){
            return "only administrators and the current group leader can remove members from a group";
        }
        return null;
    }

    private String canUserRemoveUserFromGroup(User caller, UserGroup group, User member){
        //only admins and group leaders can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(group == null){
            return "group does not exist";
        }
        if(member == null){
            return "user does not exist";
        }
        if(!(caller.isAdmin() || group.getLeader().equals(caller))){
            return "only administrators and the current group leader can remove members from a group";
        }
//        if(member.equals(group.getLeader())){
//            return "the current leader can not be removed from the group";
//        }
        if(!group.getUsers().contains(member)){
            return "the user is not a member of the group";
        }
        return null;
    }

    @Override
    public String canUserDeleteGroup(Integer callerId, Integer groupId) {
        return canUserDeleteGroup(em.find(User.class, callerId), em.find(UserGroup.class, groupId));
    }

    private String canUserDeleteGroup(User caller, UserGroup group){
        //only admins and group leaders can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(group == null){
            return "group does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(group.getApplications().size() > 0){
            return "the group can not be deleted right now, remove all its applications and try again";
        }
        if(caller.equals(group.getLeader())){
            return null;
        }
        return "only administrators and the current group leader can delete a group";
    }

    @Override
    public String canUserReadPlatform(Integer callerId, Integer platId) {
        return canUserReadPlatform(em.find(User.class, callerId), em.find(Platform.class, platId));
    }

    private String canUserReadPlatform(User caller, Platform plat) {
        //everybody can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(plat == null){
            return "platform does not exist";
        }
        return null;
    }

    @Override
    public String canUserUpdatePlatform(Integer callerId, Integer platId) {
        return canUserUpdatePlatform(em.find(User.class, callerId), em.find(Platform.class, platId));
    }

    private String canUserUpdatePlatform(User caller, Platform plat) {
        //only admins can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(plat == null){
            return "platform does not exist";
        }
        if(!caller.isAdmin()){
            return "only administrators can update platforms";
        }
        return null;
    }

    @Override
    public String canUserDeletePlatform(Integer callerId, Integer platId) {
        return canUserDeletePlatform(em.find(User.class, callerId), em.find(Platform.class, platId));
    }

    private String canUserDeletePlatform(User caller, Platform plat) {
        //only admins can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(plat == null){
            return "platform does not exist";
        }
        if(!caller.isAdmin()){
            return "only administrators can update platforms";
        }
        if(plat.getImplementations().size() > 0){
            return "the platform can not be deleted right now, remove all its implementations and try again";
        }
        return null;
    }

    @Override
    public Boolean canUserReadDownloadApplication(Integer callerId, Integer appId) {
        Application app = em.find(Application.class, appId);
        if(app == null) return null;

        User user = em.find(User.class, callerId);

        return (isGroupMember(app, user) && app.getGroupRead() && app.getGroupDownload())
                ||
                canOthersReadDownloadApplication(app);
    }

    private boolean canOthersReadDownloadApplication(Application app) {
        return app.getOthersRead() && app.getOthersDownload();
    }

    @Override
    public String canUserReadApplication(Integer callerId, Integer appId) {
        return canUserReadApplication(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserReadApplication(User caller, Application app){
        //admins can and validators can and owners can and group members can if group read/download/modify flag is on and others can if others read/download flag is on
        //public is covered by a separate method
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(app == null){
            return "application does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(app.getOwner()) || ((app.getGroupRead() || app.getGroupDownload() || app.getGroupModify()) && isGroupMember(app, caller)) || app.getOthersRead() || app.getOthersDownload()){
            return null;
        }
        return "you are not allowed to view this application";
    }

    @Override
    public String canPublicReadApplication(Integer appId) {
        return canPublicReadApplication(em.find(Application.class, appId));
    }

    private String canPublicReadApplication(Application app){
        //public can if the published flag is on
        if(app == null){
            return "application does not exist";
        }
        if(!(app.getPublished())){
            return "you are not allowed to view this application, its implementations, its attributes or its files";
        }
        return null;
    }

    @Override
    public String canPublicReadImplementation(Integer impId) {
        return canPublicReadImplementation(em.find(Implementation.class, impId));
    }

    private String canPublicReadImplementation(Implementation imp){
        //public can if the published flag is on
        String canReadApplicationError = canPublicReadApplication(imp.getApplication());
        if(canReadApplicationError!=null){
            return canReadApplicationError;
        }
        if(imp.getStatus()!= ImplementationStatus.PUBLIC){
            return "You are not allowed to view private implementations.";
        }
        return null;
    }

    @Override
    public String canUserCreateApplications(Integer callerId) {
        return canUserCreateApplications(em.find(User.class, callerId));
    }

    private String canUserCreateApplications(User caller) {
        //in general everybody can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        return null;
    }

    private String canUserCreateApplication(User caller, UserGroup group) {
        //only leaders of the group can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(group == null){
            return "group does not exist";
        }
        if(!(caller.isAdmin() || (group.getUsers().contains(caller)))){
            return "only group members can assign a new applications to a group";
        }
        return null;
    }


    @Override
    public String canUserUpdateAppAccess(Integer callerId, Integer appId) {
        return canUserUpdateAppAccess(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserUpdateAppAccess(User caller, Application app) {
        //admins and owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(!(caller.isAdmin()|| caller.equals(app.getOwner()))){
            return "only administrators, validators and the current owner can change access control";
        }
        return null;
    }

    private String canUserUpdateAppAccess(User caller, Application app, UserGroup g) {
        //admins and owner can, owner must choose a group they lead
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(g == null){
            return "group does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(!(g.getUsers().contains(caller))){
            return "you can assign applications only to groups you are a member of";
        }
        return null;
    }

    @Override
    public String canUserPublishApp(Integer callerId, Integer appId) {
        return canUserPublishApp(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserPublishApp(User caller, Application app) {
        //admins and owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(!(caller.isAdmin())){
            return "only administrators can publish applications";
        }
        return null;
    }


    @Override
    public String canUserUpdateAppDetails(Integer callerId, Integer appId) {
        return canUserModifyApplication(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    @Override
    public String canUserChangeAppOwner(Integer callerId, Integer appId) {
        return canUserChangeAppOwner(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserChangeAppOwner(User caller, Application app){
        //admins and owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(!(caller.isAdmin() || caller.equals(app.getOwner()))){
            return "only administrators and the current owner can assign a new owner";
        }
        return null;
    }

    private String canUserChangeAppOwner(User caller, Application app, User newOwner){
        //admins and owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(!(caller.equals(app.getOwner()))){
            return "only administrators and the current owner can assign a new owner";
        }
        if(isGroupMember(app, newOwner)){
            return "the new owner should be a member of the group of the application";
        }
        return null;
    }

    @Override
    public String canUserDeleteApplication(Integer callerId, Integer appId) {
        return canUserDeleteApplication(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserDeleteApplication(User caller, Application app){
        //admins and owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(app.getOwner())){
            return null;
        }
        return "only administrators and the current owner can delete an application";
    }

    @Override
    public String canUserReadAppAttr(Integer callerId, Integer attrId) {
        return canUserReadAppAttr(em.find(User.class, callerId), em.find(AppAttribute.class, attrId));
    }

    private String canUserReadAppAttr(User caller, AppAttribute attr){
        if(attr == null){
            return "the attribute does not exist";
        }
        return canUserReadApplication(caller, attr.getApplication());
    }

    @Override
    public String canUserCreateAppAttrs(Integer callerId, Integer appId) {
        return canUserModifyApplication(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserModifyApplication(User caller, Application app){
        //admins can and owners can and group members can if group modify flag is on
        //public is covered by a separate method
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(app == null){
            return "application does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(app.getOwner())){
            return null;
        }
        if(app.getGroupModify() && isGroupMember(app, caller)){
            return null;
        }
        return "you are not allowed to modify this application, its implementation or its attributes";
    }

    @Override
    public String canUserUpdateAppAttr(Integer callerId, Integer attrId) {
        return canUserModifyAppAttr(em.find(User.class, callerId), em.find(AppAttribute.class, attrId));
    }

    private String canUserModifyAppAttr(User caller, AppAttribute attr){
        if(attr == null){
            return "the attribute does not exist";
        }
        return canUserModifyApplication(caller, attr.getApplication());
    }

    @Override
    public String canUserDeleteAppAttr(Integer callerId, Integer attrId) {
        return canUserModifyAppAttr(em.find(User.class, callerId), em.find(AppAttribute.class, attrId));
    }

    @Override
    public String canUserReadImp(Integer callerId, Integer impId) {
        return canUserReadImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserReadImp(User caller, Implementation imp){
        if(imp == null){
            return "the implementation does not exist";
        }
        return canUserReadApplication(caller, imp.getApplication());
    }

    @Override
    public String canUserCreateImps(Integer callerId, Integer appId) {
        return canUserCreateImps(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserCreateImps(User caller, Application app){
        //admins can and owners can and group members can if group modify flag is on
        //public is covered by a separate method
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(app == null){
            return "application does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(app.getOwner())){
            return null;
        }
        if(app.getGroupModify() && isGroupMember(app, caller)){
            return null;
        }
        return "you are not allowed to modify this application, its implementation or its attributes";
    }

    @Override
    public String canUserUpdateImp(Integer callerId, Integer impId) {
        return canUserUpdateImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserUpdateImp(User caller, Implementation imp) {
        //admins can and owners can and group members can if group modify flag is on
        //public is covered by a separate method
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(imp == null){
            return "implementation does not exist";
        }
        //admins can ignore the implementation lifecycle
        if(caller.isAdmin()){
            return null;
        }
        //Implementations are not updateable if they are READY, VALIDATED or OLD
        if(!isImpUpdateable(imp)){
            return "the implementation can not be modified, deprecate it and try again";
        }
        if(caller.equals(imp.getApplication().getOwner())){
            return null;
        }
        if(imp.getApplication().getGroupModify() && isGroupMember(imp.getApplication(), caller)){
            return null;
        }
        return "you are not allowed to modify this implementation or its attributes";
    }

    private boolean isImpUpdateable(Implementation imp){
        //READY, VALIDATED and OLD are NOT updateable
//        if(imp == null){
//            return false;
//        }
//        if(ImplementationStatus.READY.equals(imp.getStatus()) || ImplementationStatus.VALIDATED.equals(imp.getStatus()) || ImplementationStatus.OLD.equals(imp.getStatus())){
//            return false;
//        }
        return true;
    }

    private boolean isImpRunnable(Implementation imp){
        //READY, VALIDATED and OLD and runnable
        if(imp == null){
            return false;
        }
        if(ImplementationStatus.READY.equals(imp.getStatus()) || ImplementationStatus.PUBLIC.equals(imp.getStatus()) || ImplementationStatus.OLD.equals(imp.getStatus())){
            return true;
        }
        return false;
    }

    @Override
    public String canUserSubmitImp(Integer callerId, Integer impId) {
        return canUserSubmitImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserSubmitImp(User caller, Implementation imp) {
        //admins can and the application owner can unless it's READY or VALIDATED or OLD
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(imp == null){
            return "implementation does not exist";
        }
        //admins can ignore the implementation lifecycle
        if(caller.isAdmin()){
            return null;
        }
        if(!isImpUpdateable(imp)){
            return "implementation has already been validated or is waiting for validation";
        }
        if(!caller.equals(imp.getApplication().getOwner())){
            return "only administrators and application owners can submit implementations for validation";
        }
        return null;
    }

    @Override
    public String canUserValidateImp(Integer callerId, Integer impId) {
        return canUserValidateImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserValidateImp(User caller, Implementation imp) {
        return this.canUserUpdateImp(caller, imp);
//        //admins can, validators can if it is READY unless they are the owners of the app
//        if(caller == null){
//            return "your account has been deleted";
//        }
//        if(!caller.isActive()){
//            return "your account has been deactivated";
//        }
//        if(imp == null){
//            return "implementation does not exist";
//        }
//        //admins can ignore the implementation lifecycle
//        if(caller.isAdmin()){
//            return null;
//        }
//        if(!ImplementationStatus.READY.equals(imp.getStatus())){
//            return "implementation is not ready for validation";
//        }
//        if(caller.isValidator() && !caller.equals(imp.getApplication().getOwner())){
//            return null;
//        }
//        return "only administrators and validators can approve or deny validation unless they are the application owners";
    }

    @Override
    public String canUserFailImp(Integer callerId, Integer impId) {
        return canUserValidateImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    @Override
    public String canUserMarkImpOld(Integer callerId, Integer impId) {
        return canUserMarkImpOld(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserMarkImpOld(User caller, Implementation imp) {
        //admins can and the owner can if it is currently VALIDATED
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(imp == null){
            return "implementation does not exist";
        }
        //admins can ignore the implementation lifecycle
        if(caller.isAdmin()){
            return null;
        }
        if(!ImplementationStatus.PUBLIC.equals(imp.getStatus())){
            return "only validated implementations can be marked 'old'";
        }
        if(!(caller.equals(imp.getApplication().getOwner()))){
            return "only administrators and the application owner can mark an implementation old";
        }
        return null;
    }

    @Override
    public String canUserMarkImpDeprecated(Integer callerId, Integer impId) {
        return canUserMarkImpDeprecated(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserMarkImpDeprecated(User caller, Implementation imp) {
        //admins can and the owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(imp == null){
            return "implementation does not exist";
        }
        //admins can ignore the implementation lifecycle
        if(caller.isAdmin()){
            return null;
        }
        if(!(caller.equals(imp.getApplication().getOwner()))){
            return "only administrators and the application owner can mark an implementation 'old'";
        }
        return null;
    }

    @Override
    public String canUserMarkImpCompromised(Integer callerId, Integer impId) {
        return canUserMarkImpCompromised(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserMarkImpCompromised(User caller, Implementation imp) {
        //admins can and the owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(imp == null){
            return "implementation does not exist";
        }
        //admins can ignore the implementation lifecycle
        if(caller.isAdmin()){
            return null;
        }
        if(!(caller.equals(imp.getApplication().getOwner()))){
            return "only administrators and the application owner can mark an implementation 'compromised'";
        }
        return null;
    }

    @Override
    public String canUserDeleteImp(Integer callerId, Integer impId) {
        return canUserDeleteImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserDeleteImp(User caller, Implementation imp){
        //admins can and the owner can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(imp == null){
            return "implementation does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(imp.getApplication().getOwner())){
            return null;
        }
        return "only administrators and the application owner can delete an implementation";
    }

    @Override
    public String canUserReadImpAttr(Integer callerId, Integer attrId) {
        return canUserReadImpAttr(em.find(User.class, callerId), em.find(ImpAttribute.class, attrId));
    }

    private String canUserReadImpAttr(User caller, ImpAttribute attr){
        //same as can read implementation
        if(attr == null){
            return "implementation attribute does not exist";
        }
        return canUserReadApplication(caller, attr.getImplementation().getApplication());
    }

    @Override
    public String canUserCreateImpAttrs(Integer callerId, Integer impId) {
        return canUserUpdateImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    @Override
    public String canUserUpdateImpAttr(Integer callerId, Integer attrId) {
        return canUserUpdateImpAttr(em.find(User.class, callerId), em.find(ImpAttribute.class, attrId));
    }

    private String canUserUpdateImpAttr(User caller, ImpAttribute attr) {
        //same as can modify implementation
        if(attr == null){
            return "implementation attribute does not exist";
        }
        return canUserUpdateImp(caller, attr.getImplementation());
    }

    @Override
    public String canUserDeleteImpAttr(Integer callerId, Integer attrId) {
        return canUserUpdateImpAttr(em.find(User.class, callerId), em.find(ImpAttribute.class, attrId));
    }

    @Override
    public String canUserUploadImpFile(Integer callerId, Integer impId) {
        return canUserUpdateImp(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    @Override
    public String canUserDownloadImpFile(Integer callerId, Integer impId) {
        return canUserDownloadImpFile(em.find(User.class, callerId), em.find(Implementation.class, impId));
    }

    private String canUserDownloadImpFile(User caller, Implementation imp){
        //admins can and validators can and app owner can and group can if group download is on and others can if others download is on
        //public is covered by a separate method
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(imp == null){
            return "implementation does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(imp.getApplication().getOwner()) || ((imp.getApplication().getGroupDownload() || imp.getApplication().getGroupModify()) && isGroupMember(imp.getApplication(), caller)) || imp.getApplication().getOthersDownload()){
            return null;
        }
        return "you are not allowed to download files of this application";
    }


    @Override
    public String canUserDeleteImpFiles(Integer callerId, Integer impId) {
        return canUserModifyApplication(em.find(User.class, callerId), em.find(Implementation.class, impId).getApplication());
    }

    @Override
    public String canUserUploadAppFile(Integer callerId, Integer appId) {
        return canUserModifyApplication(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    @Override
    public String canUserDownloadAppFile(Integer callerId, Integer appId) {
        return canUserDownloadAppFile(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    private String canUserDownloadAppFile(User caller, Application app){
        //admins can and validators can and app owner can and group can if group download is on and others can if others download is on
        //public is covered by a separate method

        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(app == null){
            return "application does not exist";
        }
        if(caller.isAdmin()){
            return null;
        }
        if(caller.equals(app.getOwner()) || ((app.getGroupDownload() || app.getGroupModify()) && isGroupMember(app, caller)) || app.getOthersDownload()){
            return null;
        }
        return "you are not allowed to download files of this application";
    }

    @Override
    public String canUserDeleteAppFiles(Integer callerId, Integer appId) {
        return canUserModifyApplication(em.find(User.class, callerId), em.find(Application.class, appId));
    }

    @Override
    public String canPublicDownloadImpFile(Integer impId) {
        return canPublicDownloadImpFile(em.find(Implementation.class, impId));
    }

    private String canPublicDownloadImpFile(Implementation imp){
        //same as app read
        if(imp == null){
            return "implementation not found";
        }
        if(imp.getApplication() == null){
            return "application not found";
        }

        // the intended functionality is not to show non-validated imps for guest users,
        // but since there are only a few validated imps and we want to show some content
        // to attract users, temporaily non-validated imps can be seen and downloaded as
        // well
        //if(imp.getApplication().getPublished() && imp.getApplication().getOthersDownload()){

        if(imp.getApplication().getOthersDownload()){
            return null;
        }
        return "you are not allowed to download files of this implementation";
        //return canPublicReadApplication(imp.getApplication());
    }

    @Override
    public String canPublicDownloadAppFile(Integer appId) {
        return canPublicDownloadAppFile(em.find(Application.class, appId));
    }

    private String canPublicDownloadAppFile(Application app){
        //same as app read
        if(app == null){
            return "application not found";
        }

        if(app.getOthersDownload()){
            return null;
        }
        return "you are not allowed to download files of this application";
        //return canPublicReadApplication(app);
    }

    @Override
    public String canUserCreatePlatforms(Integer callerId) {
        return canUserCreatePlatforms(em.find(User.class, callerId));
    }

    private String canUserCreatePlatforms(User caller){
        //only admins can
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(!caller.isAdmin()){
            return "only administrators can create platforms";
        }
        return null;
    }

    private void deleteAllImpFiles(Implementation i) throws FileOperationFailedException {
        File appDir = new File(this.repoPath + i.getApplication().getId().toString());
        if(!appDir.exists()){
            return;
        }
        File impDir = new File(appDir.getAbsolutePath() + "/" + i.getId().toString());
        if(!impDir.exists()){
            return;
        }
        deleteRecursively(impDir);
        deleteDirectory(impDir);
    }

    private void deleteAllAppFiles(Application a) throws FileOperationFailedException {
        File appDir = new File(this.repoPath + a.getId().toString());
        if(!appDir.exists()){
            return;
        }
        deleteRecursively(appDir);
        deleteDirectory(appDir);
    }

    private void checkFileExists(File f, String pathName, InputStream source) throws FileOperationFailedException, IOException {
        if(f.exists()){
            throw new FileOperationFailedException("the file already exists: '"+pathName+"'");
        } else { //file does not exist
            if(!f.createNewFile()) { //could not create file
                logger.log(Level.SEVERE, "internal error when uploading file: failed to create file"); //log error
                source.close();
                throw new FileOperationFailedException("could not upload file (server error)");
            }
        }
    }

    private void copyBytesOfUploadFile(BufferedInputStream bis, BufferedOutputStream bos) throws IOException {
        byte[] buffer = new byte[102400]; //TODO: should not throw buffer away like this. implement a thread safe buffer pool instead. use singleton pattern
        while (true) {
            int amountRead = bis.read(buffer);
            if (amountRead == -1) {
               break;
            }
            bos.write(buffer, 0, amountRead);
        }
    }

    private void closeStreams(BufferedInputStream bis, InputStream source, BufferedOutputStream bos) {
        try {
            if(bis != null){
                bis.close(); //closes wrapped streams ie source.close() is not necessary
            }else{ //didn't get as far as creating bis, need to call source.close()
                if(source != null){
                    source.close();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to close upload stream reader : {0}", e.getMessage()); //log error
        }
        try {
            if(bos != null){
                bos.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to close output file writer: {0}", e.getMessage()); //log error
        }
    }

    private void validatePath(String pathName) throws ValidationFailedException {
        //TODO: validate pathName
        if(pathName == null){
            throw new ValidationFailedException("no filename specified"); //TODO: elaborate error message
        }
        if(!isValidPath(pathName)){
            throw new ValidationFailedException("invalid filename: '"+pathName+"'"); //TODO: elaborate error message
        }
    }

    private void validateAppDir(File appDir, InputStream source) throws IOException, FileOperationFailedException {
        if(!appDir.exists()){
            if(!appDir.mkdir()){
                source.close();
                throw new FileOperationFailedException("could not upload file (server error)");
            }
        }
    }

    private void validateImpDir(File impDir, InputStream source) throws FileOperationFailedException, IOException {
        if(!impDir.exists()){
            if(!impDir.mkdirs()){
                logger.log(Level.SEVERE, "internal error when uploading file: could not create implementation directory"); //log error
                source.close(); //already caught
                throw new FileOperationFailedException("could not upload file (server error)");
            }
        }
    }

    private boolean isValidPath(String pathName) {
        return pathName.matches("[a-zA-Z0-9\\._][\\.a-zA-Z0-9_-]*");
    }

    private void deleteFile(File f) throws FileOperationFailedException {
        if(!f.delete()){
            throw new FileOperationFailedException("could not delete file: '" + f.getName() + "'");
        }
    }

    private void deleteDirectory(File impDir) throws FileOperationFailedException {
        if(!impDir.delete()){
            throw new FileOperationFailedException("could not delete directory");
        }
    }

    private void deleteRecursively(File impDir) throws FileOperationFailedException {

        for(File d: impDir.listFiles()){
            if(d.isDirectory()){
                for(File f: d.listFiles()){
                    deleteFile(f);
                }
                deleteDirectory(d);
            }
            if(d.isFile()){
                deleteFile(d);
            }
        }
    }

    @Override
    public PlatformTO getPlatform(String name, String version) {
        Platform platform = em.createNamedQuery("Platform.findByNameAndVersion", Platform.class).setParameter("name", name).setParameter("version", version).getSingleResult();
        if(platform!=null){
            return new PlatformTO(platform);
        }
        return null;
    }

    @Override
    public PlatformTO getPlatform(int id) {
        Platform platform = em.createNamedQuery("Platform.findById", Platform.class).setParameter("platformId", id).getSingleResult();
        if(platform!=null){
            return new PlatformTO(platform);
        }
        return null;
    }

    @Override
    public List<PlatformTO> getPlatforms() {
        return em.createNamedQuery("Platform.loadAllPlatforms", PlatformTO.class).getResultList();
    }

    private boolean isGroupMember(Application app, User caller) {
        return app.getGroup().getUsers().contains(caller);
    }

    /*
     * ------ Workflow Engine Management Functions ------------------------------
     *          AUTHOR: EMK
     * ------ Workflow Engine Functions -----------------------------------------
     */

    //returns the list of all workflow engines
    @Override
    public List<Platform> listWorkflowEngines() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list applications
            return new ArrayList<Platform>(0);
        }
        return em.createNamedQuery("Platform.findAll",Platform.class).getResultList();
    }

    @Override
    public List<String> listWorkflowEnginesNoDupes(){
        List<String> tempList = new ArrayList<String>();
        tempList = em.createNamedQuery("Platform.findDistinctNames", String.class).getResultList();
        return tempList;
    }

    @Override
    public Platform getWEById(int idWE){
        return em.find(Platform.class, idWE);
    }

    //these following functions defines if a user is allowed to access workflow engine management
    @Override
    public String canUserReadWorkflowEngine(int callerId, int weId) {
        return canUserReadWorkflowEngine(em.find(User.class, callerId), em.find(Platform.class, weId));
    }

    private String canUserReadWorkflowEngine(User caller, Platform we)
    {
        //admins can and validators can and owners can and group members can if group read/download/modify flag is on and others can if others read/download flag is on
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(we == null){
            return "This workflow engine does not exist";
        }
        if(caller.isAdmin() || caller.isActive()){
            return null;
        }
        return "you are not allowed to view this workflow engine";
    }

    //these following functions defines if a anonymous user is allowed to access workflow engine management. basically not
    @Override
    public String canPublicReadWorkflowEngine(int weId) {
        return canPublicReadWorkflowEngine(em.find(Platform.class, weId));
    }

    private String canPublicReadWorkflowEngine(Platform we) {
        if(we == null)
            return "This workflow engine does not exist";
        return null;
    }

    /*
     *Used in the creation of Workflow Engines by administrator.
     */
    @Override
    public String canUserCreateWorkflowEngines(int callerId){
        return canUserCreateWorkflowEngines(em.find(User.class, callerId));
    }

    private String canUserCreateWorkflowEngines(User caller) {
        //only the admin or a WE manager is supposed to be allowed to perform this
        if(caller == null){
            return "your account has been deleted";
        }//need to update the correct role
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        return null;
    }

    @Override
    public Platform createWorkflowEngine(String nameWE, String version, String desc)
        throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, WEBuildingException
    {
        User caller = getCallerUser();
        String err = canUserCreateWorkflowEngines(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //validate app name
        if(!nameWE.matches("[\\-\\w\\.]+{3,50}")){
            throw new ValidationFailedException("Workflow engine names can only contain alphanumeric characters and must be between 3 and 50 charaters long");
        }
        for (Platform temp : em.createNamedQuery("Platform.findByName", Platform.class).setParameter("name", nameWE).getResultList()){
            if(temp.getVersion().equals(version))
                throw new EntityAlreadyExistsException("Workflow engine '"+nameWE+"' with version '"+version+"' already exists");
        }

        Platform _WE;

        _WE = new Platform(nameWE, version, desc);
        em.persist(_WE);
        em.flush();

        return _WE;
    }

    @Override
    public void deleteWE(Platform _we)
            throws EntityNotFoundException, AuthorizationException, NotSafeToDeleteException
    {
        /*
         * emk :-
         * Assume that if user can create WE, then they can delete them also!
         */
        User caller = getCallerUser();
        String err = canUserCreateWorkflowEngines(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }

        //delete children first!

        //weimps
        //handled by cascade type

        for(WEImplementation temp : _we.getWeImplementationCollection()){
            deleteWEImplementation(temp.getIdWEImp());
        }

        //wefiles
        for(WEUploadedFile temp : _we.getUploadedFileCollection()){

            //possibly delete all files at once?
            try{
                deleteWEFiles(temp);
            }catch (Exception e){

            }

            //em.remove(em.find(WEUploadedFile.class, temp.getIdWEFile()));
        }

        em.flush();

        //ensure that the uploaded files and weimps are not merged on deletion!
        _we.setUploadedFileCollection(new ArrayList<WEUploadedFile>());
        _we.setWeImplementationCollection(new ArrayList<WEImplementation>());

        em.remove(em.merge(_we));
        em.flush();

    }


    /*
     * Workflow Engine Implementation functions ---------------------------------------
     */

    //lists WE implementations with respect to the corresponding workflow engine
    @Override
    public Collection<WEImplementation> listWEImplementation(Platform _we)
    {
        User caller = getListCaller();
        if(caller == null || !caller.isActive())     //only active users may list applications
            return new ArrayList<WEImplementation>(0);

        //return em.createNamedQuery("WEImplementation.listAccordingWE", WEImplementation.class).setParameter("selectedWE", weId).getResultList();
        return em.createNamedQuery("Platform.findById", Platform.class).setParameter("platformId", _we.getId()).getSingleResult().getWeImplementationCollection();
    }

    //these following functions defines if a anonymous user is allowed to access workflow engine management. basically not, but maybe later, a user will be allowed to see (and only see) WE implementations
    @Override
    public String canUserReadWEImplementation(int userId, int weImpId){
        return canUserReadWEImplementation(em.find(User.class, userId), em.find(WEImplementation.class, weImpId));
    }

    private String canUserReadWEImplementation(User caller, WEImplementation we){
        if(caller == null){
            return "your account has been deleted";
        }
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        if(we == null){
            return "This workflow engine implementation does not exist";
        }
        if(caller.isAdmin() || /*-test-*/caller.isActive()/*-/test-*/){
            return null;
        }
        /*if(caller.equals(we.getOwner()) || ((we.getGroupRead() || app.getGroupDownload() || app.getGroupModify()) && app.getGroup().getUsers().contains(caller)) || app.getOthersRead() || app.getOthersDownload()){
            return null;
        }*/
        return "you are not allowed to view this workflow engine implementation";
    }

    //these following functions defines if a anonymous user is allowed to access workflow engine management. basically not, but maybe later, a user will be allowed to see (and only see) WE implementations
    @Override
    public String canPublicReadWEImplementation(int weImpId){
        return canPublicReadWEImplementation(em.find(WEImplementation.class, weImpId));
    }

    private String canPublicReadWEImplementation(WEImplementation weImp) {
        if(weImp == null)
            return "This workflow engine implementation is not for public viewing";
        return null;
    }

    //these following functions defines if a logged user is allowed to access workflow engine management.
    @Override
    public String canUserCreateWEImplementations(int callerId){
        return canUserCreateWEImplementations(em.find(User.class, callerId));
    }

    @Override
    public String canUserModifyWEImplementations(int callerId, WEImplementation weimp){
        return canUserModifyWEImplementations(em.find(User.class, callerId), weimp);
    }

    @Override
    public String canUserModifyBEInst(int callerId, BeInstance beinst){
        return canUserModifyBEInst(em.find(User.class, callerId), beinst);
    }

    private String canUserModifyBEInst(User caller, BeInstance beinst) {
        if(caller == null){
            return "your account has been deleted";
        }else if(!caller.isActive()){
            return "your account has been deactivated";
        }else if(caller.isAdmin() || (caller.isWEDev() && this.isBEInstOwner(caller, beinst))){
            return null;
        }else{
            return "You are not authorized to do this - it has been reported";
        }
    }

    @Override
    public boolean isBEInstOwner(int callerId, BeInstance beinst) {
        return this.isBEInstOwner(em.find(User.class, callerId), beinst);
    }

    public boolean isBEInstOwner(User caller, BeInstance beinst) {
        return caller.equals(beinst.getWEDev());
    }

    private String canUserCreateWEImplementations(User caller) {
        //only the admin or a WE manager is supposed to be allowed to perform this
        if(caller == null){
            return "your account has been deleted";
        }else if(!caller.isActive()){
            return "your account has been deactivated";
        }else if(caller.isAdmin() || caller.isWEDev()){
            return null;
        }else{
            return "You are not authorized to do this - it has been reported";
        }
    }

    private String canUserModifyWEImplementations(User caller, WEImplementation weimp) {
        if(caller == null){
            return "your account has been deleted";
        }else if(!caller.isActive()){
            return "your account has been deactivated";
        }else if(caller.isAdmin() || (caller.isWEDev() && this.isWEImpOwner(caller, weimp))){
            return null;
        }else{
            return "You are not authorized to do this - it has been reported";
        }
    }

    @Override
    public boolean isWEImpOwner(int callerId, WEImplementation weimp) {
        return this.isWEImpOwner(em.find(User.class, callerId), weimp);
    }

    public boolean isWEImpOwner(User caller, WEImplementation weimp) {
        return caller.equals(weimp.getWEDev());
    }

    @Override
    public List<WEImplementation> getWEImpByWE(Platform _we)
    {
        return em.createNamedQuery("WEImplementation.listAccordingWE", WEImplementation.class).setParameter("idWE", _we).getResultList();
    }

    @Override
    public WEImplementation createWEImp(String nameWEImp, String descriptionWEImp, boolean preDeployed, String prefixData, Platform _we, BeInstance _be, WEUploadedFile _zip, String _shell, WEUploadedFile _shellFile)
            throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, WEBuildingException
    {

        User caller = this.getCallerUser();
        /*
         * If this works then
         * TODO: More validation and checking EVERYWHERE!
         */

        String err = canUserCreateWEImplementations(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }

        if(!nameWEImp.matches("[\\-\\w\\.]+{3,50}")){
            throw new ValidationFailedException("Workflow engine implementation names can only contain alphanumeric characters and must be between 3 and 50 charaters long");
        }

        Query q = em.createNamedQuery("WEImplementation.findByNameWEImpAndIdWE", WEImplementation.class);
        q.setParameter("nameWEImp", nameWEImp);
        q.setParameter("idWE", _we);

        if(q.getResultList().size() > 0){
            throw new EntityAlreadyExistsException("Workflow engine implementation'"+nameWEImp+"' already exists");
        }
        if(preDeployed){
            //TODO: needs more validation here. should it end in .sh or .bat?
            if(!_shell.startsWith("/")){
                throw new ValidationFailedException("Pathname should be absolute");
            }
        }

        if(preDeployed == false && _zip == null){
            throw new ValidationFailedException("If the Engine is not predeployed then please select Engine Data for on the fly deployment");
        }

        if(preDeployed == false && _shellFile == null){
            throw new ValidationFailedException("If the Engine is not predeployed then please select an Engine Executable for on the fly deployment");
        }

        WEImplementation weImp = null;
        try {
                if(!preDeployed){
                    weImp = WEImplementation.WEImplementationFactory(nameWEImp, descriptionWEImp, preDeployed, prefixData, _we, _be, _zip, _shellFile, caller);
                }else{
                    weImp = WEImplementation.WEImplementationFactory(nameWEImp, descriptionWEImp, preDeployed, prefixData, _we, _be, _shell, caller);
                }

                em.persist(weImp);
                em.flush();


                if(weImp.getIdWE().getWeImplementationCollection() == null){
                        weImp.getIdWE().setWeImplementationCollection(new ArrayList<WEImplementation>());
                }

                weImp.getIdWE().getWeImplementationCollection().add(weImp);

                if(weImp.getIdBackendInst().getWeImplementationCollection() == null){
                    weImp.getIdBackendInst().setWeImplementationCollection(new ArrayList<WEImplementation>());
                }

                weImp.getIdBackendInst().getWeImplementationCollection().add(weImp);

                if(_zip != null){
                    if(weImp.getZipWEFileId().getWeImplementationCollection() == null){
                        weImp.getZipWEFileId().setWeImplementationCollection(new ArrayList<WEImplementation>());
                    }
                    weImp.getZipWEFileId().getWeImplementationCollection().add(weImp);
                }

                if(_shellFile != null){
                    if(weImp.getShellWEFileId().getWeImplementationCollection() == null){
                        weImp.getShellWEFileId().setWeImplementationCollection(new ArrayList<WEImplementation>());
                    }
                    weImp.getShellWEFileId().getWeImplementationCollection().add(weImp);
                }

                if(caller != null){
                    if(weImp.getWEDev().getWEImps() == null){
                        weImp.getWEDev().setWEImps(new ArrayList<WEImplementation>());
                    }
                    weImp.getWEDev().getWEImps().add(weImp);
                }

                /*
                 * The order in which the entities are persisted does matter
                 * Persist first, merge later.
                 */

                em.merge(weImp.getIdBackendInst());
                em.merge(weImp.getIdWE());
                em.merge(weImp.getWEDev());

                //if WEI is pre deployed
                if(_zip != null){
                    em.merge(weImp.getZipWEFileId());
                }

                if(_shellFile != null){
                    em.merge(weImp.getShellWEFileId());
                }

                em.merge(weImp);
                em.flush();
                logger.log(Level.INFO, "Created Workflow Engine Implementation {0}", weImp.getNameWEImp());
        } catch (WEBuildingException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return weImp;
    }

    @Override
    public void updateWEImp(WEImplementation _WEImp)
            throws ValidationFailedException, AuthorizationException, WEBuildingException
    {
        User caller = getCallerUser();
        String nameWEImp = _WEImp.getNameWEImp();
        String err = canUserCreateWEImplementations(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //validate app name ***change this to include -/_
        if(!nameWEImp.matches("[\\-\\w\\.]+{3,50}")){
            throw new ValidationFailedException("Workflow engine implementation names can only contain alphanumeric characters and must be between 3 and 50 charaters long");
        }
        if(!_WEImp.getPreDeployed() && (_WEImp.getShellWEFileId() == null || _WEImp.getZipWEFileId() == null))
            throw new ValidationFailedException("Unrecognized Executable or Data file for On-The-Fly deployment. Try Again");
        if ( nameWEImp.length() < 3 || nameWEImp.length() > 50)
	    throw new WEBuildingException( "name", nameWEImp );
        if ( _WEImp.getDescriptionWEImp().length() > 5000 )
            throw new WEBuildingException( "description", _WEImp.getDescriptionWEImp() );
        if ( _WEImp.getPrefixData().length() > 500)
	    throw new WEBuildingException( "extraArg", _WEImp.getPrefixData() );

        /*
         * Ensure that predeployed fields are set or not set depending on value
         */
        if(_WEImp.getPreDeployed()){
            if(_WEImp.getShellPath().isEmpty()){
                throw new ValidationFailedException("If the Workflow Engine Implementation is predeployed then a path to the engine executable should be provided");
            }
            _WEImp.setShellWEFileId(null);
            _WEImp.setZipWEFileId(null);
        }else{
            if(_WEImp.getShellWEFileId() == null){
                throw new ValidationFailedException("If the Workflow Engine Implementation is not predeployed then an Executable script or file should be provided");
            }
            if(_WEImp.getZipWEFileId() == null){
                throw new ValidationFailedException("If the Workflow Engine Implementation is not predeployed then a archive containing the Engine data should be provided");
            }
            _WEImp.setShellPath("");
        }

        em.merge(_WEImp);
        em.flush();
        logger.log(Level.INFO, "Updated WEImp {0}", _WEImp.getNameWEImp());
    }

    @Override
    public WEImplementation dupeWEImp(WEImplementation _wei, String name) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, WEBuildingException{

        if(_wei == null){
            logger.log(Level.SEVERE, "Please select a Workflow Engine Implementation to duplicate");
            throw new ValidationFailedException("Please select a Workflow Engine Implementation to duplicate");
        }

        WEImplementation newWEImp = null;
            if(!_wei.getPreDeployed()){
                newWEImp = this.createWEImp(name, _wei.getDescriptionWEImp(), _wei.getPreDeployed(), _wei.getPrefixData(), _wei.getIdWE(), _wei.getIdBackendInst(), _wei.getZipWEFileId(), null, _wei.getShellWEFileId());
            } else if (_wei.getPreDeployed()){
                newWEImp = this.createWEImp(name, _wei.getDescriptionWEImp(), _wei.getPreDeployed(), _wei.getPrefixData(), _wei.getIdWE(), _wei.getIdBackendInst(), null, _wei.getShellPath(), null);
            }
        return newWEImp;
    }
    //these following functions defines if a anonymous user is allowed to delete workflow engine implementation. Basically not. This test is useful to be sure the current object is not null.
    @Override
    public String canPublicDeleteWEImplementation(int weImpId){
        return canPublicDeleteWEImplementation(em.find(WEImplementation.class, weImpId));
    }

    private String canPublicDeleteWEImplementation(WEImplementation weImp) {
        if(weImp == null)
            return "This workflow engine implementation does not exist";
        return null;
    }

    //these following functions defines if a identified user is allowed to delete workflow engine implementation
    @Override
    public String canUserDeleteWEImplementation(int callerId, int weImpId){
        return canUserDeleteWEImplementation(em.find(User.class, callerId));
    }

    private String canUserDeleteWEImplementation(User caller) {
        //only the admin or a WE manager is supposed to be allowed to perform this
        if(caller == null){
            return "your account has been deleted";
        }//need to update the correct role
        if(!caller.isActive()){
            return "your account has been deactivated";
        }
        return null;
    }

    @Override
    public void deleteWEImplementation(int weImpId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException {
        User caller = getCallerUser();
        WEImplementation weImp = em.find(WEImplementation.class, weImpId);

        if(weImp == null){
            throw new EntityNotFoundException("No such Implementation exists in the DB");
        }

        String err = canUserDeleteWEImplementation(caller.getId(), weImp.getIdWEImp());
        if(err != null){
            throw new AuthorizationException(err);
        }
        em.remove(weImp);
        em.flush();
        logger.log(Level.INFO, "Deleted WEImp {0}", weImp.getNameWEImp());

        /*
         * Ensure that the WEImp is removed from the WEFile collections.
         */
        if(!weImp.getPreDeployed()){
            weImp.getShellWEFileId().getWeImplementationCollection().remove(weImp);
            weImp.getZipWEFileId().getWeImplementationCollection().remove(weImp);
            em.merge(weImp.getShellWEFileId());
            em.merge(weImp.getZipWEFileId());
            em.flush();
        }

        weImp.getIdBackendInst().getWeImplementationCollection().remove(weImp);
        em.merge(weImp.getIdBackendInst());

        //May not be necessary!
        weImp.getWEDev().getWEImps().remove(weImp);
        em.merge(weImp.getWEDev());

    }

    /*
     * BACKEND MANAGEMENT FUNCTIONS --------------------------------------------------
     */

    @Override
    public String canUserCreateBackends(int callerId){
        return canUserCreateBackends(em.find(User.class, callerId));
    }

    private String canUserCreateBackends(User caller) {
        //only the admin or a WE manager is supposed to be allowed to perform this
        if(caller == null){
            return "your account has been deleted";
        }//need to update the correct role
        if(!caller.isActive()){
            return "your account has been deactivated";
        } else if(caller.isAdmin() || caller.isWEDev()){
            return null;
        }else{
            return "You are not permitted to perform this action";
        }

    }

    /*
     * Two Functions that do the same thing! Destroy one!
     */
    @Override
    public List<Backend> listBackendNames() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return new ArrayList<Backend>(0);
        }
        return em.createNamedQuery("Backend.listOnlyNames", Backend.class).getResultList();
    }

        @Override
    public List<Backend> listBackendAll() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return new ArrayList<Backend>(0);
        }
        return em.createNamedQuery("Backend.listAll", Backend.class).getResultList();
    }

    @Override
    public Backend getBackendById(int _bId){
        return em.createNamedQuery("Backend.findByIdBackend", Backend.class).setParameter("idBackend", _bId).getSingleResult();
    }


    @Override
    public Backend createBackend(String name, String desc, Collection<JobManager> _jList)
            throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException
    {

        User caller = getCallerUser();
        String err = canUserCreateBackends(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //validate app name
        if(!name.matches("[\\-\\w\\.]+{3,50}")){
            throw new ValidationFailedException("Backend names can only contain alphanumeric characters and must be between 3 and 50 charaters long");
        }
        if(_jList.isEmpty()){
            throw new ValidationFailedException("Need to pick at laest 1 Job Manager");
        }
        if(em.createNamedQuery("Backend.findByBackendName", Backend.class).setParameter("backendName", name).getResultList().size() > 0){
            throw new EntityAlreadyExistsException("Backend '"+name+"' already exists");
        }

        Backend b = new Backend(name, desc, _jList);
        /* clean up if this works! */

        for(JobManager j : _jList){
            //j.getBackendCollection().add(selectedBackend);
            Collection<Backend> temp = j.getBackendCollection();
            temp.add(b);
            j.setBackendCollection(temp);
            em.merge(j);
        }

        em.persist(b);
        em.flush();
        return b;
    }

    @Override
    public void updateBackend(Backend _be, Collection<JobManager> _jm)
            throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException {

        User caller = getCallerUser();
        String err = canUserCreateBackends(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //validate app name
        if(!_be.getBackendName().matches("[\\-\\w\\.]+{3,50}")){
            throw new ValidationFailedException("Backend names can only contain alphanumeric characters and must be between 3 and 50 charaters long");
        }
        if(_jm.isEmpty()){
            throw new ValidationFailedException("Need to pick at laest 1 Job Manager");
        }

        List<Backend> results = em.createNamedQuery("Backend.findByBackendName", Backend.class).setParameter("backendName", _be.getBackendName()).getResultList();

        if(results.size() > 0 && !results.contains(_be)){
            throw new EntityAlreadyExistsException("Backend '"+_be.getBackendName()+"' already exists");

        }

        /*
         * Need to test if old list of supported Job Managers is different to new list
         * If it is. New list needs ammeding on be and on jm collection side of the ManyToMany relationship
         * This is a long way round! Better way perhaps?
         */

        Collection<JobManager> _be_jm = _be.getJobManagerCollection();

        /*
         * Remove all BE objects from old JobManager backend collections
         * This represents the ManyToMany relationship from the JobManagers side
         */
        for(JobManager bjm : _be_jm)
        {
            Collection<Backend> temp = bjm.getBackendCollection();
            temp.remove(_be);
            bjm.setBackendCollection(temp);
            em.merge(bjm);
        }

        /*
         * Add the editted backend to the new list of JobManagers' BE collections
         * This represents the ManyToMany relationship from the JobManagers side
         */
        for(JobManager j : _jm){
            Collection<Backend> temp = j.getBackendCollection();
            temp.add(_be);
            j.setBackendCollection(temp);
            em.merge(j);
        }

        /*
         * This represents the ManyToMany relationship on the backend side
         * Adds the updated collection of JobManagers to the updated Backend
         */
        _be.setJobManagerCollection(_jm);
        em.merge(_be);
        em.flush();
    }

    //need auth here!
    @Override
    public void deleteBackend(Backend _be)
        throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException
    {
        /*
         * Remove all BE objects from old JobManager backend collections
         * This represents the ManyToMany relationship from the JobManagers side
         * TODO: Validation!! Throw some things!
         */
        Collection<JobManager> _be_jm = _be.getJobManagerCollection();

        for(JobManager bjm : _be_jm)
        {
            Collection<Backend> temp = bjm.getBackendCollection();
            temp.remove(_be);
            bjm.setBackendCollection(temp);
            em.merge(bjm);
        }

        Backend remove = em.merge(_be);
        em.remove(remove);
        em.flush();
    }

    /*
     * WEFiles Management Functions -------------------------------------------------
     */

    @Override
    public String canUserModifyWEFiles(int callerId){
        return canUserModifyWEFiles(em.find(User.class, callerId));
    }

    private String canUserModifyWEFiles(User caller) {
        //only the admin or a WEDev is supposed to be allowed to perform this
        if(caller == null){
            return "Your account has been deleted";
        }//need to update the correct role
        if(!caller.isActive()){
            return "Your account has been deactivated";
        } else if (caller.isAdmin() || caller.isWEDev()){
            return null;
        } else {
            return "You are not permitted to perform this action";
        }

    }

    @Override
    public String canUserDownloadWEFiles(int callerId){
        return canUserModifyWEFiles(em.find(User.class, callerId));
    }

    @Override
    public List<WEImplementation> getAfftectedWEImps(WEUploadedFile _wefile){
        if(_wefile == null){
            return null;
        }else if(_wefile.getIsData()){
            return em.createNamedQuery("WEImplementation.findByZipFile", WEImplementation.class).setParameter("zipFile", _wefile).getResultList();
        }else if(!_wefile.getIsData()){
            return em.createNamedQuery("WEImplementation.findByShellFile", WEImplementation.class).setParameter("shellFile", _wefile).getResultList();
        } else {
            return null;
        }

    }

    @Override
    public File getWEFile(int idWE, String filename)
            throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException
    {
        //TODO: validate pathName
        if(filename == null){
            throw new ValidationFailedException("no filename specified"); //TODO: elaborate error message
        }
        if(!filename.matches("[a-zA-Z0-9][\\.a-zA-Z0-9_-]*")){
            throw new ValidationFailedException("invalid filename: '"+filename+"'"); //TODO: elaborate error message
        }

        File f = new File(this.repoPath + "WEFiles/" + idWE + "/" + filename);

        if(!f.exists())
        {
            throw new FileOperationFailedException("file not found");
        }

        return f;

    }

    @Override
    public Collection<WEUploadedFile> listFilesByWE(Platform _we, boolean isData)
    {
        ArrayList<WEUploadedFile> tempList = new ArrayList<WEUploadedFile>();
        for(WEUploadedFile temp : _we.getUploadedFileCollection()){
            if(temp.getIsData() == isData){
                tempList.add(temp);
            }
        }

        return tempList;
    }

    @Override
    public WEUploadedFile getWEFileById(int id)
    {
        /* What do we need? Validation!! */
        return em.createNamedQuery("WEUploadedFile.findByIdWEFile", WEUploadedFile.class).setParameter("idWEFile", id).getSingleResult();
    }

    @Override
    public WEUploadedFile uploadWEFile(String name, String desc, Platform _we, String pathName, InputStream source, boolean isData)
            throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, FileOperationFailedException
    {
        /*
         * Ensure file is always unique! Apply proper error message if it is not!
         * Add validation, I guess. Change xhmtl to remove strings from backingbean
         */

        User caller = getCallerUser();

        String err = canUserModifyWEFiles(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }
        //TODO: validate pathName
        if(pathName == null){
            throw new ValidationFailedException("no filename specified"); //TODO: elaborate error message
        }
        if(!pathName.matches("[a-zA-Z0-9][\\.a-zA-Z0-9_-]*")){
            throw new ValidationFailedException("invalid filename: '"+pathName+"'"); //TODO: elaborate error message
        }
        if(name == null || name.equals("")){
            throw new ValidationFailedException("name invalid"); //TODO: elaborate error message
        }
        if(desc == null || desc.equals("")){
            throw new ValidationFailedException("description invalid"); //TODO: elaborate error message
        }

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try{
            File weDir = new File(this.repoPath + "WEFiles/" + _we.getId());
            if(!weDir.exists()){
                if(!weDir.mkdirs()){
                    source.close();
                    throw new FileOperationFailedException("could not upload file (server error)");
                }
            }
            File f = new File(weDir.getAbsolutePath()+"/"+pathName);
            if(f.exists()){
                throw new FileOperationFailedException("the file already exists: '"+pathName+"'");
            } else { //file does not exist
                if(!f.createNewFile()) { //could not create file
                    logger.log(Level.SEVERE, "internal error when uploading file: failed to create file"); //log error
                    source.close();
                    throw new FileOperationFailedException("could not upload file (server error)");
                }
            }
            bis = new BufferedInputStream(source);
            bos = new BufferedOutputStream(new FileOutputStream(f));
            byte[] buffer = new byte[102400]; //TODO: should not throw buffer away like this. implement a thread safe buffer pool instead. use singleton pattern
            while (true) {
                int amountRead = bis.read(buffer);
                if (amountRead == -1) {
                   break;
                }
                bos.write(buffer, 0, amountRead);
            }

            /*
             * To ensure the multiplicity of the relationship is maintained!
             * A reference to the WorkflowEngine is stored inside the new File
             */

            //A file can only have one associated WE
            Collection<Platform> temp = new ArrayList<Platform>();
            temp.add(_we);

            WEUploadedFile _uf = new WEUploadedFile(name, desc, pathName, temp, isData);
            em.persist(_uf);

            Collection<WEUploadedFile> uCol = _we.getUploadedFileCollection();
            uCol.add(_uf);
            _we.setUploadedFileCollection(uCol);

            em.merge(_we);
            em.flush();

            return _uf;
        }catch(IOException e){
            logger.log(Level.SEVERE, "internal error when uploading file: {0}", e.getMessage()); //log error
            throw new FileOperationFailedException("could not upload file (server error)");
        }finally{
            try {
                if(bis != null){
                    bis.close(); //closes wrapped streams ie source.close() is not necessary
                }else{ //didn't get as far as creating bis, need to call source.close()
                    if(source != null){
                        source.close();
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "failed to close upload stream reader : {0}", e.getMessage()); //log error
            }
            try {
                if(bos != null){
                    bos.close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "failed to close output file writer: {0}", e.getMessage()); //log error
            }
        }
    }

    @Override
    public Platform deleteWEFiles(WEUploadedFile _weFile)
            throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException
    {

        logger.log(Level.INFO, "{0} attempting deletion...", _weFile.getName());

        WEUploadedFile owner;
        User caller = getCallerUser();

        String err = canUserModifyWEFiles(caller);
        if(err != null){
            throw new AuthorizationException(err);
        }

        //incase array is empty
        if(_weFile == null){
            throw new ValidationFailedException("Error with File Container. Fatal.");
        }

            String t = this.repoPath + "WEFiles/" + _weFile.getWorkflowEngineCollection().iterator().next().getId() + "/" + _weFile.getFilePath();
            File f = new File(t);

            if(!f.exists()){
                logger.log(Level.SEVERE, "{0} file not found or does not exist on server", _weFile.getName());
                throw new FileOperationFailedException(_weFile.getName() + " file not found or does not exist on server");
            }

            if(!f.delete()){
                logger.log(Level.SEVERE, "{0} delete failed at delete()", _weFile.getName());
                throw new FileOperationFailedException("Delete Failed:" + t);
            }else{
                if(_weFile.getIdWEFile() != null){
                    logger.log(Level.INFO, "{0} deleted! now removing from DB...", _weFile.getName());
                    owner = em.find(WEUploadedFile.class, _weFile.getIdWEFile());
                    logger.log(Level.INFO, "{0} found in DB...", _weFile.getName());
                }
                else{
                    throw new EntityNotFoundException("No ID for object OR object doesn't exist in database");
                }


                try{

                    logger.log(Level.INFO, "{0}: updating object containers and syncing with entity manager...", owner.getName());
                    //updating object containers and syncing with entity manager...
                    owner.getWorkflowEngineCollection().iterator().next().getUploadedFileCollection().remove(owner);

                    logger.log(Level.INFO, "{0} Removing from DB...", _weFile.getName());
                    em.remove(em.merge(owner));
                    em.flush();

                    Platform tempWE = em.merge(owner.getWorkflowEngineCollection().iterator().next());
                    logger.log(Level.INFO, "Updating WE details and relations: {0}", tempWE.getName());
                    em.flush();

                    //switch these two?
                    em.refresh(tempWE);  //forces up-to-date version of tempWE in db.
                    em.flush();

                    if(tempWE.getUploadedFileCollection().contains(owner)){
                        throw new FileOperationFailedException("Failed to remove entry from Workflow Engine Entity. Contact System Admin");
                    }else{
                        return tempWE;
                    }
                }catch(Exception e){
                    //this means that the deletion of the file may have occured but a record could still be in the database...this is bad!
                    throw new FileOperationFailedException(e.getMessage() + "\nEntity Delete Failed: Contact system admin");
                }
            }
    }

    /*
     * Operating system management functions ----------------------------------------------------
     */

    //returns the list of OS
    @Override
    public List<OperatingSystems> listOperatingSystemsNames() {
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return new ArrayList<OperatingSystems>(0);
        }
        return em.createNamedQuery("OperatingSystems.findAll", OperatingSystems.class).getResultList();
    }

    @Override
    public int getOsIdByName(String name){
        return em.createNamedQuery("OperatingSystems.findByName", OperatingSystems.class).setParameter("name", name).getSingleResult().getIdOS();
    }

    @Override
    public OperatingSystems getOperatingSystemById(int id){
        return em.createNamedQuery("OperatingSystems.findByIdOS", OperatingSystems.class).setParameter("idOS", id).getSingleResult();
    }

    @Override
    public String canUserModifyOS(int callId){
        return canUserModifyOS(em.find(User.class, callId));
    }


    private String canUserModifyOS(User caller) {
        //only the admin or a WE manager is supposed to be allowed to perform this
        if(caller == null){
            return "your account has been deleted";
        }//need to update the correct role
        if(!caller.isActive()){
            return "your account has been deactivated";
        }else if(caller.isAdmin()){
            return null;
        }else{
            return "You are not permitted to perform this action";
        }

    }

    @Override
    public OperatingSystems createOS(String name, String version)
            throws ValidationFailedException, AuthorizationException, EntityAlreadyExistsException
    {

        User caller = getCallerUser();

        //Assume if can create, then can delete/update!!
        String err = canUserModifyOS(caller);

        if(err != null){
            throw new AuthorizationException(err);
        }

        if(!name.matches("[\\-\\w\\.]+{3,50}"))
            throw new ValidationFailedException("Please format the name correctly");

        if(version.length() > 50 || !name.matches("[\\-\\w\\.]+{3,50}"))
            throw new ValidationFailedException("Please format the version correctly");

        List<OperatingSystems> nameTemp = em.createNamedQuery("OperatingSystems.findByName", OperatingSystems.class).setParameter("name", name).getResultList();

        for(OperatingSystems t : nameTemp){
            if(t.getVersion().equals(version))
                throw new EntityAlreadyExistsException("An Operating System with that Version already exists");
        }

        OperatingSystems newOS = new OperatingSystems(name, version);

        em.persist(newOS);
        em.flush();

        return newOS;
    }

    @Override
    public OperatingSystems updateOS(OperatingSystems _os)
            throws ValidationFailedException, AuthorizationException, EntityAlreadyExistsException
    {

        /*
         * names do not have to be unique because versions can represent the uniqueness
         */

        User caller = getCallerUser();

        //Assume if can create, then can delete/update!!
        String err = canUserModifyOS(caller);

        if(err != null){
            throw new AuthorizationException(err);
        }

        List<OperatingSystems> nameTemp = em.createNamedQuery("OperatingSystems.findByName", OperatingSystems.class).setParameter("name", _os.getName()).getResultList();

        for(OperatingSystems t : nameTemp){
            if(t.getVersion().equals(_os.getVersion()))
                throw new EntityAlreadyExistsException("An Operating System with that Version already exists");
        }

        if(!_os.getName().matches("[\\-\\w\\.]+{3,50}"))
            throw new ValidationFailedException("Please format the name correctly");

        if(_os.getVersion().length() > 50 || !_os.getName().matches("[\\-\\w\\.]+{3,50}"))
            throw new ValidationFailedException("Please format the version correctly");

        return em.merge(_os);
    }

    @Override
    public void deleteOS(OperatingSystems _os)
            throws ValidationFailedException, AuthorizationException, EntityNotFoundException
    {

        User caller = getCallerUser();

        //Assume if can create, then can delete/update!!
        String err = canUserModifyOS(caller);

        if(err != null){
            throw new AuthorizationException(err);
        }

        if(_os == null)
            throw new ValidationFailedException("Null OS: An error has occurred, please try again");

        if(em.createNamedQuery("OperatingSystems.findByIdOS", OperatingSystems.class).setParameter("idOS", _os.getIdOS()).getResultList().isEmpty())
            throw new EntityNotFoundException("Operating System destined for destruction doesn't exist, reload and try again.");

        /*
         * TODO: Fix!
         * This REALLY WON'T Work!
         */

        OperatingSystems _osR = em.merge(_os);
        em.remove(_osR);
        em.flush();
    }

    /*
     * Job Manager management functions -------------------------------------------------------
     */

    //Return appropriate job manager objects from string references
    @Override
    public List<JobManager> getJobManagersFromString(ArrayList<String> selectedJobManagers)
    {
        List<JobManager> allList = this.listJobManagers();
        List<JobManager> newList = new ArrayList<JobManager>();

        for(JobManager t : allList)
        {
            for(String s : selectedJobManagers)
            {
                if(t.getJobManagerName().equals(s))
                {
                    newList.add(t);
                    continue;
                }
            }
        }

        return newList;
    }

    @Override
    public List<JobManager> listJobManagers(){
        User caller = getListCaller();
        if(caller == null || !caller.isActive()){ //only active users may list platforms
            return new ArrayList<JobManager>(0);
        }
        return em.createNamedQuery("JobManager.findAll", JobManager.class).getResultList();
    }

    @Override
    public List<JobType> listJobTypes(){
        return em.createNamedQuery("JobType.findAll", JobType.class).getResultList();
    }

    @Override
    public JobType getJobTypeFromString(int _jId){
        return em.createNamedQuery("JobType.findByJobTypeId", JobType.class).setParameter("jobTypeId", _jId).getSingleResult();
    }


    /*
     * Backend Instance Management Functions ------------------------------------------------
     */

    @Override
    public BeInstance getBEInstanceById(int _bId){
        return em.createNamedQuery("BeInstance.findByIdBackendInst", BeInstance.class).setParameter("idBackendInst", _bId).getSingleResult();

    }

    @Override
    public BeInstance updateBEI(BeInstance _be)
            throws ValidationFailedException, AuthorizationException, EntityNotFoundException
    {
        if(!(getCallerUser().isAdmin() || _be.getWEDev().equals(getCallerUser()))){
            throw new AuthorizationException("You are not permitted to perform this action");
        }

        if(em.find(BeInstance.class, _be.getIdBackendInst()) == null){
            throw new EntityNotFoundException("Either a Backend Configuration has not been selected or it does not exist!");
        }

        if(_be.getIdBackend() == null){
            throw new ValidationFailedException("Please select an appropriate Backend to alter this configuration");
        }

        if(_be instanceof GT4){
            if(((GT4)_be).getSite().isEmpty()){
                throw new ValidationFailedException("Invalid Resource String");
            }
            if(((GT4)_be).getJobManager().isEmpty()){
                throw new ValidationFailedException("Invalid Job Manager");
            }
            if(((GT4)_be).getJobTypeId() == null){
                throw new ValidationFailedException("Invalid Job Type");
            }
        }
        if(_be instanceof GT2){
            if(((GT2)_be).getSite().isEmpty()){
                throw new ValidationFailedException("Invalid Resource String");
            }
            if(((GT2)_be).getJobManager().isEmpty()){
                throw new ValidationFailedException("Invalid Job Manager");
            }
            if(((GT2)_be).getJobTypeId() == null){
                throw new ValidationFailedException("Invalid Job Type");
            }
        }

        _be = em.merge(_be);
        em.flush();


        return _be;
    }

    @Override
    public void deleteBEI(BeInstance _beI)
           throws EntityNotFoundException, AuthorizationException, ValidationFailedException
    {
        /*
         * Check management of relationships
         */

        if((_beI = em.find(BeInstance.class, _beI.getIdBackendInst())) == null){
            logger.log(Level.SEVERE,"Entity not found in DB");
            throw new EntityNotFoundException("Entity not found in DB");
        }

        if(em.createNamedQuery("WEImplementation.findByBEI", WEImplementation.class).setParameter("bei", _beI).getResultList().size() > 0){
            logger.log(Level.SEVERE, "Backend Configuration: {0} cannot be deleted. It is used in one or more Workflow Engine Implementations", _beI.getBackendInstName());
            throw new EntityNotFoundException("Backend Configuration: " + _beI.getBackendInstName() + " cannot be deleted. It is used in one or more Workflow Engine Implementations");
        }

        if(!(getCallerUser().isAdmin() || getCallerUser().equals(_beI.getWEDev()))){
            logger.log(Level.SEVERE,"Not permitted to perform this action - Deleting a Backend Configuration");
             throw new AuthorizationException("Not permitted to perform this action - Deleting a Backend Configuration " + _beI.getBackendInstName());
        }


        em.remove(_beI);
        em.flush();
    }



    @Override
    public BeInstance createBeInstance(String _name, Backend _idBackend, String _site, String _backendOut, String _backendErr, String _jobManager, JobType _jobType, OperatingSystems _idOS, String _resource, int callerId)
            throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException
    {

        logger.log(Level.INFO, "Beginning Backend Instance Creation");

        User caller = getCallerUser();

        String err = canUserCreateWEImplementations(caller);

        User wedev = em.find(User.class, callerId);

        if(err != null){
            logger.log(Level.SEVERE, err);
            throw new AuthorizationException(err);
        }

        if(wedev == null){
            logger.log(Level.SEVERE, "No such WEDev user or User couldn't be found");
            throw new AuthorizationException("Workflow Developer doesn't exist or you do not have the correct privileges, contact an administrator.");
        }

        /*
         * TODO: Test regex
         */
        if(!_name.matches("[\\-\\w\\.]+{3,50}")){
            logger.log(Level.WARNING, "Please format the name correctly");
            throw new ValidationFailedException("Please format the name correctly");
        }

        if(_resource.isEmpty()){
            logger.log(Level.SEVERE, "Resource is null");
            throw new ValidationFailedException("Please input a resource to associate with the configuration.");
        }

        if(!em.createNamedQuery("BeInstance.findByBackendInstName", BeInstance.class).setParameter("backendInstName", _name).getResultList().isEmpty()){
            logger.log(Level.WARNING, "BEInstance Entity already exists");
            throw new EntityAlreadyExistsException("A Backend Instance with that name already exists, please chose another.");
        }


        if(_idBackend == null){
            logger.log(Level.SEVERE, "Abstract backend is null");
            throw new ValidationFailedException("Please select an Backend to associate with the configuration.");
        }

        if(_idOS == null){
            logger.log(Level.SEVERE, "Operating System var is null");
            throw new ValidationFailedException("Please select an OS to associate with the configuration.");
        }

        BeInstance newBEI = null;

        if(_idBackend.getBackendName().equalsIgnoreCase("GT4") || _idBackend.getBackendName().equalsIgnoreCase("GT2")){
            if(_jobType == null){
                logger.log(Level.SEVERE, "JobType is null");
                throw new ValidationFailedException("Please select a Job Type to associate woth this configuration.");
            }
            if(_jobManager.isEmpty()){
                logger.log(Level.SEVERE, "Job Manager is null");
                throw new ValidationFailedException("Please select a Job Manager to associate with this configuration.");
            }

            if(_idBackend.getBackendName().equalsIgnoreCase("GT4") ){
                newBEI = new GT4(_jobManager, _jobType, _name, _idBackend, _site, _backendOut, _backendErr, _idOS, _resource, wedev);
                logger.log(Level.INFO, "New GT4 object created");
            }else if(_idBackend.getBackendName().equalsIgnoreCase("GT2")){
                newBEI = new GT2(_jobManager, _jobType, _name, _idBackend, _site, _backendOut, _backendErr, _idOS, _resource, wedev);
                logger.log(Level.INFO, "New GT2 object created");
            }

        }

        if(_idBackend.getBackendName().equalsIgnoreCase("gLite")){
                newBEI = new GLite(_name, _idBackend, _backendOut, _backendErr, _idOS, _resource, wedev);
                logger.log(Level.INFO, "New "+_idBackend.getBackendName()+" object created");
        }

        if(_idBackend.getBackendName().equalsIgnoreCase("local")){
                newBEI = new Local(_name, _idBackend, _backendOut, _backendErr, _idOS, _resource, wedev);
                logger.log(Level.INFO, "New "+_idBackend.getBackendName()+" object created");
        }

        em.persist(newBEI);

        if(newBEI.getIdBackend().getBeInstanceCollection() == null){
            newBEI.getIdBackend().setBeInstanceCollection(new ArrayList<BeInstance>());
        }

        newBEI.getIdBackend().getBeInstanceCollection().add(newBEI);

        if(newBEI.getIdOS().getBeInstanceCollection() == null){
            newBEI.getIdOS().setBeInstanceCollection(new ArrayList<BeInstance>());
        }

        newBEI.getIdOS().getBeInstanceCollection().add(newBEI);

        em.merge(newBEI.getIdBackend());
        em.merge(newBEI.getIdOS());
        em.flush();
        return newBEI;
    }

    @Override
    public BeInstance dupeBeInstance (BeInstance _bei, String _name) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException{

        if(_bei == null){
            logger.log(Level.SEVERE, "Please select a backend configuration to duplicate");
            throw new ValidationFailedException("Please select a backend configuration to duplicate");
        }

        if(_bei.getIdBackend().getBackendName().equalsIgnoreCase("GT4")){
            GT4 temp = ((GT4)_bei);
            logger.log(Level.INFO, "Attempting to Dupelicate Backend Configuration: " + _bei.getBackendInstName() + "...");
            return this.createBeInstance(_name, temp.getIdBackend(), temp.getSite(), temp.getBackendOutput(), temp.getBackendErrorOut(), temp.getJobManager(), temp.getJobTypeId(), temp.getIdOS(), temp.getResource(), getCallerUser().getId());
        }else if(_bei.getIdBackend().getBackendName().equalsIgnoreCase("GT2")){
            GT2 temp = ((GT2)_bei);
            logger.log(Level.INFO, "Attempting to Dupelicate Backend Configuration: " + _bei.getBackendInstName() + "...");
            return this.createBeInstance(_name, temp.getIdBackend(), temp.getSite(), temp.getBackendOutput(), temp.getBackendErrorOut(), temp.getJobManager(), temp.getJobTypeId(), temp.getIdOS(), temp.getResource(), getCallerUser().getId());
        }else if(_bei.getIdBackend().getBackendName().equalsIgnoreCase("gLite")){
            GLite temp = ((GLite)_bei);
            logger.log(Level.INFO, "Attempting to Dupelicate Backend Configuration: " + _bei.getBackendInstName() + "...");
            return this.createBeInstance(_name, temp.getIdBackend(), null, temp.getBackendOutput(), temp.getBackendErrorOut(), null, null, temp.getIdOS(), temp.getResource(), getCallerUser().getId());
        }else{
            logger.log(Level.SEVERE, "Null pointer received, no abstract backend selected!");
            return null;
        }
    }

    @Override
    public List<BeInstance> getBeInstanceAll(){
        return em.createNamedQuery("BeInstance.findAll", BeInstance.class).getResultList();
    }

    /**
     * SUBMISSION SERVICE
     *
     */
    // submission service functions: Tests have to be performed
    @Override
    public List<ImplShort> listValidatedImplementations(String extServiceId,
            String extUserId)
            throws IllegalArgumentException, DatabaseProblemException {
        logger.info("List of all validated implementation requested");
        logger.log(Level.INFO, "Service ID: {0}", extServiceId);
        logger.log(Level.INFO, "User ID: {0}", extUserId);

        List<Object[]> list = SubmissionRequests.
                getAllValidatedImplementations(extServiceId, extUserId, em);

        try {
            return SubmissionHelpers.treatValidatedImplementations(list);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Processing data problem: "
                    + ex.getMessage(), ex);
        }
    }

    // check not existing case
    @Override
    public List<String> listWorkflowEngineInstances(String implName)
            throws IllegalArgumentException, DatabaseProblemException {
        logger.info("List of all workflow instances requested");
        logger.log(Level.INFO, "Implementation name: {0}", implName);

        int implId = SubmissionRequests.getValidatedImplementationID(implName, em);
        return SubmissionRequests.getAllWorkflowEngineInstances(implId, em);
    }

    // check not existing case
    @Override
    public List<Parameter> listParametersImpl(String implName)
            throws IllegalArgumentException, DatabaseProblemException {
        logger.info("List of all Parameters requested");
        logger.log(Level.INFO, "Implementation name: {0}", implName);

        int implId = SubmissionRequests.getValidatedImplementationID(implName, em);
        List<ImpAttribute> listAttributes =
                SubmissionRequests.getAllParameters(implId, em);

        try {
            return SubmissionHelpers.getNonFixedParameters(listAttributes);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Processing data problem: "
                    + ex.getMessage(), ex);
        }
    }

    @Override
    public ImplJSDL loadImplementationJSDL(String repositoryURL, String implName)
            throws IllegalArgumentException, DatabaseProblemException {
        logger.info("Full implementation for JSDL requested");
        logger.log(Level.INFO, "Implementation name: {0}", implName);

        int implId = SubmissionRequests.getValidatedImplementationID(implName, em);
        Implementation implementation = SubmissionRequests.getImplementation(implId, em);

        try {
            return SubmissionHelpers.treatImplementationToJSDL(repositoryURL,
                    implementation, implId);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Processing data problem: "
                    + ex.getMessage(), ex);
        }
    }

    @Override
    public WorkflowEngineInstance loadWEIJSDL(String repositoryURL,
            EngineData engineData)
            throws IllegalArgumentException, DatabaseProblemException {
        logger.info("Full workflow engine instance for JSDL requested");

        Platform engine = SubmissionRequests.getWorkflowEngineData(engineData, em);

        try {
            return SubmissionHelpers.loadWorkflowEngineImplementation(
                    repositoryURL, engine, engineData.getEngineInstanceName());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Processing data problem: "
                    + ex.getMessage(), ex);
        }
    }

    /**
     * END OF SUBMISSION SERVICE
     *
     */

    @Override
    public List<Ratings> getRatingsByImpId(int impId)
            throws AuthorizationException, EntityNotFoundException
    {
        /* Policy on Ratings?
        if(!getCallerUser().isActive()){
            throw new AuthorizationException("Only active users may perform this action");
        }*/

        if(em.find(Implementation.class, impId) == null){
            throw new EntityNotFoundException("No Imp found");
        }

        return em.createNamedQuery("Ratings.findByImpId", Ratings.class).setParameter("imp", em.find(Implementation.class, impId)).getResultList();
    }

    @Override
    public void insertOrUpdateRating(Ratings r)
            throws AuthorizationException, EntityNotFoundException, ValidationFailedException
    {
        if(r.getRate() < 0 || r.getRate() > 10){
            throw new ValidationFailedException("Ratings value is incorrect range");
        }

        if(!getCallerUser().isActive()){
            throw new AuthorizationException("Unregistered user is attempting to rate an application");
        }

        if(em.find(Implementation.class, r.getVersionID().getId()) == null){
            throw new EntityNotFoundException("Implementation not found");
        }

        Query q = em.createNamedQuery("Ratings.findByImpIdAndUserId", Ratings.class);
        q.setParameter("imp", r.getVersionID());
        q.setParameter("id", r.getUserID());

        try{
            Ratings temp = (Ratings) q.getSingleResult();
            temp.setRate(r.getRate());
            if(temp.getRate() == 0){
                this.removeSingleRating(r.getVersionID(), r.getUserID());
            }else{
                em.merge(temp);
                logger.log(Level.INFO, "Ammended user {0} rating for Imp: {1}...", new Object[]{r.getUserID().getLoginName(), r.getVersionID().getId()});
            }
        } catch (NoResultException e) {
            em.persist(r);
            logger.log(Level.INFO, "Added user {0} rating for Imp: {1}...", new Object[]{r.getUserID().getLoginName(), r.getVersionID().getId()});
        } catch (Exception e){
            logger.log(Level.SEVERE, null, e);
        }finally{
            em.flush();
            logger.log(Level.INFO, "Successfully completed rating transacton!");
        }
    }

    @Override
    public Implementation getImpById(int id){
        return em.find(Implementation.class, id);
    }

    @Override
    public void deleteRatingsOfImp(int impId)
            throws ValidationFailedException, AuthorizationException, EntityNotFoundException
    {

    }


    @Override
    public void removeSingleRating(int impId, int userId)
            throws AuthorizationException, EntityNotFoundException
    {
        this.removeSingleRating(em.find(Implementation.class, impId), em.find(User.class, userId));
    }


    private void removeSingleRating(Implementation impId, User userId)
            throws AuthorizationException, EntityNotFoundException
    {
        if(impId == null){
            logger.log(Level.SEVERE, "Implementation not found");
            throw new EntityNotFoundException("Implementation not found");
        }
        if(userId == null){
            logger.log(Level.SEVERE, "User not found");
            throw new EntityNotFoundException("User not found");
        }

        Query q = em.createNamedQuery("Ratings.findByImpIdAndUserId", Ratings.class);
        q.setParameter("imp", impId);
        q.setParameter("id", userId);

        try{
            Ratings temp = (Ratings) q.getSingleResult();
            em.remove(temp);
            logger.log(Level.INFO, "Removing user {0} rating for Imp: {1}...", new Object[]{userId.getLoginName(), impId.getId()});
        } catch (NoResultException e) {
            logger.log(Level.INFO, "No such rating exists for this user: {0} and this ImpId {1}", new Object[]{userId.getLoginName(), impId.getId()});
            throw new EntityNotFoundException("No such rating exists for this user: " + userId.getLoginName() + " and this ImpId " + impId.getId());
        } catch (Exception e){
            logger.log(Level.SEVERE, null, e);
        }finally{
            em.flush();
            logger.log(Level.INFO, "Successfully completed rating transacton!");
        }
    }

    @Override
    public User findUserByName(String login){
        return em.createNamedQuery("User.findByLoginName", User.class).setParameter("loginName", login).getSingleResult();
    }

    @Override
    public BeInstance loadBackendInstance(String implName, String engineInstance)
            throws IllegalArgumentException, DatabaseProblemException {
        int implId = SubmissionRequests.getValidatedImplementationID(implName, em);
        Platform engine = SubmissionRequests.getWorkflowEngineData(implId, em);

        try {
            return SubmissionHelpers.loadBackendInstance(engine, engineInstance);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Processing data problem: "
                    + ex.getMessage(), ex);
        }
    }
}
