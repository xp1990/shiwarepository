/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects;

import java.io.Serializable;

/**
 * Class containing the data to retrieve a workflow engine implementation
 * 
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class EngineData implements Serializable {

    private String engineName;
    private String engineVersion;
    private String engineInstanceName;

    public EngineData() {
    }

    public EngineData(String engineName, String engineVersion,
            String engineInstanceName) {
        this.engineName = engineName;
        this.engineVersion = engineVersion;
        this.engineInstanceName = engineInstanceName;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public String getEngineInstanceName() {
        return engineInstanceName;
    }

    public void setEngineInstanceName(String engineInstanceName) {
        this.engineInstanceName = engineInstanceName;
    }
}
