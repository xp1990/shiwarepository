/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.shiwa.repository.submission.helpers.SubmissionServiceTools;
import org.shiwa.repository.submission.interfaces.SubmissionService;
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
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@WebService(endpointInterface = "org.shiwa.repository.submission.interfaces.SubmissionService",
        serviceName = "SubmissionService", portName = "SubmissionService")
public class SubmissionServiceImpl implements SubmissionService {

    @EJB
    private ApplicationFacadeLocal facade;
    @Resource
    private WebServiceContext context;

    @Override
    public List<ImplShort> getAllPublicValidatedImplementations(
            String extServiceId, String extUserId)
            throws UnauthorizedException, DatabaseProblemException,
            ForbiddenException {
        // check authorization, throw exception if wrong
        if (!SubmissionServiceTools.isValid(extServiceId, extUserId)) {
            throw new UnauthorizedException("Invalid user detected");
        }

        List<ImplShort> listOfItems = new ArrayList<ImplShort>();
        try {
            listOfItems = facade.listValidatedImplementations(extServiceId,
                    extUserId);
        } catch (IllegalArgumentException ex) {
            throw new ForbiddenException(ex);
        }

        return listOfItems;
    }

    @Override
    public List<String> getAllWorkflowEngineInstances(String implName)
            throws DatabaseProblemException, ForbiddenException {
        try {
            return facade.listWorkflowEngineInstances(implName);
        } catch (IllegalArgumentException ex) {
            throw new ForbiddenException(ex);
        }
    }

    @Override
    public List<Parameter> getLCParameters(String implName)
            throws DatabaseProblemException, ForbiddenException {
        try {
            return facade.listParametersImpl(implName);
        } catch (IllegalArgumentException ex) {
            throw new ForbiddenException(ex);
        }
    }

    @Override
    public ImplJSDL getFullImplForJSDL(String implName)
            throws DatabaseProblemException, ForbiddenException, NotFoundException {
        // build server url
        HttpServletRequest servletRqt = (HttpServletRequest) context.
                getMessageContext().get(MessageContext.SERVLET_REQUEST);
        String repositoryURL = SubmissionServiceTools.buildRepositoryURL(servletRqt);

        // fill out all the class
        try {
            return facade.loadImplementationJSDL(repositoryURL, implName);
        } catch (IllegalArgumentException ex) {
            throw new ForbiddenException(ex);
        }
    }

    @Override
    public WorkflowEngineInstance getFullWEIForJSDL(EngineData engine)
            throws NotFoundException, DatabaseProblemException, ForbiddenException {
        HttpServletRequest servletRqt = (HttpServletRequest) context.
                getMessageContext().get(MessageContext.SERVLET_REQUEST);
        String repositoryURL = SubmissionServiceTools.buildRepositoryURL(servletRqt);

        // fill out all the class
        try {
            return facade.loadWEIJSDL(repositoryURL, engine);
        } catch (IllegalArgumentException ex) {
            throw new ForbiddenException(ex);
        }
    }

    @Override
    public BeInstance getBackendInstance(String implName, String engineInstance)
            throws DatabaseProblemException, ForbiddenException {
        try {
            return facade.loadBackendInstance(implName, engineInstance);
        } catch (IllegalArgumentException ex) {
            throw new ForbiddenException(ex);
        }
    }
}
