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
public class GroupRTO {
    int id;
    String title;

    public GroupRTO() {
    }

    public GroupRTO(int id, String title) {
        this.id = id;
        this.title = title;
    }
    
    @Override
    public String toString() {
        return id + RepoUtil.UNIT + title;
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
    
    
    
}