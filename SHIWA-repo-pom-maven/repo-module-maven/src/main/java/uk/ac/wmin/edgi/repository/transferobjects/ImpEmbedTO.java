/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import uk.ac.wmin.edgi.repository.entities.ImpEmbed;

/**
 *
 * @author zsolt
 */
public class ImpEmbedTO implements Serializable {

    private Integer id;
    private Integer impId;
    private String extUserId;
    private String extServiceId;

    public ImpEmbedTO() {
    }

    public ImpEmbedTO(ImpEmbed embed){
        this.id = embed.getId();
        this.impId = embed.getImplementation().getId();
        this.extUserId = embed.getExtUserId();
        this.extServiceId = embed.getExtServiceId();
    }

    public ImpEmbedTO(Integer id, Integer impId, String extUserId, String extServiceId) {
        this.id = id;
        this.impId = impId;
        this.extUserId = extUserId;
        this.extServiceId = extServiceId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtUserId() {
        return extUserId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    public String getExtServiceId() {
        return extServiceId;
    }

    public void setExtServiceId(String extServiceId) {
        this.extServiceId = extServiceId;
    }
    
    public Integer getImpId() {
        return impId;
    }

    public void setImpId(Integer parentId) {
        this.impId = parentId;
    }

}
