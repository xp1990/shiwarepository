/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.exceptions;

/**
 *
 * @author kukla
 */
public class ItemExistsException extends Exception {

    public ItemExistsException(String message) {
        super(message);
    }

    public ItemExistsException() {
    }
    
}
