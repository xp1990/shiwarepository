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
 *
 * @author glassfish
 */
public class SubmissionRequests {

    private static final Logger logger = Logger.getLogger("SUBMISSION_REQUESTS");

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
        String rqt = "SELECT CONCAT(app.name, '#', i.version) as name, "
                + "(SELECT a.attr_value FROM imp_attribute a "
                + "WHERE a.imp_id=i.id AND a.name='description') as description, "
                + "IF(EXISTS(SELECT e.imp_id FROM imp_embed e "
                + "WHERE e.imp_id=i.id AND e.ext_service_id=?1 "
                + "AND e.ext_user_id=?2), 'true', 'false') as selected "
                + "FROM implementation i, application app "
                + "WHERE i.submittable='1' AND app.id=i.app_id "
                + "AND EXISTS(SELECT idWEImp FROM we_implementation wei "
                + "WHERE wei.idWE = i.plat_id) = '1'";

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

    public static List<String> getAllWorkflowEngineInstances(int implId, EntityManager em)
            throws DatabaseProblemException, IllegalArgumentException {
        logger.fine("getAllWorkflowEngineInstance");
        logger.log(Level.FINE, "Implementation ID: {0}", implId);

        String rqt = "SELECT wei.nameWEImp FROM "
                + "implementation i, platform p, we_implementation wei "
                + "WHERE i.id=?1 AND i.plat_id=p.id AND p.id=wei.idWE";

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

    public static List<ImpAttribute> getAllParameters(int implId, EntityManager em)
            throws DatabaseProblemException, IllegalArgumentException {
        logger.fine("getAllParameters");
        logger.log(Level.FINE, "Implementation ID: {0}", implId);

        Query queryAttr = em.createNamedQuery(
                "ImpAttribute.findByImpIDAndName");
        queryAttr.setParameter("impId", implId);
        queryAttr.setParameter("name", "execution.parameters.%");
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
