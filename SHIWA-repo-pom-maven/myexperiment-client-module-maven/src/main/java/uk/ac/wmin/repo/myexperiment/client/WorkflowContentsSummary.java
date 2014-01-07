/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.wmin.repo.myexperiment.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Benoit Meilhac
 */
public class WorkflowContentsSummary {

    private Map<String, String> workflowContents;
    private String[] listOfID = {"title", "description", "content-uri",
        "thumbnail-big", "preview", "license-type", "type", "content-type",
        "tags"};

    public WorkflowContentsSummary() {
        this.workflowContents = new HashMap<String, String>();

        for (int i = 0; i < this.listOfID.length; i++) {
            String id = this.listOfID[i];
            this.workflowContents.put(id, "");
        }
    }

    public String[] getListOfID() {
        return listOfID;
    }

    public void put(String id, String contents) {
        if (id != null
                && (Arrays.asList(listOfID).contains(id) || id.equals("tag"))) {
            workflowContents.put(id, contents);
        }
    }
    
    public String getTag(){
        return workflowContents.get("tag");
    }

    public String getContentType() {
        return workflowContents.get("content-type");
    }

    public String getContentUri() {
        return workflowContents.get("content-uri");
    }

    public String getDescription() {
        return workflowContents.get("description");
    }

    public String getLicenseType() {
        return workflowContents.get("license-type");
    }

    public String getPreview() {
        return workflowContents.get("preview");
    }

    public String getTags() {
        return workflowContents.get("tags");
    }

    public String getThumbnailBig() {
        return workflowContents.get("thumbnail-big");
    }

    public String getTitle() {
        return workflowContents.get("title");
    }

    public String getType() {
        return workflowContents.get("type");
    }
}
