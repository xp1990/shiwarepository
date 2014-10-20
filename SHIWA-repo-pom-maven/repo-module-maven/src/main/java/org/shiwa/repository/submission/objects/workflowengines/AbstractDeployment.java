/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects.workflowengines;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Parent class defining the deployment strategy of a workflow engine implementation
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
@XmlSeeAlso({
    PreDeploy.class,
    OnTheFly.class
})
public abstract class AbstractDeployment implements Serializable{
}
