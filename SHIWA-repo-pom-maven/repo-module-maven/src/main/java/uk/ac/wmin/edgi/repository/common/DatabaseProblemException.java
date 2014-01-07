/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.edgi.repository.common;

/**
 *
 * @author glassfish
 */
public class DatabaseProblemException extends Exception {

    /**
     * Creates a new instance of
     * <code>DatabaseProblemException</code> without detail message.
     */
    public DatabaseProblemException() {
    }

    /**
     * Constructs an instance of
     * <code>DatabaseProblemException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DatabaseProblemException(String msg) {
        super(msg);
    }

    public DatabaseProblemException(Throwable thrwbl) {
        super(thrwbl);
    }

    public DatabaseProblemException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
