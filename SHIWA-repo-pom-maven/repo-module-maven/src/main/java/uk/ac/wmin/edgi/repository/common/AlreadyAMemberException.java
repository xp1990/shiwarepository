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
public class AlreadyAMemberException extends Exception{

    public AlreadyAMemberException(String message) {
        super(message);
    }

    public AlreadyAMemberException() {
    }

}
