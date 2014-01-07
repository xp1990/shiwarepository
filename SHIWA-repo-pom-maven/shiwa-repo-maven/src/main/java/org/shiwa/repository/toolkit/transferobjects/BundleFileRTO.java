/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.toolkit.transferobjects;

import java.io.File;

/**
 *
 * @author kukla
 */
public class BundleFileRTO {
    String title;
    public enum FileType {DEFINITION_FILE,INPUT_FILE,DEPENDENCY_FILE,BUNDLE_FILE,IMAGE_FILE,CTR_FILE};    
    FileType fileType;
    File file;
    String description;

    public BundleFileRTO() {
    }
    
    public BundleFileRTO(String title) {
        this.title=title;
    }    

    public BundleFileRTO(String title, FileType fileType, File file, String description) {
        this.title = title;
        this.fileType = fileType;
        this.file = file;
        this.description = description;
    }

    @Override
    public String toString() {
        return "BundleFile{" + "title=" + title + ", fileType=" + fileType + ", file=" + file.getAbsolutePath() + ", description=" + description + '}';
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
        
}
