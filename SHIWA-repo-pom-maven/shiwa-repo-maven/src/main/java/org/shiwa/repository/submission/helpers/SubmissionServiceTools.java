/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.helpers;

import javax.servlet.http.HttpServletRequest;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;

/**
 * This class provides methods to help the web service checking parameters and
 * getting the SHIWA Repository URL in order to download files.
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class SubmissionServiceTools {

    /**
     * Check if the list of string is valid of not
     * @param params list of string
     * @return true is all values are not null and not empty, false otherwise
     */
    public static boolean isValid(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Build the URL of the SHIWA Repository from the call done by the service
     * contacting the web service deployed on the SHIWA Repository side.
     * @param servletRqt request done by the service calling the SHIWA Reporitory
     * web service
     * @return URL of the SHIWA Repository 
     * (eg. http://shiwa-repo.cpc.wmin.ac.uk/shiwa-repo)
     * @throws NotFoundException 
     */
    public static String buildRepositoryURL(HttpServletRequest servletRqt)
            throws NotFoundException {
        if (servletRqt == null) {
            throw new NotFoundException("Impossible to get a correct Repository"
                    + " server name");
        }

        String serverName = servletRqt.getServerName();
        int serverPort = servletRqt.getServerPort();
        String serverContext = servletRqt.getContextPath();

        return (servletRqt.isSecure() ? "https" : "http") + "://"
                + serverName + (serverPort == 80 ? "" : ":" + serverPort)
                + serverContext;
    }
}
