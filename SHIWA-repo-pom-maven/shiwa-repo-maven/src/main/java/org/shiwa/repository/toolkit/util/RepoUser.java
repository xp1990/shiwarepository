/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.util;

import uk.ac.wmin.edgi.repository.server.ApplicationFacadeLocal;

/**
 *
 * @author attila
 */
public class RepoUser {    
    public static final String GUEST_USER = "guest";
    ApplicationFacadeLocal af;
    int id = -1;
            
    public RepoUser(ApplicationFacadeLocal af) {
        this.af = af;
    }        

    public void setUserId(String remoteUser) {                   
        
        id = af.loadUser((remoteUser != null)
                    ? remoteUser
                    : GUEST_USER
                ).getId();
    }
    
    public int getId() {
        return id;
    }
}
