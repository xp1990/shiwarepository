/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.exceptions;

/**
 *
 * @author kukla
 */
public class ForbiddenException extends Exception {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
    }

    public ForbiddenException(Throwable thrwbl) {
        super(thrwbl);
    }

    public ForbiddenException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
