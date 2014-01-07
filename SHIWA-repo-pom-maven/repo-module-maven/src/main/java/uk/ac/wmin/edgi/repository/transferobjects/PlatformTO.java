/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import uk.ac.wmin.edgi.repository.entities.Platform;

/**
 *
 * @author zsolt
 */
public class PlatformTO implements Serializable {

    public static final String queryPrefix = "SELECT NEW uk.ac.wmin.edgi.repository.transferobjects.PlatformTO(o.id, o.name, o.description) FROM Platform o ";
    public static final String countPrefix = "SELECT COUNT(o) FROM Platform o ";
    private static final String[][] filterFields = {
        {"name", "o.name LIKE :name "},
    };
    private static final String[][] sortFields = {
        {null, "o.name "}, //default sort field
        {"name", "o.name "},
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
    String version;
    String description;

    public PlatformTO() {
    }

    public PlatformTO(Platform p) {
        this.id = p.getId();
        this.name = p.getName();
        this.version = p.getVersion();
        this.description = p.getDescription();
    }

    public PlatformTO(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public PlatformTO(Integer id, String name, String version, String description) {
        this.id = id;
        this.name = name;
        this.version = version;        
        this.description = description;
    }    

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    

    public String getDescription() {
        return description;
    }

    public String getShortDescription(){
        if(this.description.length() <= 50){
            return this.description;
        }else{
            return this.description.substring(0, 47) + "...";
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
