/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import uk.ac.wmin.edgi.repository.entities.User;

/**
 *
 * @author zsolt
 */
public class UserTO implements Serializable {

    public static final String queryPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.UserTO(o.id, o.loginName, o.fullName, o.organization, o.email, o.admin, o.validator, o.active) FROM User o ";
    public static final String queryGroupUsersPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.UserTO(o.id, o.loginName, o.fullName, o.organization, o.email, o.admin, o.validator, o.active) FROM User o JOIN o.groups g ";
    public static final String countPrefix = "SELECT COUNT(o) FROM User o ";
    public static final String countGroupUsersPrefix = "SELECT COUNT(o) FROM User o JOIN o.groups g ";
    private static final String[][] filterFields = {
        {"groupId", "g.id = :groupId "},
        {"loginName", "o.loginName LIKE :loginName "},
        {"fullName", "o.fullName LIKE :fullName "},
        {"organization", "o.organization LIKE :organization "}
    };
    private static final String[][] sortFields = {
        {null, "o.loginName "}, //default sort field
        {"admin", "o.admin "},
        {"active", "o.active "},
        {"WEDev", "o.WEDev "},
        {"loginName", "o.loginName "},
        {"fullName", "o.fullName "},
        {"organization", "o.organization "}
    };

    public static final Map<String, String> filterMap;
    public static final Map<String, String> sortMap;

    static{
        filterMap = new HashMap<String, String>(filterFields.length);
        for(String[] s: filterFields){
            filterMap.put(s[0], s[1]);
        }
        sortMap = new HashMap<String, String>(sortFields.length);
        for(String[] s: sortFields){
            sortMap.put(s[0], s[1]);
        }
    }

    Integer id;
    String loginName;
    String fullName;
    String organization;
    String email;
    Boolean admin;
    Boolean WEDev;
    Boolean active;

    public UserTO() {
    }

    public UserTO(User u) {
        this.id = u.getId();
        this.loginName = u.getLoginName();
        this.fullName = u.getFullName();
        this.organization = u.getOrganization();
        this.email = u.getEmail();
        this.admin = u.isAdmin();
        this.WEDev = u.isWEDev();
        this.active = u.isActive();
    }

    public UserTO(Integer id, String loginName, String fullName, String organization, String email, Boolean admin, Boolean WEDev, Boolean active) {
        this.id = id;
        this.loginName = loginName;
        this.fullName = fullName;
        this.organization = organization;
        this.email = email;
        this.admin = admin;
        this.WEDev = WEDev;
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getOrganization() {
        return organization;
    }

    public String getFullName() {
        return fullName;
    }

    public Boolean getWEDev() {
        return WEDev;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setFullName(String realName) {
        this.fullName = realName;
    }

    public void setWEDev(Boolean WEDev) {
        this.WEDev = WEDev;
    }

    public String getRolesAsString(){
        return (getActive() ? "active" : "inactive") + (getWEDev() ? ", WEDev" : "") + (getAdmin() ? ", admin" : "") ;
    }

}
