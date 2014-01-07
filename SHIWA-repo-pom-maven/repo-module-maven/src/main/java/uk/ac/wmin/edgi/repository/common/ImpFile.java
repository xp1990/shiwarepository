/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.common;

import java.io.InputStream;

/**
 *
 * @author zsolt
 */
public class ImpFile {

    String fileName;
    InputStream stream;
    long size;

    public ImpFile() {
    }

    public ImpFile(String fileName, InputStream stream, long size) {
        this.fileName = fileName;
        this.stream = stream;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    }

    public InputStream getStream() {
        return stream;
    }

}
