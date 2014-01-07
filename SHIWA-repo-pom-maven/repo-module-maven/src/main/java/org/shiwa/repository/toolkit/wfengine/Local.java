/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.wfengine;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import uk.ac.wmin.edgi.repository.entities.User;

/**
 *
 * @author edward
 */
@Entity
@DiscriminatorValue("4")
public class Local extends BeInstance{

    protected Local(){

    }

    public Local(String _name, Backend _idBackend, String _backendOut, String _backendErr, OperatingSystems _idOS, String _resource, User _wedev) {
        super(_name, _idBackend, _backendOut, _backendErr, _idOS, _resource, _wedev);
    }

}
