/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kukla
 */
public class SignatureRTO {
    String taskType;
    List<PortRTO> inputPorts;
    List<PortRTO> outputPorts;

    public SignatureRTO() {
        inputPorts = new ArrayList<PortRTO>();
        outputPorts = new ArrayList<PortRTO>();
    }
    
    public SignatureRTO(String taskType, List<PortRTO> inputPorts, List<PortRTO> outputPorts) {
        this.taskType = taskType;
        this.inputPorts = inputPorts;
        this.outputPorts = outputPorts;
    }

    @Override
    public String toString() {
        return "Signature{" + "taskType=" + taskType + ", inputPorts=" + inputPorts + ", outputPorts=" + outputPorts + '}';
    }

    public List<PortRTO> getInputPorts() {
        return inputPorts;
    }

    public void setInputPorts(List<PortRTO> inputPorts) {
        this.inputPorts = inputPorts;
    }

    public List<PortRTO> getOutputPorts() {
        return outputPorts;
    }

    public void setOutputPorts(List<PortRTO> outputPorts) {
        this.outputPorts = outputPorts;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    
}
