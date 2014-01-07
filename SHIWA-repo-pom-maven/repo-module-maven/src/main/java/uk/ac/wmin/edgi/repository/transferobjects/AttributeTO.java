/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.wmin.edgi.repository.transferobjects;

import java.io.Serializable;
import uk.ac.wmin.edgi.repository.entities.AppAttribute;
import uk.ac.wmin.edgi.repository.entities.ImpAttribute;

/**
 *
 * @author zsolt
 */
public class AttributeTO implements Serializable {

    private Integer id;
    private Integer parentId;
    private String name;
    private String value;

    public AttributeTO() {
    }

    public AttributeTO(AppAttribute attr){
        this.id = attr.getId();
        this.parentId = attr.getApplication().getId();
        this.name = attr.getName();
        this.value = attr.getValue();
    }

    public AttributeTO(ImpAttribute attr){
        this.id = attr.getId();
        this.parentId = attr.getImplementation().getId();
        this.name = attr.getName();
        this.value = attr.getValue();
    }

    public AttributeTO(Integer attrId, Integer parentId, String attrName, String attrVal) {
        this.id = attrId;
        this.parentId = parentId;
        this.name = attrName;
        this.value = attrVal;
    }

    public AttributeTO(String attrName, String attrVal) {
        this.name = attrName;
        this.value = attrVal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getShortValue(){
        if(value == null || value.length() <= 30){
            return value;
        }else{
            return (value.substring(0, 27) + "...");
        }
    }

}
