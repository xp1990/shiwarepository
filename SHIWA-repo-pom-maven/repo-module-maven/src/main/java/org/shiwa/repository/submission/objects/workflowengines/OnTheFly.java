/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects.workflowengines;

/**
 * Class representing an On The Fly configuration for a workflow engine implementation
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class OnTheFly extends AbstractDeployment {

    private String zipName;
    private String zipPath;
    private String shellName;
    private String shellPath;

    public OnTheFly() {
    }

    public String getZipName() {
        return zipName;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName;
    }

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public String getShellName() {
        return shellName;
    }

    public void setShellName(String shellName) {
        this.shellName = shellName;
    }

    public String getShellPath() {
        return shellPath;
    }

    public void setShellPath(String shellPath) {
        this.shellPath = shellPath;
    }
}
