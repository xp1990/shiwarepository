/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zsolt
 */
public class ImpCommentTO {

    public static final String queryPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.CommentTO(o.id, o.application.id, o.created, o.user.loginName) FROM ImpComment o ";
    public static final String countPrefix = "SELECT COUNT(o) FROM ImpComment o ";
    private static final String[][] filterFields = {
        {"userLoginName", "o.user.loginName LIKE :loginName "},
        {"parentId,", "o.implementation.id = parentId"},
    };
    private static final String[][] sortFields = {
        {null, "o.created "}, //default sort field
        {"created", "o.created "},
        {"userLoginName", "o.user.loginName "},
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

}
