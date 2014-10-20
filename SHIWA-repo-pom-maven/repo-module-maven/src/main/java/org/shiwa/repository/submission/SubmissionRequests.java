/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.shiwa.repository.submission.objects.EngineData;
import uk.ac.wmin.edgi.repository.common.DatabaseProblemException;
import uk.ac.wmin.edgi.repository.entities.ImpAttribute;
import uk.ac.wmin.edgi.repository.entities.Implementation;
import uk.ac.wmin.edgi.repository.entities.Platform;

/**
 * Class interacting with the database in order to retrieve data for the 
 * SHIWA Submission Service.
 *
 * @author glassfish
 */
public class SubmissionRequests {

    private static final Logger logger = Logger.getLogger("SUBMISSION_REQUESTS");

    /**
     * Get the list of all submission enabled implementations as a list of objects.
     * Each object has three fields (name of the implementation, description of 
     * the implementation, if the implementation has been selected 
     * for the portal id given and the user id given)
     * @param extServiceId id of the portal
     * @param extUserId id of the user
     * @param em database manager
     * @return list of objects representing the implementation data
     * @throws DatabaseProblemException
     * @throws IllegalArgumentException 
     */
    public static List<Object[]> getAllValidatedImplementations(String extServiceId,
            String extUserId, EntityManager em)
            throws DatabaseProblemException, IllegalArgumentException {
        logger.fine("getAllValidatedImplementations");

        if (extServiceId == null || extUserId == null) {
            logger.log(Level.SEVERE, "null data detected");
            throw new IllegalArgumentException("Incorrect external data provided");
        }

        logger.log(Level.FINE, "ExtServiceId: {0}", extServiceId);
        logger.log(Level.FINE, "ExtUserId: {0}", extUserId);
        //TODO: see again this function when validated system working and
        // see the management of public implementation only
        // maybe set up validation(replacing gemlca deployment) as imp_attribute?

        String RESULT_CONCAT = "CONCAT(app.name, '#', i.version) as name";
        String RESULT_DESCRIPTION = "(SELECT a.attr_value FROM imp_attribute a "
                + "WHERE a.imp_id=i.id AND a.name='description') as description";
        String RESULT_SELECTED = "IF(EXISTS(SELECT e.imp_id FROM imp_embed e "
                + "WHERE e.imp_id=i.id AND e.ext_service_id=?1 "
                + "AND e.ext_user_id=?2), 'true', 'false') as selected";
        String TABLES = "implementation i, application app";
        String CONDITION_GENERAL = "i.submittable='1' AND app.id=i.app_id";
        String CONDITION_WEI = "EXISTS(SELECT wei.idWEImp FROM we_implementation wei "
                + "WHERE wei.idWE=i.plat_id AND wei.enabled='1')='1'";
        String CONDITION_WE = "(SELECT p.submittable FROM platform p "
                + "WHERE p.id=i.plat_id)='1'";

        String rqt = "SELECT " + RESULT_CONCAT + ", " + RESULT_DESCRIPTION + ", "
                + RESULT_SELECTED + " FROM " + TABLES + " "
                + "WHERE " + CONDITION_GENERAL + " AND " + CONDITION_WEI
                + " AND " + CONDITION_WE;

        Query queryImplShort = em.createNativeQuery(rqt);
        queryImplShort.setParameter(1, extServiceId);
        queryImplShort.setParameter(2, extUserId);

        try {
            return queryImplShort.getResultList();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "All validated implementations exception", ex);
            throw new DatabaseProblemException("List all validated "
                    + "implementations problem: " + ex.getMessage(), ex);
        }
    }

    /**
     * Get the list of all workflow engine implementation names for an 
     * implementation.
     * @param implId id of the implementation
     * @param em database manager
     * @return list of workflow engine implementation names
     * @throws DatabaseProblemException
     * @throws IllegalArgumentException 
     */
    public static List<String> getAllWorkflowEngineInstances(int implId, EntityManager em)
            throws DatabaseProblemException, IllegalArgumentException {
        logger.fine("getAllWorkflowEngineInstance");
        logger.log(Level.FINE, "Implementation ID: {0}", implId);

        String rqt = "SELECT wei.nameWEImp FROM "
                + "implementation i, platform p, we_implementation wei "
                + "WHERE i.id=?1 AND i.plat_id=p.id AND p.id=wei.idWE AND "
                + "p.submittable='1' and wei.enabled='1'";

        Query queryWEI = em.createNativeQuery(rqt);
        queryWEI.setParameter(1, implId);

        try {
            return queryWEI.getResultList();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "All workflow engine instances exception", ex);
            throw new DatabaseProblemException("List all workflow engine "
                    + "instances problem: " + ex.getMessage(), ex);
        }
    }

    /**
     * Get the list of all parameters fixed and not fixed for an implementation
     * @param implId id of the implementation
     * @param em database manager
     * @return list of all parameters as ImpAttribute
     * @throws DatabaseProblemException
     * @throws IllegalArgumentException 
     */
    public static List<ImpAttribute> getAllParameters(int implId, EntityManager em)
            throws DatabaseProblemException, IllegalArgumentException {
        logger.fine("getAllParameters");
        logger.log(Level.FINE, "Implementation ID: {0}", implId);

        Query queryAttr = em.createNamedQuery(
                "ImpAttribute.findByImpIDAndName");
        queryAttr.setParameter("impId", implId);
        queryAttr.setParameter("name", "Submission Execution Node.parameters.%");
        queryAttr.setHint("eclipselink.refresh", "true");
        queryAttr.setHint("eclipselink.refresh.cascade", "CascadeAllParts");

        try {
            return queryAttr.getResultList();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "All parameters exception", ex);
            throw new DatabaseProblemException("List all non fixed parameters "
                    + "problem: " + ex.getMessage(), ex);
        }
    }

    /**
     * Get the requested implementation
     * @param implId id of the implementation
     * @param em database manager
     * @return implementation requested
     * @throws DatabaseProblemException
     * @throws IllegalArgumentException 
     */
    public static Implementation getImplementation(int implId, EntityManager em)
            throws DatabaseProblemException, IllegalArgumentException {
        logger.fine("getImplementation");
        logger.log(Level.FINE, "Implementation ID: {0}", implId);

        Query query = em.createNamedQuery("Implementation.loadImplementationFull");
        query.setParameter("id", implId);
        query.setHint("eclipselink.refresh", "true");
        query.setHint("eclipselink.refresh.cascade", "CascadeAllParts");

        try {
            return (Implementation) query.getSingleResult();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Implementation exception", ex);
            throw new DatabaseProblemException("List full validated "
                    + "implementation problem: " + ex.getMessage(), ex);
        }
    }

    /**
     * Get the workflow engine from its version and name (engineData contains
     * those two information)
     * @param engineData name, version of the workflow engine
     * @param em database manager
     * @return full workflow engine as a Platform
     * @throws IllegalArgumentException
     * @throws DatabaseProblemException 
     */
    public static Platform getWorkflowEngineData(EngineData engineData,
            EntityManager em)
            throws IllegalArgumentException, DatabaseProblemException {
        logger.fine("getWorkflowEngineData");

        if (engineData == null || engineData.getEngineName() == null
                || engineData.getEngineVersion() == null
                || engineData.getEngineInstanceName() == null) {
            logger.log(Level.SEVERE, "null data detected");
            throw new IllegalArgumentException("Incorrect engine data provided");
        }

        logger.log(Level.FINE, "Engine name: {0}", engineData.getEngineName());
        logger.log(Level.FINE, "Engine version: {0}", engineData.getEngineVersion());

        Query queryWE = em.createNamedQuery("Platform.findByNameAndVersion");
        queryWE.setParameter("name", engineData.getEngineName());
        queryWE.setParameter("version", engineData.getEngineVersion());
        queryWE.setHint("eclipselink.refresh", "true");
        queryWE.setHint("eclipselink.refresh.cascade", "CascadeAllParts");

        try {
            return (Platform) queryWE.getSingleResult();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Workflow engine data exception", ex);
            throw new DatabaseProblemException("Problem detected with "
                    + "platform given (" + engineData.getEngineName() + ")", ex);
        }
    }

    /**
     * Get the workflow engine associated to an implementation
     * @param implId id of the implementation
     * @param em database manager
     * @return workflow engine as a Platform
     * @throws IllegalArgumentException
     * @throws DatabaseProblemException 
     */
    public static Platform getWorkflowEngineData(int implId, EntityManager em)
            throws IllegalArgumentException, DatabaseProblemException {
        logger.fine("getWorkflowEngineData");
        logger.log(Level.FINE, "Implementation ID: {0}", implId);

        Implementation implementation = em.find(Implementation.class, implId);

        try {
            return implementation.getPlatform();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Workflow engine data exception", ex);
            throw new DatabaseProblemException("Problem detected with "
                    + "implementation id given (" + implId + ")", ex);
        }
    }

    /**
     * Get the id of an implementation from its name 
     * (name is: applicationName + # + implementationVersion)
     * @param implName name of the implementation
     * @param em database manager
     * @return id of the implementation
     * @throws IllegalArgumentException
     * @throws DatabaseProblemException 
     */
    public static int getValidatedImplementationID(String implName,
            EntityManager em) throws IllegalArgumentException,
            DatabaseProblemException {
        logger.fine("getValidatedImplementationID");
        logger.log(Level.FINE, "Implementation name: {0}", implName);

        int index = implName.lastIndexOf("#");

        if (index == -1) {
            throw new IllegalArgumentException("Wrong implementation name given");
        }
        String appName = implName.substring(0, index);
        String implVersion = implName.substring(index + 1);

        Query query = em.createNamedQuery(
                "Implementation.getValidatedImpIDByAppNameAndVersion");
        query.setParameter("appName", appName);
        query.setParameter("version", implVersion);
        query.setHint("eclipselink.refresh", "true");
        query.setHint("eclipselink.refresh.cascade", "CascadeAllParts");

        try {
            return (Integer) query.getSingleResult();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Validated implementation id exception", ex);
            throw new DatabaseProblemException("Problem detected with "
                    + "implementation given (" + implName + ")", ex);
        }
    }
}
