/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.common;

import javax.ejb.ApplicationException;

/**
 *
 * @author zsolt
 */
@ApplicationException(rollback = true)
public class ValidationFailedException extends Exception {

    public ValidationFailedException(String message) {
        super(message);
    }

    public ValidationFailedException() {
    }

}
