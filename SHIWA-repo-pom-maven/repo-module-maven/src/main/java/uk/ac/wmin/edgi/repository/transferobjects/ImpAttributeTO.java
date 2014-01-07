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
public class ImpAttributeTO extends AttributeTO{

    public static final String queryPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.AttributeTO(o.id, o.implementation.id, o.name, o.value) FROM ImpAttribute o ";
    public static final String countPrefix = "SELECT COUNT(o) FROM ImpAttribute o ";
    private static final String[][] filterFields = {
        {"name", "o.name LIKE :name "},
        {"impId", "o.implementation.id = :impId "}
    };
    private static final String[][] sortFields = {
        {null, "o.name "}, //default sort field
        {"name", "o.name "}
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
