/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

/**
 *
 * @author fritmayo
 */
public class WEBuildingException extends Exception
{
    public WEBuildingException( String _errArg, String _errValue )
    {
	System.err.println("Impossible to create a workflow engine object with the paramter " + _errArg + " sets to " + _errValue);
	System.err.println("Parameter length is wrong. Aborting...");
    }
}
