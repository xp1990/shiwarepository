/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import uk.ac.wmin.edgi.repository.entities.UserGroup;

/**
 *
 * @author zsolt
 */
public class GroupTO implements Serializable {

    public static final String queryPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.GroupTO(o.id, o.name, o.leader.loginName) FROM UserGroup o ";
    public static final String queryUserGroupsPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.GroupTO(o.id, o.name, o.leader.loginName) FROM UserGroup o JOIN o.users u ";
    public static final String countPrefix = "SELECT Count(o) FROM UserGroup o ";
    public static final String countUserGroupsPrefix = "SELECT Count(o) FROM UserGroup o JOIN o.users u ";
    private static final String[][] filterFields = {
        {"userId", "u.id = :userId "},
        {"leaderId", "o.leader.id = :leaderId "},
        {"name", "o.name LIKE :name "},
        {"leaderLoginName", "o.leader.name LIKE :leaderLoginName "},
    };
    private static final String[][] sortFields = {
        {null, "o.name "}, //default sort field
        {"name", "o.name "},
        {"leaderLoginName", "o.leader.name "},
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
    String name;
    String leaderLoginName;

    public GroupTO() {
    }

    public GroupTO(UserGroup group){
        this.id = group.getId();
        this.name = group.getName();
        this.leaderLoginName = group.getLeader().getLoginName();
    }

    public GroupTO(Integer id, String name, String leaderName) {
        this.id = id;
        this.name = name;
        this.leaderLoginName = leaderName;
    }

    public Integer getId() {
        return id;
    }

    public String getLeaderLoginName() {
        return leaderLoginName;
    }

    public void setLeaderLoginName(String leaderLoginName) {
        this.leaderLoginName = leaderLoginName;
    }

    public String getName() {
        return name;
    }

}
