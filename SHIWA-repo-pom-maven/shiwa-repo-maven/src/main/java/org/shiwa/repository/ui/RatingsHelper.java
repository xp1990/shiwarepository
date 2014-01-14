/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.wmin.edgi.repository.entities.Ratings;
import uk.ac.wmin.edgi.repository.common.AuthorizationException;
import uk.ac.wmin.edgi.repository.common.EntityNotFoundException;
import uk.ac.wmin.edgi.repository.common.ValidationFailedException;
import uk.ac.wmin.edgi.repository.entities.Implementation;
import uk.ac.wmin.edgi.repository.entities.User;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author root
 */
public class RatingsHelper {
    ApplicationFacadeLocal facade;


    public RatingsHelper() {
    }

    public void setAf(ApplicationFacadeLocal af){
        facade = af;
    }

    public void deleteRatingsByVersion(final String versionID) {
        deleteRatingsByVersion(Integer.valueOf(versionID));
    }

    public void deleteRatingsByVersion(final Integer versionID) {
        try {
            facade.deleteRatingsOfImp(versionID);
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.INFO, "Successfully deleted rating for ImpId: " + versionID);
        } catch (ValidationFailedException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthorizationException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Ratings> getRatingsForVersion(String versionId) {

        ArrayList<Ratings> temp = new ArrayList<Ratings>();

        try {
            //Logger.getLogger(RatingsHelper.class.getName()).log(Level.INFO, "Ratings retrieved for for ImpId: " + versionId);
            return facade.getRatingsByImpId(Integer.parseInt(versionId));
        } catch (AuthorizationException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return temp;
    }

    public void insertOrUpdateRatings(Ratings r) {
        try {
            facade.insertOrUpdateRating(r);
        } catch (AuthorizationException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ValidationFailedException ex) {
            Logger.getLogger(RatingsHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getUser(String name){
        return facade.findUserByName(name);
    }

    public Implementation getImpById(int id){
        return facade.getImpById(id);
    }
}
