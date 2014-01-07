/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.ui;

import java.io.Serializable;

/**
 *
 * @author zsolt
 */
public class NewCommentBean implements Serializable {

    String message;

    public NewCommentBean() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void clear(){
        this.message = "";
    }

}
