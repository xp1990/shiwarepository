/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.ui;

import java.io.Serializable;

/**
 *
 * @author zsolt
 */
public class NewUserBean implements Serializable{

    String loginName = "";
    String fullName = "";
    String emailAddr = "";
    String organization = "";
    String password = "";
    boolean admin = false;
    boolean WEDev = false;
    boolean active = true;

    public NewUserBean() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isWEDev() {
        return WEDev;
    }

    public void setWEDev(boolean WEDev) {
        this.WEDev = WEDev;
    }

    public void clear(){
        this.loginName = "";
        this.active = true;
        this.admin = false;
        this.emailAddr = "";
        this.fullName = "";
        this.organization = "";
        this.password = "";
        this.WEDev = false;
    }


}
