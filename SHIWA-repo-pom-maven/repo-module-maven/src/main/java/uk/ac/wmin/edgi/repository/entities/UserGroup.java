/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 *
 * @author zsolt
 */
@Entity
@Table(name = "repo_group")
@NamedQueries({
    @NamedQuery(name = "UserGroup.findByName", query = "SELECT g FROM UserGroup g WHERE g.name = :name"),
    @NamedQuery(name = "UserGroup.findById", query = "SELECT g FROM UserGroup g WHERE g.id = :id"),
    @NamedQuery(name = "UserGroup.filterNames", query = "SELECT g.name FROM UserGroup g WHERE g.name LIKE :name ORDER BY g.name ASC"),
    @NamedQuery(name = "UserGroup.filterNamesByLeader", query = "SELECT g.name FROM UserGroup g WHERE g.name LIKE :name AND g.leader.loginName = :leaderName ORDER BY g.name ASC"),
    @NamedQuery(name = "UserGroup.filterNamesByMember", query = "SELECT g.name FROM UserGroup g JOIN g.users u WHERE u.id = :userId AND g.name LIKE :name ORDER BY g.name ASC"),
    @NamedQuery(name = "UserGroup.loadAllUserGroups", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.GroupTO(g.id, g.name, g.leader.loginName) FROM UserGroup g"),
    @NamedQuery(name = "UserGroup.loadUserGroupsOfUser", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.GroupTO(g.id, g.name, g.leader.loginName) FROM UserGroup g JOIN g.users u WHERE u.id = :userId"),
    @NamedQuery(name = "UserGroup.loadOwnedUserGroupsOfUser", query = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.GroupTO(g.id, g.name, g.leader.loginName) FROM UserGroup g WHERE g.leader.id = :leaderId")
})
@TableGenerator(name="group_gen", table="generator", pkColumnName="name", pkColumnValue="group_gen", valueColumnName="value")
public class UserGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(generator="group_gen") @Column(name = "id") private Integer id;
    @Basic(optional = false) @Column(name = "name") private String name;
    //the following means UserGroup is the owning side of the relationship
    @JoinTable(name = "user_group",
                joinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")},
                inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany(cascade=CascadeType.ALL)
    @OrderBy("loginName ASC")
    private List<User> users;

    @OneToMany(mappedBy = "group") private List<Application> applications;
    @JoinColumn(name = "leader_id", referencedColumnName = "id") @ManyToOne(optional = false) private User leader;

    public UserGroup() {
    }

    public UserGroup(String name, User leader) {
        this.name = name;
        this.leader = leader;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User user) {
        leader = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User user){
        if(users == null){
            users = new ArrayList<User>();
        }
        if(!users.contains(user)){
            users.add(user);
        }
    }

    public void removeUser(User user){
        if(users != null && users.contains(user)){
            users.remove(user);
        }
    }

    public List<Application> getApplications() {
        return applications;
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
        if (!(object instanceof UserGroup)) {
            return false;
        }
        UserGroup other = (UserGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "uk.ac.wmin.edgi.repository.entities.UserGroup[id=" + id + "]";
    }

}
