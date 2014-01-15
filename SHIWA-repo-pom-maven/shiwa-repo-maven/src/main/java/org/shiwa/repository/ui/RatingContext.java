/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import uk.ac.wmin.edgi.repository.entities.Implementation;
import uk.ac.wmin.edgi.repository.entities.Ratings;
import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author michnie
 */
public class RatingContext {
    private RatingsHelper ratingsHelper = new RatingsHelper();
    private Map<String, Ratings> ratingMap = new HashMap<String, Ratings>();
    private Double rating = 0.0;
    private List<Ratings> ratings = new ArrayList<Ratings>();
    private Map<Integer, Double> userRatings = new HashMap<Integer, Double>();

    private RatingContext(){
    }

    public static RatingContext create() {
        return new RatingContext();
    }

    public Double getAVGRate(String impid) {
        List<Ratings> results = ratingsHelper.getRatingsForVersion(impid);

        ratings = results;

        if (results.isEmpty()) {
            return 0.0;
        }
        Double sum = 0.0;
        for (Ratings r : ratings) {
            sum += r.getRate();
        }
        rating = sum / ratings.size();
        return rating;
    }

    public void setAf(ApplicationFacadeLocal af){
        ratingsHelper.setAf(af);
    }

    public Double getRating() {
        return rating;
    }

    public Integer getNumberOfRatings(String impid) {
        getAVGRate(impid);
        return ratings.size();
    }

    public void rateDone(int imp) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest httpServletRequest = (HttpServletRequest) context.getExternalContext().getRequest();
        Ratings r = new Ratings();
        r.setRate(userRatings.get(imp).intValue());
        r.setUserID(ratingsHelper.getUser(httpServletRequest.getRemoteUser()));
        r.setVersionID(ratingsHelper.getImpById(imp));
        ratingMap.put(String.valueOf(imp), r);
        ratingsHelper.insertOrUpdateRatings(r);
    }

    public void setRating(Double i) {
        rating = i;
    }

    public Map<Integer, Double> getRatingMap() {
        return userRatings;
    }

}
