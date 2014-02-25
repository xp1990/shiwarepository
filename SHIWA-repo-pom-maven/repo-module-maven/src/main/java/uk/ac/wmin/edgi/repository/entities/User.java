/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;
import org.shiwa.repository.toolkit.wfengine.BeInstance;
import org.shiwa.repository.toolkit.wfengine.WEImplementation;

/**
 *
 * @author zsolt
 */
@Entity
@Table(name = "repo_user")
@NamedQueries({
    @NamedQuery(name = "User.loadByLoginName", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.UserTO(u.id, u.loginName, u.fullName, u.organization, u.email, u.admin, u.WEDev, u.active) FROM User u WHERE u.loginName = :loginName"),
    @NamedQuery(name = "User.findByLoginName", query = "SELECT u FROM User u WHERE u.loginName = :loginName"),
    @NamedQuery(name = "User.filterNames", query = "SELECT u.loginName FROM User u WHERE u.loginName LIKE :loginName ORDER BY u.loginName ASC"),
    @NamedQuery(name = "User.filterNamesNotInGroup", query = "SELECT u.loginName FROM User u WHERE u.loginName LIKE :loginName AND u.id NOT IN (SELECT gu.id FROM UserGroup g JOIN g.users gu WHERE g.id = :groupId) ORDER BY u.loginName ASC"),
    @NamedQuery(name = "User.loadAllUsers", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.UserTO(u.id, u.loginName, u.fullName, u.organization, u.email, u.admin, u.WEDev, u.active) FROM User u"),
    @NamedQuery(name = "User.loadUsersOfGroupLikeName", query = "SELECT u.loginName FROM User u JOIN u.groups g WHERE g.id = :groupId AND u.loginName LIKE :loginName"),
    @NamedQuery(name = "User.loadUsersOfGroup", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.UserTO(u.id, u.loginName, u.fullName, u.organization, u.email, u.admin, u.WEDev, u.active) FROM User u JOIN u.groups g WHERE g.id = :groupId")
})
@TableGenerator(name="user_gen", table="generator", pkColumnName="name", pkColumnValue="user_gen", valueColumnName="value")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="user_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "login_name") private String loginName;
    @Basic(optional = false) @Column(name = "pass_hash") private String passHash;
    @Basic(optional = false) @Column(name = "full_name") private String fullName;
    @Basic(optional = false) @Column(name = "organization") private String organization;
    @Basic(optional = false) @Column(name = "email") private String email;
    @Basic(optional = false) @Column(name = "active") private Boolean active;
    @Basic(optional = false) @Column(name = "admin") private Boolean admin;
    @Basic(optional = false) @Column(name = "we_dev") private Boolean WEDev;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") @OrderBy("created DESC") private List<AppComment> appComments;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") @OrderBy("created DESC") private List<ImpComment> impComments;
        @OneToMany(mappedBy = "owner") private Set<Application> applications = new HashSet<Application>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "leader") @OrderBy("name ASC") private List<UserGroup> ledGroups;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "users") @OrderBy("name ASC") private List<UserGroup> groups;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "WEDev")
    private List<WEImplementation> WEImps;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "WEDev")
    private List<BeInstance> BEInsts;

    public Boolean getWEDev() {
        return WEDev;
    }

    public void setWEDev(Boolean WEDev) {
        this.WEDev = WEDev;
    }

    @XmlTransient
    public List<BeInstance> getBEInsts() {
        return BEInsts;
    }

    public void setBEInsts(List<BeInstance> BEInsts) {
        this.BEInsts = BEInsts;
    }


    @XmlTransient
    public List<WEImplementation> getWEImps() {
        return WEImps;
    }

    public void setWEImps(List<WEImplementation> WEImps) {
        this.WEImps = WEImps;
    }

    public User() {
    }

    public User(String loginName, String passHash, String fullName, String organization, String email, Boolean active, Boolean admin, Boolean WEDev) {
        this.loginName = loginName;
        this.passHash = passHash;
        this.fullName = fullName;
        this.organization = organization;
        this.email = email;
        this.active = active;
        this.admin = admin;
        this.WEDev = WEDev;
    }

    /*
    public User(String loginName, String passHash, String fullName, String organization, String email) {
        this.loginName = loginName;
        this.passHash = passHash;
        this.fullName = fullName;
        this.organization = organization;
        this.email = email;
    }*/

    public Integer getId() {
        return id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UserGroup> getGroups() {
        return groups;
    }

    public void addGroup(UserGroup group){
        if(groups == null){
            groups = new ArrayList<UserGroup>();
        }
        if(!groups.contains(group)){
            groups.add(group);
        }
    }

    public void removeGroup(UserGroup group){
        if(groups != null && groups.contains(group)){
            groups.remove(group);
            //group.removeUser(this);
        }
    }

    public List<UserGroup> getLedGroups() {
        return ledGroups;
    }

    public void addLedGroup(UserGroup group){
        if(ledGroups == null){
            ledGroups = new ArrayList<UserGroup>();
        }
        if(!ledGroups.contains(group)){
            ledGroups.add(group);
        }
    }

    public void removeLedGroup(UserGroup group){
        if(ledGroups != null && ledGroups.contains(group)){
            ledGroups.remove(group);
        }
    }

    public List<AppComment> getAppComments() {
        return appComments;
    }

    public Set<Application> getApplications() {
        return applications;
    }

    public List<ImpComment> getImpComments() {
        return impComments;
    }

    public boolean isAdmin(){
        return admin;
    }

    public boolean isWEDev(){
        return WEDev;
    }

    public boolean isUser(){
        return true;
    }

    public boolean isActive(){
        return active;
    }

    public boolean getActive(){
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public void setUser(boolean set){
        //do nothing
    }

    public void setWEDev(boolean set){
        WEDev = set;
    }

    public String getRolesAsString(){
        return (isActive() ? "active" : "inactive") + (isWEDev() ? ", WEDev" : "") + (isAdmin() ? ", admin" : "") ;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.User[id=" + id + "]";
    }

}
