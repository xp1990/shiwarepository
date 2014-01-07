/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import org.shiwa.repository.toolkit.util.RepoUtil;

/**
 *
 * @author kukla
 */
public class EngineRTO implements Comparable {
    int id;
    String title;
    String version;
    String description;

    public EngineRTO() {
    }

    public EngineRTO(int id, String title, String version, String description) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.description = description;
    }
    
    @Override
    public String toString() {
        return id + RepoUtil.UNIT + title + RepoUtil.UNIT + version + RepoUtil.UNIT + description;
    }    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(Object t) {
        EngineRTO comp = (EngineRTO) t;
        return this.getVersion().compareToIgnoreCase(comp.getVersion()) 
               + this.getTitle().compareToIgnoreCase(comp.getTitle());
    }
}
