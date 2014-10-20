/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects;

import java.io.Serializable;

/**
 * Class representing a parameter formatted as GEMLCA was doing before the 
 * SHIWA Submission Service. We kept this way to do in order to have a better
 * integration with the gUSE/WS-PGRADE Portal.
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class Parameter implements Serializable {

    private int index;
    private String value;

    public Parameter() {
    }

    public Parameter(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
