/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit;

import org.shiwa.repository.toolkit.exceptions.ForbiddenException;
import org.shiwa.repository.toolkit.exceptions.ItemExistsException;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;
import org.shiwa.repository.toolkit.exceptions.UnauthorizedException;
import org.shiwa.repository.toolkit.transferobjects.*;

import javax.ejb.Local;
import java.util.List;
import uk.ac.wmin.edgi.repository.common.AuthorizationException;


/**
 *
 * @author kukla
 */
@Local
public interface ToolkitInterface {

    /**
     * Provides a list of workflow summaries the given user can read or modify
     * @param userId users id
     * @param modify whether to list workflows that the user can modify or the workflows the user can read
     * @return workflowSymmaryList providing key info about the workflow
     * @throws UnauthorizedException if user does not exist
     */
    public List<WorkflowSummaryRTO> listWorkflows(int userId, boolean modify) throws UnauthorizedException;

    /**
     * Provides a list of implementation summaries the given user can download
     * @param userId users id
     * @param workflowId the id of the workflow of which implementations are to be listed
     * @return implementationSymmaryList providing key info about the implementations
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if the user does not have access to the
     * @throws NotFoundException if the workflow does not exist
     */
    public List<ImplementationSummaryRTO> listImplementations(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Provides a list of configuration summaries the given user can download
     * @param userId users id
     * @param workflowId the id of the workflow of which configurations are to be listed
     * @return configurationSymmaryList providing key info about the configurations
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if the user does not have access to the
     * @throws NotFoundException if the workflow does not exist
     */
    public List<ConfigurationSummaryRTO> listConfigurations(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Retrieves with the id of the workflow the given implementation belongs to
     * @param userId users id
     * @param implementationId of the given implementation
     * @return workflow id
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot read the given workflow
     * @throws NotFoundException if there is no implementation with the given id
     */
    public int getWorkflowId(int userId, int implementationId) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Retrieves with the signature of the given workflow
     * @param userId users id
     * @param workflowId of the given workflow
     * @return workflow signature
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot read the given workflow
     * @throws NotFoundException if there is no workflow with the given id
     */
    public SignatureRTO getSignature(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Creates a new workflow
     * @param userId users id
     * @param workflow provides all info about the workflow, but no implementations
     * @return id of the newly created workflow
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot create workflow
     * @throws ItemExistsException if there is a workflow in the repository with the same signature or workflow name
     */
    public int createWorkflow(int userId, WorkflowRTO workflow) throws UnauthorizedException, ForbiddenException, ItemExistsException;

    /**
     * Creates a new implementation under the workflow specified within the implementation object in workflowId
     * @param userId users id
     * @param implementation RTO to be created
     * @return id of the newly created implementation
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot add implementation to the given workflow
     * @throws NotFoundException it there is not workflow with the given id
     * @throws ItemExistsException if there is an implementation in the repository under the given workflow with the same engineId,
     * engineVersion and implementation version
     */
    public int createImplementation(int userId, ImplementationRTO implementation) throws UnauthorizedException, ForbiddenException, NotFoundException, ItemExistsException;

    /**
     * Adds a data configuration to the given workflow
     * @param userId users id
     * @param workflowId parent workflow
     * @param configuration RTO to be created
     * @return id of the newly created configuration
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot add configuration to the given workflow
     * @throws NotFoundException if there is no workflow with the given id
     */
    public int createConfiguration(int userId, int workflowId, ConfigurationRTO configuration) throws UnauthorizedException, ForbiddenException, NotFoundException ;

    /**
     * Updates given implementation, where the implementation to update is identified by workflowId, enginId, engineVersion
     * and implementation version
     * @param userId users id
     * @param implementation RTO to be updated
     * @return id of the updated implementation
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot update implementation
     * @throws NotFoundException if there is no implementation in the repository under the given workflow with the same engineId,
     * engineVersion and implementation version
     */
    public int updateImplementation(int userId, ImplementationRTO implementation) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Retrieves the workflow with the give id
     * @param userId users id
     * @param workflowId workflows id
     * @return workflow
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot read the given workflow
     * @throws NotFoundException if there is no such workflow
     */
    public WorkflowRTO getWorkflow(int userId, int workflowId) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Retrieves a list of configurations of the given workflow with the specified ids
     * @param userId users id
     * @param workflowId defines the workflow of which configurations are to be retrieved
     * @param configurationIds list of configuration ids
     * @return list of configurations
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot read the given workflow
     * @throws NotFoundException if there is no workflow with the given workflowId or no matching configurations
     */
    public List<ConfigurationRTO> getConfigurations(int userId, int workflowId, List<Integer> configurationIds) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Retrieves a list of implementations with the specified ids
     * @param userId users id
     * @param implementationIds implementation ids
     * @return list of implementations
     * @throws UnauthorizedException if user does not exist
     * @throws ForbiddenException if user cannot read the given implementations
     * @throws NotFoundException if there are no matching implementations
     */
    public List<ImplementationRTO> getImplementations(int userId, List<Integer> implementationIds) throws UnauthorizedException, ForbiddenException, NotFoundException;

    /**
     * Retrieves with the engine object that has the given engine name and version
     * @param userId users id
     * @param name engine name
     * @param version engine version
     * @return EngineRTO
     * @throws UnauthorizedException user does not have access to repository
     * @throws NotFoundException engine cannot be found
     */
    public EngineRTO getEngine(int userId, String name, String version) throws UnauthorizedException, NotFoundException;

    /**
     * Retrieves with the engine object that belongs to the given engine id
     * @param userId users id
     * @param engineId engine id
     * @return EngineRTO
     * @throws UnauthorizedException user does not have access to repository
     * @throws NotFoundException engine cannot be found
     */
    public EngineRTO getEngine(int userId, int engineId) throws UnauthorizedException, NotFoundException;

    /**
     * Retrieves with a list of engine objects that belong to the given engine ids
     * @param userId users id
     * @param engineIds list
     * @return EngineRTO list
     * @throws UnauthorizedException user does not have access to repository
     * @throws NotFoundException engines cannot be found
     */
    public List<EngineRTO> getEngines(int userId, List<Integer> engineIds) throws UnauthorizedException, NotFoundException;

    /**
     * In the case of a new portal/engine version - create an appropriate engine in the repository.
     * @param engName name
     * @param engVersion version
     * @return success
     */
    public EngineRTO createEngine(String engName, String engVersion) throws AuthorizationException, ForbiddenException;

    /**
     * Retrieves with a list of all provided workflow attribute values for a given attribute name
     * @param userId users id
     * @param attrName attribute name
     * @return String list
     * @throws UnauthorizedException user does not have access to repository
     * @throws NotFoundException attributes cannot be found
     */
    public List<String> getWFAttributeValues(int userId, String attrName) throws UnauthorizedException, NotFoundException;    /**

    /**
     * Retrieves a list of all workflow groups the user is a member of
     * @param userId users id
     * @return GroupRTO list of groups
     * @throws UnauthorizedException user does not have access to repository
     * @throws NotFoundException user cannot be found
     */
    public List<GroupRTO> getUsersGroups(int userId) throws UnauthorizedException, NotFoundException;
}