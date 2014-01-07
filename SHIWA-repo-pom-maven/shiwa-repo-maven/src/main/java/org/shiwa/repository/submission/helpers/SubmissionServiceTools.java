/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.helpers;

import javax.servlet.http.HttpServletRequest;
import org.shiwa.repository.toolkit.exceptions.NotFoundException;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class SubmissionServiceTools {

    public static boolean isValid(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                return false;
            }
        }

        return true;
    }

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
