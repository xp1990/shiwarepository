/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.exceptions;

/**
 *
 * @author kukla
 */
public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
    }

    public NotFoundException(Throwable thrwbl) {
        super(thrwbl);
    }

    public NotFoundException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
