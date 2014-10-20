/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.interfaces;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.shiwa.repository.submission.objects.ImplShort;
import org.shiwa.repository.submission.objects.JSDL.ImplJSDL;
import org.shiwa.repository.submission.objects.Parameter;
import org.shiwa.repository.submission.objects.EngineData;
import org.shiwa.repository.submission.objects.workflowengines.WorkflowEngineInstance;
import org.shiwa.repository.toolkit.exceptions.ForbiddenException;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;
import org.shiwa.repository.toolkit.exceptions.UnauthorizedException;
import org.shiwa.repository.toolkit.wfengine.BeInstance;
import uk.ac.wmin.edgi.repository.common.DatabaseProblemException;

/**
 * Interface declaring all methods for the web service communicating with the 
 * Submission Service.
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(name = "SubmissionService")
public interface SubmissionService {

    /**
     * ImplShort name: NameOfApplication#VersionOfImplementation description:
     * WorkflowEngineName selected: is this implementation selected or not?
     *
     * The implementation has to own a correct execution node to be added
     * (validated)
     * 
     * Usage: WSCodeListService from the SHIWA Submission Service
     *
     * @param extServiceId service using
     * @param extUserId user
     * @return List containing a resume of all publicly visible validated
     * implementations
     * @throws UnauthorizedException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     */
    @WebMethod(operationName = "getAllPublicValidatedImplementations")
    public List<ImplShort> getAllPublicValidatedImplementations(
            @WebParam(name = "extServiceId") String extServiceId,
            @WebParam(name = "extUserId") String extUserId)
            throws UnauthorizedException, DatabaseProblemException,
            ForbiddenException;

    /**
     * From an implementation specific name, retrieve the workflow engine
     * associated and return all workflow engine instances related
     * (implementation -> workflow engine -> all instances)
     *
     * Usage: WSCodeListService from the SHIWA Submission Service
     * 
     * @param implName this form: NameOfTheApplication#VersionOfImplementation
     * @return list of all workflow engine instances related to the specific
     * implementation
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     */
    @WebMethod(operationName = "getAllWorkflowEngineInstances")
    public List<String> getAllWorkflowEngineInstances(
            @WebParam(name = "implName") String implName)
            throws DatabaseProblemException, ForbiddenException;

    /**
     * Get the list of all non-fixed parameters for a given implementation.
     * Parameter is a special class and the algorithm given in the function
     * implementation has to be followed. 7 steps for a parameter non-fixed
     * under a String every step appended and separated by a coma: 
     * - name/title: String, 
     * - mandatory or not: Yes/No, 
     * - file of not: File/Commandline, 
     * - value: file name or value, 
     * - input or not: Input/Output, 
     * - regex associated: , -> has to appear but empty 
     * - default or not: Default/NoDefault -> by default, Default
     *
     * Usage: WSCodeListService from the SHIWA Submission Service
     * 
     * @param implName this form: NameOfTheApplication#VersionOfImplementation
     * @return list of all non-fixed parameters
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     */
    @WebMethod(operationName = "getLCParameters")
    public List<Parameter> getLCParameters(
            @WebParam(name = "implName") String implName)
            throws DatabaseProblemException, ForbiddenException;

    /**
     * Get a complete implementation data useful for a JSDL generation. It
     * contains data concerning the implementation and the execution node well
     * configured (a new exception could be thrown such as
     * "NoConfiguredImplementationException".
     *
     * Usage: WSExecutionService from the SHIWA Submission Service
     * 
     * @param implName this form: NameOfTheApplication#VersionOfImplementation
     * @return complete implementation data for a JSDL creation
     * @throws NotFoundException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     */
    @WebMethod(operationName = "getFullImplForJSDL")
    public ImplJSDL getFullImplForJSDL(
            @WebParam(name = "implName") String implName)
            throws NotFoundException, DatabaseProblemException, ForbiddenException;

    /**
     * Get a complete workflow engine instance data useful for a JSDL
     * generation. It contains data related to the workflow engine and the
     * instance itself, the middleware and deployment configuration.
     *
     * Usage: WSExecutionService from the SHIWA Submission Service
     * 
     * @param engine engine information (name, version and instance)
     * @return complete workflow engine instance for a JSDL creation
     * @throws NotFoundException
     * @throws DatabaseProblemException
     * @throws ForbiddenException
     */
    @WebMethod(operationName = "getFullWEIForJSDL")
    public WorkflowEngineInstance getFullWEIForJSDL(
            @WebParam(name = "engine") EngineData engine)
            throws NotFoundException, DatabaseProblemException, ForbiddenException;

    /**
     * Get all information about a backend instance for a given implementation
     * having a specific workflow engine implementation. With a given 
     * implementation, you can find the associated workflow engine.
     * With the workflow engine, you can have the list of workflow engine 
     * implementations and extract the one needed.
     * 
     * Usage: WSCodeListService from the SHIWA Submission Service
     * 
     * @param implName name of the implementation
     * @param engineInstance name of the workflow engine implementation
     * @return backend instance associated to the given workflow engine 
     * implementation
     * @throws DatabaseProblemException
     * @throws ForbiddenException 
     */
    @WebMethod(operationName = "getBackendInstance")
    public BeInstance getBackendInstance(
            @WebParam(name = "implName") String implName,
            @WebParam(name = "engineInstance") String engineInstance)
            throws DatabaseProblemException, ForbiddenException;
}
