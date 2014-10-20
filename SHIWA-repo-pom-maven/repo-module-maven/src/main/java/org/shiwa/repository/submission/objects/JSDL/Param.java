/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.submission.objects.JSDL;

/**
 * Class containing all attributes for a parameter
 * @author Benoit Meilhac <B.Meilhac@westminster.ac.uk>
 */
public class Param {

    private boolean cmdLine;
    private String defaultValue; //path to get name if file = true
    private boolean file;
    private boolean fixed;
    private boolean input;
    private String prefixCmd;
    private String fileName; // if file
    private String title;

    public Param() {
    }

    public boolean isCmdLine() {
        return cmdLine;
    }

    public void setCmdLine(boolean cmdLine) {
        this.cmdLine = cmdLine;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    public String getPrefixCmd() {
        return prefixCmd;
    }

    public void setPrefixCmd(String prefixCmd) {
        this.prefixCmd = prefixCmd;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
