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
public class ConfigurationSummaryRTO {
    int id;
    String description;
    String title;

    public ConfigurationSummaryRTO() {
    }

    public ConfigurationSummaryRTO(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public ConfigurationSummaryRTO(ConfigurationRTO configuration) {
        this.id = configuration.id;
        this.description = configuration.description;
    }

    @Override
    public String toString() {
        return id + RepoUtil.UNIT + title + RepoUtil.UNIT + description;
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
}