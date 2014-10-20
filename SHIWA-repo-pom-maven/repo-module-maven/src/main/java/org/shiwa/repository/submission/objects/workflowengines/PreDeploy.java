/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects.workflowengines;

/**
 * Class representing a pre-deploy configuration for a workflow engine implementation
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class PreDeploy extends AbstractDeployment {

    private String shellPathEndPoint;

    public PreDeploy() {
    }

    public String getShellPathEndPoint() {
        return shellPathEndPoint;
    }

    public void setShellPathEndPoint(String shellPathEndPoint) {
        this.shellPathEndPoint = shellPathEndPoint;
    }
}
