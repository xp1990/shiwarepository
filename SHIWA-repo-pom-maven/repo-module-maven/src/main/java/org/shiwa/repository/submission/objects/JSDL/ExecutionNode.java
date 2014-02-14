/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects.JSDL;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class ExecutionNode {

    private String implFullName; // AppName#ImplVersion
    private int maxWallTime;
    private List<Param> listInputs;
    private List<Param> listOutputs;

    public ExecutionNode() {
        this.implFullName = null;
        this.maxWallTime = 0;
        this.listInputs = new ArrayList<Param>();
        this.listOutputs = new ArrayList<Param>();
    }

    public ExecutionNode(String implFullName) {
        this.implFullName = implFullName;
        this.maxWallTime = 0;
        this.listInputs = new ArrayList<Param>();
        this.listOutputs = new ArrayList<Param>();
    }

    public String getImplFullName() {
        return implFullName;
    }

    public void setImplFullName(String implFullName) {
        this.implFullName = implFullName;
    }

    public List<Param> getListInputs() {
        return listInputs;
    }

    public void setListInputs(List<Param> listInputs) {
        this.listInputs = listInputs;
    }

    public List<Param> getListOutputs() {
        return listOutputs;
    }

    public void setListOutputs(List<Param> listOutputs) {
        this.listOutputs = listOutputs;
    }

    public int getMaxWallTime() {
        return maxWallTime;
    }

    public void setMaxWallTime(int maxWallTime) {
        this.maxWallTime = maxWallTime;
    }
}
