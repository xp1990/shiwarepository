/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.edgi.repository.server;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Local;
import org.shiwa.repository.submission.objects.EngineData;
import org.shiwa.repository.submission.objects.ImplShort;
import org.shiwa.repository.submission.objects.JSDL.ImplJSDL;
import org.shiwa.repository.submission.objects.Parameter;
import org.shiwa.repository.submission.objects.workflowengines.WorkflowEngineInstance;
import org.shiwa.repository.toolkit.wfengine.*;
import uk.ac.wmin.edgi.repository.common.*;
import uk.ac.wmin.edgi.repository.entities.Implementation;
import uk.ac.wmin.edgi.repository.entities.Platform;
import uk.ac.wmin.edgi.repository.entities.Ratings;
import uk.ac.wmin.edgi.repository.entities.User;
import uk.ac.wmin.edgi.repository.transferobjects.*;

/**
 *
 * @author zsolt
 */
@Local
public interface ApplicationFacadeLocal {

    Logger getEdgiLogger();
    String getGlobusUserpass();
    String getGlobusUsercert();
    String getGlobusUserkey();

    //TODO: remove these, modify servlets to use new list* methods
    List<AttributeTO> getAppAttributesByKey(String attrKey);
    List<AttributeTO> getAppAttributesByKeyAndFilter(String attrKey, String query);
    List<AttributeTO> getImpAttributesByKeyAndFilter(String attrKey, String query);
    List<AttrListItemTO> getAppAttributeList(Collection<Integer> appIDs);
    List<AttrListItemTO> getAppAttributeList(Collection<Integer> appIDs, Collection<String> attrNames);
    List<ImpListItemTO> getImpList();
    List<AppListItemTO> getAppList();
    List<AppListItemTO> getAppList(String impAttrName, String impAttrVal) throws IllegalAttributeNameException;
    List<ImpListItemTO> getImpListForApps(Collection<Integer> appIDs);
    List<AttrListItemTO> getImpAttributeList(Collection<Integer> impIDs);
    List<AttrListItemTO> getImpAttributeList(Collection<Integer> impIDs, Collection<String> attrNames);
    List<ImpListItemTO> getImpListForApps(Integer appID, String attrName, String attrValue);
    List<ImpFileTO> getFilesForImps(List<Integer> idc);
    List<AppFileTO> getFilesForApps(List<Integer> idc);

    boolean isAuthorizedUser();
    UserTO loadUser(String name);
    List<UserTO> listUsers();
    List<UserTO> listUsersOfGroup(int groupId);
    List<PlatformTO> listPlatforms();
    List<PlatformTO> getPlatforms();
    List<ImpEmbedTO> listImpsForEmbedding();
    List<ImpEmbedTO> listImpsForEmbedding(String extServiceId);
    List<ImpEmbedTO> listImpsForEmbedding(String extServiceId, String extUserId);
    List<AttributeTO> listImpAttrsByKey(String key);

    PlatformTO loadPlatform(String name, String version);
    PlatformTO getPlatform(String name, String version);
    boolean getPlatformSubmittable(String name, String version);
    void togglePlatformSubmittable(Platform plat) throws AuthorizationException, ValidationFailedException;

    PlatformTO loadPlatform(int id);
    PlatformTO getPlatform(int id);

    List<PlatformTO> listPlatformsByName(String name);
    GroupTO loadGroup(String groupname);
    GroupTO getGroup(String groupname);
    List<GroupTO> listGroups();
    List<GroupTO> listGroupsOfUser(int userId);
    List<GroupTO> listOwnedGroupsOfUser(int userId);
    Date appTimestamp();
    Date impTimestamp();
    List<ApplicationTO> listApplications();
    List<ApplicationTO> listApplicationsPrivate();
    List<ApplicationTO> listApplicationsReadyForValidation();
    ApplicationTO loadApplication(int appId);
    List<ApplicationTO> listApplicationsUserCanModify();
    List<ApplicationTO> listApplicationsUserCanRead();
    List<ApplicationTO> listApplications(String appAttrNameFilter, String appAttrValueFilter);
    List<ApplicationTO> listApplicationsOfUser(int userId);
    List<ApplicationTO> listApplicationsOfGroup(int groupId);
    List<AttributeTO> listAttributesOfApplication(int appId);
    ImplementationTO loadImplementation(int impId);
    List<ImplementationTO> listImplementationsOfApplication(int appId);
    List<ImplementationTO> listImplementationsOfPlatform(int platId);
    List<ImplementationTO> listImplementationsOfValidator(int userId);
    List<ImplementationTO> listImplementations();
    List<ImplementationTO> listImplementationsUserCanRead();
    List<ImplementationTO> listImplementationsOfApplicationUserCanRead(int appId);
    List<ImplementationTO> listImplementations(String impAttrNameFilter, String impAttrValueFilter);
    List<CommentTO> listCommentsOfApplication(int appId);
    List<AttributeTO> listAttributesOfImplementation(int impId);
    List<CommentTO> listCommentsOfImplementation(int impId);
    List<ImpFileTO> listFilesOfImplementation(int impId);
    List<AppFileTO> listFilesOfApplication(int impId);
    List<ImplementationTO> listImplementationsReadyForValidation();
    List<GroupTO> listMyGroups();
    List<GroupTO> listMyOwnedGroups();
    List<ApplicationTO> listMyApplications();
    List<ImplementationTO> listMyValidatedImplementations();

    //these are for autocomplete
    List<String> filterAddUserToGroupUserLoginNames(Integer groupId, String query);
    List<String> filterAppGroupNames(String query);
    List<String> filterImpPlatformNames(String query);
    List<String> filterGroupLeaderLoginNames(String query);
    List<String> filterAppOwnerLoginNames(String query);
    List<String> filterAppAttrNames(String query);
    List<String> filterImpAttrNames(String query);
    public List<String> filterAppOwnerLoginNamesByGroup(String query, int groupId);

    //these are business methods
    UserTO createUser(String loginName, String password, String fullName, String organization, String emailAddr, Boolean isActive, Boolean isAdmin, Boolean isValidator) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    //UserTO updateUser(Integer userId, String fullName, String organization, String emailAddr, Boolean isUser,  Boolean isValidator, Boolean isAdmin) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    UserTO updateUser(UserTO user) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    UserTO changeUserPassword(Integer userId, String password) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    //UserTO deactivateUser(Integer userId) throws EntityNotFoundException, AuthorizationException;
    void deleteUser(Integer userId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException;
    UserTO findUserByLoginName(String loginName);
    UserTO findValidatorOfImp(int impId);
    String canUserReadUser(Integer userId, Integer targetUserId);
    String canUserCreateUsers(Integer userId);
    String canUserUpdateUser(Integer userId, Integer targetUserId);
    String canUserChangeUserPassword(Integer userId, Integer targetUserId);
    String canUserDeleteUser(Integer userId, Integer targetUserId);

    GroupTO createGroup(String groupName) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    GroupTO changeGroupLeader(Integer groupID, String leaderLoginName) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void addUserToGroup(Integer groupId, String userLoginName) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void removeUserFromGroup(Integer groupId, Integer userId) throws EntityNotFoundException, AuthorizationException;
    void deleteGroup(Integer groupId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException;
    String canUserReadGroup(Integer userId, Integer groupId);
    String canUserCreateGroups(Integer userId);
    String canUserChangeGroupLeader(Integer userId, Integer groupId);
    String canUserAddUsersToGroup(Integer userId, Integer groupId);
    String canUserRemoveUsersFromGroup(Integer userId, Integer groupId);
    String canUserDeleteGroup(Integer userId, Integer groupId);

    PlatformTO createPlatform(String platName, String description) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    PlatformTO createPlatform(String platName, String version, String description) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    PlatformTO updatePlatform(Integer platId, String description) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void deletePlatform(Integer platId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException;
    String canUserReadPlatform(Integer userId, Integer platId);
    String canUserCreatePlatforms(Integer userId);
    String canUserUpdatePlatform(Integer userId, Integer platId);
    String canUserDeletePlatform(Integer userId, Integer platId);

    ImpEmbedTO createImpEmbed(Integer impId, String extUserId, String extServiceId) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    void removeImpEmbed(Integer impId, String extUserId, String extServiceId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    Boolean isImpSelectedForEmbedding(Integer impId, String extUserID, String extServiceId);

    ApplicationTO createApp(String appName, String description, String groupName, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, EntityNotFoundException;
    ApplicationTO createApp(String appName, String description, Date created, Date updated, int groupId, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, EntityNotFoundException;
    ApplicationTO updateAppDetails(Integer appId, String description, String name) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ApplicationTO updateAppTimestamp(Integer appId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ApplicationTO updateAppAccess(Integer appId, String groupName, Boolean groupRead, Boolean othersRead, Boolean groupDownload, Boolean othersDownload, Boolean groupModify, Boolean published) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ApplicationTO changeAppOwner(Integer appId, String newOwnerLoginName) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void deleteApp(Integer appId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException, FileOperationFailedException;
    String canUserReadApplication(Integer userId, Integer appId);
    Boolean canUserReadDownloadApplication(Integer userId, Integer appId);
    String canPublicReadApplication(Integer appId);
    String canPublicReadImplementation(Integer impId);
    String canUserCreateApplications(Integer userId);
    String canUserUpdateAppDetails(Integer userId, Integer appId);
    String canUserUpdateAppAccess(Integer userId, Integer appId);
    String canUserPublishApp(Integer userId, Integer appId);
    String canUserChangeAppOwner(Integer userId, Integer appId);
    String canUserDeleteApplication(Integer userId, Integer appId);

    AttributeTO createAppAttr(Integer appId, String attrName, String attrValue) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException;
    AttributeTO updateAppAttr(Integer appAttrId, String attrValue) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void deleteAppAttr(Integer appAttrId) throws EntityNotFoundException, AuthorizationException;
    void updateAppAttrList(Integer appId, List<AttributeTO> aList) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void updateImpAttrList(Integer impId, List<AttributeTO> iList) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException;    String canUserReadAppAttr(Integer userId, Integer attrId);
    String canUserCreateAppAttrs(Integer userId, Integer appId);
    String canUserUpdateAppAttr(Integer userId, Integer attrId);
    String canUserDeleteAppAttr(Integer userId, Integer attrId);

    CommentTO createAppComm(Integer appId, String message) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    //void deleteAppComm(Integer appCommId) throws EntityNotFoundException, AuthorizationException;

    ImplementationTO createImp(Integer appId, String platformName, String version) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO createImp(Integer appId, int platformId, String version, Date created, Date updated) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO createImp(Integer appId, String platformName, String platformVersion, String version) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO updateImp(Integer impId, String platformName, String version) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO updateVersionedImp(Integer impId, String platformName, String platformVersion, String version) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO updateImpTimestamp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void viewImp(int id) throws EntityNotFoundException, AuthorizationException;
    void viewApp(int id) throws EntityNotFoundException, AuthorizationException;
    ImplementationTO submitImp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO validateImp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO failImp(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO markImpOld(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO markImpDeprecated(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO markImpCompromised(Integer impId) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    ImplementationTO toggleSubmittable(int _imp) throws EntityNotFoundException, AuthorizationException, ValidationFailedException;
    void deleteImp(Integer impId) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException;
    String canUserReadImp(Integer userId, Integer impId);
    String canUserCreateImps(Integer userId, Integer appId);
    String canUserUpdateImp(Integer userId, Integer impId);
    String canUserSubmitImp(Integer userId, Integer impId);
    String canUserValidateImp(Integer userId, Integer impId);
    String canUserFailImp(Integer userId, Integer impId);
    String canUserMarkImpOld(Integer userId, Integer impId);
    String canUserMarkImpDeprecated(Integer userId, Integer impId);
    String canUserMarkImpCompromised(Integer userId, Integer impId);
    String canUserDeleteImp(Integer userId, Integer impId);

    AttributeTO createImpAttr(Integer impId, String attrName, String attrValue) throws EntityAlreadyExistsException, EntityNotFoundException, ValidationFailedException, AuthorizationException;
    AttributeTO updateImpAttr(Integer impAttrId, String attrValue) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    void deleteImpAttr(Integer impAttrId) throws EntityNotFoundException, AuthorizationException;
    String canUserReadImpAttr(Integer userId, Integer attrId);
    String canUserCreateImpAttrs(Integer userId, Integer appId);
    String canUserUpdateImpAttr(Integer userId, Integer attrId);
    String canUserDeleteImpAttr(Integer userId, Integer attrId);
    long countAllViews(boolean imp);

    CommentTO createImpComm(Integer impId, String message) throws EntityNotFoundException, ValidationFailedException, AuthorizationException;
    //void deleteImpComm(Integer impCommId) throws EntityNotFoundException, AuthorizationException;

    ImpFileTO uploadImpFile(Integer impId, String pathName, InputStream source) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, FileOperationFailedException;
    AppFileTO uploadAppFile(Integer appId, String pathName, InputStream source) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, FileOperationFailedException;

    //TODO: download file
    ImpFile getImpFile(Integer impId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException;
    ImpFile getAppFile(Integer appId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException;
    void deleteImpFile(Integer impId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException;
    void deleteAppFile(Integer appId, String pathName) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException;
    String canUserUploadImpFile(Integer userId, Integer impId);
    String canUserDownloadImpFile(Integer userId, Integer impId);
    String canUserDeleteImpFiles(Integer userId, Integer impId);
    String canUserUploadAppFile(Integer userId, Integer appId);
    String canUserDownloadAppFile(Integer userId, Integer appId);
    String canUserDeleteAppFiles(Integer userId, Integer appId);
    String canPublicDownloadImpFile(Integer impId);
    String canPublicDownloadAppFile(Integer appId);

    //these are for workflow engines
    List<Platform> listWorkflowEngines();
    List<String> listWorkflowEnginesNoDupes();
    String canUserReadWorkflowEngine(int userId, int appId);
    String canPublicReadWorkflowEngine(int appId);
    Platform getWEById(int idWE);
    public String canUserCreateWorkflowEngines(int id);
    public Platform createWorkflowEngine(String nameWE, String version, String desc) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, WEBuildingException;
    public void deleteWE(Platform _we) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException;

    //these are for Workfow Engines Implementation
    public void updateWEImp(WEImplementation _WEImp) throws ValidationFailedException, AuthorizationException, WEBuildingException;
    public Collection<WEImplementation> listWEImplementation(Platform _we);
    public String canUserReadWEImplementation(int userId, int weImpId);
    public String canPublicReadWEImplementation(int weImpId);
    public String canUserCreateWEImplementations(int id);
    public String canUserModifyWEImplementations(int id, WEImplementation weimp);
    public boolean isWEImpOwner(int callerId, WEImplementation weimp);
    public WEImplementation createWEImp(String nameWEImp, String descriptionWEImp, boolean preDeployed, String prefixData, Platform _we, BeInstance _be, WEUploadedFile _zip, String _shell, WEUploadedFile _shellFile) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, WEBuildingException;
    public WEImplementation dupeWEImp(WEImplementation _wei, String name) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, WEBuildingException;
    public String canPublicDeleteWEImplementation(int weImpId);
    public String canUserDeleteWEImplementation(int CallerId, int weImpId);
    public void deleteWEImplementation(int weImpId) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException;
    public List<WEImplementation> getWEImpByWE(Platform _we);
    public List<WEImplementation> getAfftectedWEImps(WEUploadedFile _wefile);
    public void toggleWEImpEnabled(WEImplementation imp) throws AuthorizationException, ValidationFailedException;


    /*
     * WEFiles functions
     */
    public String canUserDownloadWEFiles(int callerId);
    public String canUserModifyWEFiles(int callerId);
    public WEUploadedFile uploadWEFile(String name, String desc, Platform _we , String pathName, InputStream source, boolean isData) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, FileOperationFailedException;
    //public WEUploadedFile uploadWEFile(String name, String desc, String vers, String pathName, InputStream source) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException, FileOperationFailedException;
    //public WEUploadedFile uploadFileAttribs(Platform _we, String fileName, String desc, String vers, WEUploadedFile _uf) throws EntityNotFoundException, EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    public File getWEFile(int idWE, String filename) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException;
    public Collection<WEUploadedFile> listFilesByWE(Platform _we, boolean isData);
    public WEUploadedFile getWEFileById(int id);
    public Platform deleteWEFiles(WEUploadedFile _weFile) throws EntityNotFoundException, AuthorizationException, FileOperationFailedException, ValidationFailedException;

    //these are for backend management
    public List<Backend> listBackendNames();
    public List<Backend> listBackendAll();
    public String canUserCreateBackends(int callId);
    public Backend createBackend(String name, String desc, Collection<JobManager> _jList) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    public void updateBackend(Backend _be, Collection<JobManager> _jm) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    public void deleteBackend(Backend _be) throws EntityNotFoundException, NotSafeToDeleteException, AuthorizationException;
    public Backend getBackendById(int _bId);
    public BeInstance getBEInstanceById(int _bId);
    public String canUserModifyBEInst(int callerId, BeInstance beinst);
    public boolean isBEInstOwner(int callerId, BeInstance beinst);
    public BeInstance createBeInstance(String _name, Backend _idBackend, String _site, String _backendOut, String _backendErr, String _jobManager, JobType _jobType, OperatingSystems _idOS, String resource, int callerId ) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    public BeInstance dupeBeInstance(BeInstance _bei, String _name) throws EntityAlreadyExistsException, ValidationFailedException, AuthorizationException;
    public BeInstance updateBEI(BeInstance _beI) throws EntityNotFoundException, AuthorizationException, ValidationFailedException;
    public void deleteBEI(BeInstance _beI) throws EntityNotFoundException, AuthorizationException, ValidationFailedException;
    public List<BeInstance> getBeInstanceAll();

    //these are for JobManager management
    public List<JobManager> listJobManagers();
    public List<JobManager> getJobManagersFromString(ArrayList<String> selectedJobManagers);

    //these are for job type management
    public List<JobType> listJobTypes();
    public JobType getJobTypeFromString(int _jId);

    //these are for operating systems management
    public List<OperatingSystems> listOperatingSystemsNames();
    public int getOsIdByName(String name);
    public OperatingSystems getOperatingSystemById(int id);
    public String canUserModifyOS(int callId);
    public OperatingSystems createOS(String name, String version) throws ValidationFailedException, AuthorizationException, EntityAlreadyExistsException;
    public OperatingSystems updateOS(OperatingSystems _os) throws ValidationFailedException, AuthorizationException, EntityAlreadyExistsException;
    public void deleteOS(OperatingSystems _os) throws ValidationFailedException, AuthorizationException, EntityNotFoundException;

    //functions for the ratings management
    public void removeSingleRating(int impId, int userId) throws AuthorizationException, EntityNotFoundException;
    public void deleteRatingsOfImp(int impId) throws ValidationFailedException, AuthorizationException, EntityNotFoundException;
    public List<Ratings> getRatingsByImpId(int impId) throws AuthorizationException, EntityNotFoundException;
    public void insertOrUpdateRating(Ratings r) throws AuthorizationException, EntityNotFoundException, ValidationFailedException;
    public User findUserByName(String loginName);
    public Implementation getImpById(int id);


    // function for the submission service
    public List<ImplShort> listValidatedImplementations(String extServiceId,
            String extUserId)
            throws IllegalArgumentException, DatabaseProblemException;
    public List<String> listWorkflowEngineInstances(String implName)
            throws IllegalArgumentException, DatabaseProblemException;
    public List<Parameter> listParametersImpl(String implName)
            throws IllegalArgumentException, DatabaseProblemException;
    public ImplJSDL loadImplementationJSDL(String repositoryURL, String implName)
            throws IllegalArgumentException, DatabaseProblemException;
    public WorkflowEngineInstance loadWEIJSDL(String repositoryURL,
            EngineData engineData)
            throws IllegalArgumentException, DatabaseProblemException;
    public BeInstance loadBackendInstance(String implName, String engineInstance)
            throws IllegalArgumentException, DatabaseProblemException;
}
