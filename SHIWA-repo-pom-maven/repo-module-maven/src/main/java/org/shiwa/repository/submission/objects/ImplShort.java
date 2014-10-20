/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects;

import java.io.Serializable;

/**
 * Class representing a short overview of an implementation
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ImplShort implements Serializable {

    private String name;
    private String description;
    private boolean selected;

    public ImplShort() {
    }

    public ImplShort(String name, String description, boolean selected) {
        this.name = name;
        this.description = description;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
