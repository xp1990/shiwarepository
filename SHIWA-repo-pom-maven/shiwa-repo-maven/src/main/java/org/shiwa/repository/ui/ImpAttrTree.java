/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import uk.ac.wmin.edgi.repository.transferobjects.AttributeTO;
import uk.ac.wmin.edgi.repository.transferobjects.ImplementationTO;
import uk.ac.wmin.edgi.repository.transferobjects.UserTO;
import uk.ac.wmin.edgi.repository.transferobjects.ApplicationTO;

/**
 *
 * @author kukla
 */
public class ImpAttrTree extends AttrTree{

    TreeNode dependencies;
    TreeNode configurations;
    TreeNode title;
    TreeNode description;
    TreeNode definition;
    TreeNode graph;
    TreeNode language;
    TreeNode rights;
    TreeNode licence;
    TreeNode keywords;
    TreeNode uuid;
    TreeNode execution;
    Dependency newDep = new Dependency();
    Config newConfig = new Config();
    Parameter newPara=new Parameter();
    Parameter editPara=new Parameter();
    UserTO currentUser= null;
    String textEditValue = "";
    String fileSelValue = "";
    String impSelValue = "";
    String wfSelValue = "";
    String depTypeValue = "";
    ImplementationTO selectedImp = null;
    ApplicationTO selectedApp=null;
    AppAttrTree aTree=null;
    Node newParameterIdNode= new Node("","");
    Node newParameterTypeNode= new Node("","");

    @Override
    public void setItemID(Integer itemID) {
        super.setItemID(itemID);
    }
   // @Override
    public void setItem(ImplementationTO selectedImp) {
         this.selectedImp=selectedImp;
    }

    public void setApplication(ApplicationTO selectedApp) {
        this.selectedApp = selectedApp;
    }

    public void setUser(UserTO currentUser) {
        this.currentUser = currentUser;
    }

    public void setApThree(AppAttrTree aTree) {
        this.aTree = aTree;
    }

    public String getWfSelValue() {
        return wfSelValue;
    }

    public void setWfSelValue(String wfSelValue) {
        this.wfSelValue = wfSelValue;
    }

    String wfDefinition;
    public String getWfDefinition(){
       TreeNode item;

       Iterator<TreeNode> iterator = root.getChildren().iterator();
            while(iterator.hasNext()){
                item = iterator.next();
                if(((Node) item.getData()).getKey().equals("definition")){
                    wfDefinition=((Node) item.getData()).getValue();
                    }
                 }
       return wfDefinition;
    }

    String wfDescription;
    public String getWfDescription(){
       TreeNode item;

       Iterator<TreeNode> iterator = root.getChildren().iterator();
            while(iterator.hasNext()){
                item = iterator.next();
                if(((Node) item.getData()).getKey().equals("title")){
                    wfDescription=((Node) item.getData()).getValue();
                    }
                 }
       return wfDescription;
    }

    public String getImpSelValue() {
        return impSelValue;
    }

    public void setImpSelValue(String impSelValue) {
        this.impSelValue = impSelValue;
    }

    public void setWfDescription(String wfDescription) {
        this.wfDescription = wfDescription;
    }

    public void setWfDefinition(String wfDefinition) {
        this.wfDefinition = wfDefinition;
    }

    String executionMaxWallTime;
    public String getExecutionMaxWallTime(){
       TreeNode item;

       Iterator<TreeNode> iterator = execution.getChildren().iterator();
          while(iterator.hasNext()){
                item = iterator.next();
                if(((Node) item.getData()).getKey().equals("maxWallTime")){
                    executionMaxWallTime=((Node) item.getData()).getValue();
                   }
                }
       return executionMaxWallTime;
    }
    String executionMaxParallelism;
    public String getExecutionMaxParallelism(){
       TreeNode item;

       Iterator<TreeNode> iterator = execution.getChildren().iterator();
          while(iterator.hasNext()){
                item = iterator.next();
                if(((Node) item.getData()).getKey().equals("maxParallelism")){
                    executionMaxParallelism=((Node) item.getData()).getValue();
                   }
               }
      return executionMaxParallelism;
    }

    public void setExecutionMaxParallelism(String executionMaxParallelism){
            this.executionMaxParallelism=executionMaxParallelism;
    }

    public List<String> getParameterIDList() {
          TreeNode item1;
          TreeNode item2;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;

          List<String> parameterIDList = new ArrayList<String>();
          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                if(((Node) item1.getData()).getKey().equals("parameters")){
                   while(iterator2.hasNext()){
                         item2=iterator2.next();
                         parameterIDList.add(((Node) item2.getData()).getName());
                        }
                    }
                 }
        return parameterIDList;
    }
    String order;
    public String getOrder(String paraID) {
          TreeNode item1;
          TreeNode item2;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;

          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        if(((Node) item2.getData()).getKey().equals(paraID)){
                              if (paraID.equals("para0001")){
                                        order="0";
                                }else if (paraID.equals("para0002")){
                                        order="1";
                                }else if (  paraID.equals("para0003")){
                                        order="2";
                                }else if (  paraID.equals("para0004")){
                                        order="3";
                                }
                                else if (  paraID.equals("para0005")){
                                        order="4";
                                }
                                else if (  paraID.equals("para0006")){
                                        order="5";
                                }
                                else if (  paraID.equals("para0007")){
                                        order="6";
                                }
                                else if (  paraID.equals("para0008")){
                                        order="7";
                                }
                                else if (  paraID.equals("para0009")){
                                        order="8";
                                }
                                else if (  paraID.equals("para0010")){
                                        order="9";
                                }
                                else if (  paraID.equals("para0011")){
                                        order="10";
                                }
                                else if (  paraID.equals("para0012")){
                                        order="11";
                                }
                                else if (  paraID.equals("para0013")){
                                        order="12";
                                }
                                else if (  paraID.equals("para0014")){
                                        order="13";
                                }
                                else if (  paraID.equals("para0015")){
                                        order="14";
                                }
                                else if (  paraID.equals("para0016")){
                                        order="15";
                                }
                                else if (  paraID.equals("para0017")){
                                        order="16";
                                }
                                else if (  paraID.equals("para0018")){
                                        order="17";
                                }
                                else if (  paraID.equals("para0019")){
                                        order="18";
                                }
                                else if (  paraID.equals("para0020")){
                                        order="19";
                                }
                                else if (  paraID.equals("para0021")){
                                        order="20";
                                }
                                else if (  paraID.equals("para0022")){
                                        order="21";
                                }
                                else if (  paraID.equals("para0023")){
                                        order="22";
                                }
                                else if (  paraID.equals("para0024")){
                                        order="23";
                                }
                                else if (  paraID.equals("para0025")){
                                        order="24";
                                }
                                else if (  paraID.equals("para0026")){
                                        order="25";
                                }
                                else if (  paraID.equals("para0027")){
                                        order="26";
                                }
                                else if (  paraID.equals("para0028")){
                                        order="27";
                                }
                                else if (  paraID.equals("para0029")){
                                        order="28";
                                }
                                else if (  paraID.equals("para0030")){
                                        order="29";
                                }
                            }
                        }
                   }
      return order;
    }

    public boolean doesParameterExist() {
          TreeNode item1;
          TreeNode item2;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          if (execution !=null && execution.getChildren()!=null){
            iterator1 = execution.getChildren().iterator();
            if (iterator1 !=null){
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    if (item1 !=null && item1.getChildren()!=null ){
                        iterator2 = item1.getChildren().iterator();
                        if (iterator2 !=null){
                            while(iterator2.hasNext()){
                                item2=iterator2.next();
                                if (item2!=null
                                    && ((Node) item2.getData())!=null
                                    &&((Node) item2.getData()).getKey().equals("para0001")){
                                    return true;
                                 }
                             }
                         }
                      }
                  }
               }
            }
      return false;
    }

    String orderCmdLine;
    public String getOrderCmdLine(String paraID){
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        if(((Node) item2.getData()).getKey().equals(paraID)){
                            while(iterator3.hasNext()){
                                 item3=iterator3.next();
                                 if(((Node) item3.getData()).getKey().equals("cmdLine")){
                                             orderCmdLine=((Node) item3.getData()).getValue();
                                   }
                               }
                           }
                       }
                  }
      return orderCmdLine;
    }

    String editParaType="";
    public String getEditParaType(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("type")){
                   editParaType=((Node) item1.getData()).getValue();
                }
             }
          }
      return editParaType;
    }

    public void setEditParaType(String editParaType){
          this.editParaType=editParaType;
    }

    String editIdOfSelectedType="";
    public String getEditIdOfSelectedType(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("portId")){
                   editIdOfSelectedType=((Node) item1.getData()).getValue();
                }
             }
          }
      return editIdOfSelectedType;
    }

    public void setEditIdOfSelectedType(String editIdOfSelectedType){
          this.editIdOfSelectedType=editIdOfSelectedType;
    }

    String editSwitchName="";
    public String getEditSwitchName(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("switchName")){
                   editSwitchName=((Node) item1.getData()).getValue();
                }
             }
          }
      return editSwitchName;
    }

    public void setEditSwitchName(String editSwitchName){
          this.editSwitchName=editSwitchName;
    }

    String fillDefaultValue="";
    public String getFillDefaultValue(){

            if (getEditParaType().equals("INPUT_PORT")
                        ||getEditParaType().equals("OUTPUT_PORT")
                        ||getEditParaType().equals("DEPENDENCY")){
		fillDefaultValue = editDefaultValue;
              }
            else if (getEditParaType().equals("CUSTOM")){
                fillDefaultValue = editPara.getFillDefaultValueCustom();
              }
         return fillDefaultValue;

    }

    public void setFillDefaultValue(String fillDefaultValue){
          this.fillDefaultValue=fillDefaultValue;
    }

    String editDefaultValueCustom="";
    public String getEditDefaultValueCustom(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("defaultValue")){
                   editDefaultValueCustom=((Node) item1.getData()).getValue();
                }
             }
          }
      return editDefaultValueCustom;
    }

    public void setEditDefaultValueCustom(String editDefaultValueCustom){
          this.editDefaultValueCustom=editDefaultValueCustom;
    }

    String editDefaultValue="";
    public String getEditDefaultValue(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("defaultValue")){
                   editDefaultValue=((Node) item1.getData()).getValue();
                }
             }
          }
      return editDefaultValue;
    }

    public void setEditDefaultValue(String editDefaultValue){
          this.editDefaultValue=editDefaultValue;
    }

    String editTitle="";
    public String getEditTitle(){


          if (getEditParaType().equals("INPUT_PORT")){
          editTitle=editPara.getEditInPortTitle();
          }
          else if (getEditParaType().equals("OUTPUT_PORT")){
          editTitle=editPara.getEditOutPortTitle();
          }
          else if (getEditParaType().equals("DEPENDENCY")){
          editTitle=editPara.getEditDependencyTitle();
          }
          else if (getEditParaType().equals("CUSTOM")){
          editTitle=editTitleCustom;
          }

      return editTitle;
    }

    public void setEditTitle(String editTitle){
          this.editTitle=editTitle;
    }

    String editTitleCustom="";
    public String getEditTitleCustom(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("title")){
                   editTitleCustom=((Node) item1.getData()).getValue();
                }
             }
          }
      return editTitleCustom;
    }

    public void setEditTitleCustom(String editTitleCustom){
          this.editTitleCustom=editTitleCustom;
    }

    String editFixed="";
    public String getEditFixed(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("fixed")){
                   editFixed=((Node) item1.getData()).getValue();
                }
             }
          }
      return editFixed;
    }

    public void setEditFixed(String editFixed){
          this.editFixed=editFixed;
    }

    String editCmdLine="";
    public String getEditCmdLine(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("cmdLine")){
                   editCmdLine=((Node) item1.getData()).getValue();
                }
             }
          }
      return editCmdLine;
    }

    public void setEditCmdLine(String editCmdLine){
          this.editCmdLine=editCmdLine;
    }

    String editFile="";
    public String getEditFile(){

        if(getEditParaType().equals("INPUT_PORT")||getEditParaType().equals("OUTPUT_PORT"))
         {
             editFile=editPara.getIsEditPortFile().toString();
         }
         if(getEditParaType().equals("DEPENDENCY"))
         {
             editFile=editPara.getIsEditDepFile().toString();
         }
         if(getEditParaType().equals("CUSTOM"))
         {
             editFile=editFileCustom;
         }
      return editFile;
    }

    public void setEditFile(String editFile){
          this.editFile=editFile;
    }

    String editFileCustom="";
    public String getEditFileCustom(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("file")){
                   editFileCustom=((Node) item1.getData()).getValue();
                }
             }
          }
      return editFileCustom;
    }

    public void setEditFileCustom(String editFileCustom){
          this.editFileCustom=editFileCustom;
    }

    String editInput="";
    public String getEditInput(){

         if(getEditParaType().equals("INPUT_PORT")
                 ||getEditParaType().equals("DEPENDENCY")
                 ||getEditParaType().equals("OUTPUT_PORT"))
         {
             editInput=editPara.getIsEditInput().toString();
         }

         if(getEditParaType().equals("CUSTOM"))
         {
             editInput=editInputCustom;
         }
      return editInput;
    }

    public void setEditInput(String editInput){
          this.editInput=editInput;
    }

    String editInputCustom="";
    public String getEditInputCustom(){

          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("input")){
                   editInputCustom=((Node) item1.getData()).getValue();
                }
             }
          }
      return editInputCustom;
    }

    public void setEditInputCustom(String editInputCustom){
          this.editInputCustom=editInputCustom;
    }

    public void updateWithEditedValues(){
          TreeNode item1;
          Iterator<TreeNode> iterator1;
          if (newParameterIdNode!=null && newParameterIdNode.getTreeNode()!=null
                 && newParameterIdNode.getTreeNode().getChildren()!=null){
          iterator1 = newParameterIdNode.getTreeNode().getChildren().iterator();
          while(iterator1.hasNext()){
                item1=iterator1.next();
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("type")){
                   ((Node) item1.getData()).setValue(editParaType);
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("portId")){
                   ((Node) item1.getData()).setValue(editIdOfSelectedType);
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("switchName")){
                   ((Node) item1.getData()).setValue(editSwitchName);
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("defaultValue")){
                   ((Node) item1.getData()).setValue(getFillDefaultValue());
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("title")){
                   ((Node) item1.getData()).setValue(getEditTitle());
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("fixed")){
                   ((Node) item1.getData()).setValue(editFixed);
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("cmdLine")){
                   ((Node) item1.getData()).setValue(editCmdLine);
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("file")){
                   ((Node) item1.getData()).setValue(getEditFile());
                }
                if(item1!=null && item1.getData()!=null && ((Node) item1.getData()).getKey().equals("input")){
                   ((Node) item1.getData()).setValue(getEditInput());
                }

             }
          }
        selectedNode.setValue(fileSelValue);
    }

    String orderSwitchName;
    public String getOrderSwitchName(String paraID){
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        if(((Node) item2.getData()).getKey().equals(paraID)){
                             while(iterator3.hasNext()){
                                 item3=iterator3.next();
                                 if(((Node) item3.getData()).getKey().equals("switchName")){
                                      orderSwitchName=((Node) item3.getData()).getValue();
                                    }
                                }
                            }
                        }
                   }
      return orderSwitchName;
    }

    String orderDefaultValue;
    public String getOrderDefaultValue(String paraID){
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

            iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();

                 while(iterator2.hasNext()){
                     item2=iterator2.next();
                     iterator3 = item2.getChildren().iterator();
                     if(((Node) item2.getData()).getKey().equals(paraID)){
                        while(iterator3.hasNext()){
                            item3=iterator3.next();
                            if(((Node) item3.getData()).getKey().equals("defaultValue")){
                                orderDefaultValue=((Node) item3.getData()).getValue();
                               }
                           }
                       }
                    }
                }
      return orderDefaultValue;
    }

    String orderTitle;
    public String getOrderTitle(String paraID){
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        if(((Node) item2.getData()).getKey().equals(paraID)){
                            while(iterator3.hasNext()){
                                item3=iterator3.next();
                                if(((Node) item3.getData()).getKey().equals("title")){
                                    orderTitle=((Node) item3.getData()).getValue();
                                 }
                              }
                         }
                    }
               }
      return orderTitle;
    }

     String orderFixed;
     public String getOrderFixed(String paraID){
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        if(((Node) item2.getData()).getKey().equals(paraID)){
                            while(iterator3.hasNext()){
                                item3=iterator3.next();
                                if(((Node) item3.getData()).getKey().equals("fixed")){
                                     orderFixed=((Node) item3.getData()).getValue();
                                 }
                            }
                       }
                  }
             }
      return orderFixed;
    }

    String orderFile;
    public String getOrderFile(String paraID){
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        if(((Node) item2.getData()).getKey().equals(paraID)){
                           while(iterator3.hasNext()){
                               item3=iterator3.next();
                               if(((Node) item3.getData()).getKey().equals("file")){
                                    orderFile=((Node) item3.getData()).getValue();
                                }
                            }
                       }
                   }
              }
      return orderFile;
    }

    String orderInput;
    public String getOrderInput(String paraID){
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        if(((Node) item2.getData()).getKey().equals(paraID)){
                            while(iterator3.hasNext()){
                                item3=iterator3.next();
                                if(((Node) item3.getData()).getKey().equals("input")){
                                    orderInput=((Node) item3.getData()).getValue();
                                 }
                             }
                         }
                     }
                }
      return orderInput;
    }

    String userName;
    public String getUserName() {
       userName=currentUser.getLoginName();
      return userName;
    }

    String userEmail;
    public String getUserEmail(){
       userEmail=currentUser.getEmail();
       return userEmail;
    }

    public void setDep(String depTypeValue) {
        this.depTypeValue = depTypeValue;
    }


    public String getDepTypeValue() {
        depTypeValue = selectedNode.getValue();
        return depTypeValue;
    }

    public void setDepTypeValue(String depTypeValue) {
        this.depTypeValue = depTypeValue;
    }
    String maxWallTime ="1000";
    public String getMaxWallTime() {

        return maxWallTime;
    }

    public void setMaxWallTime(String maxWallTime) {
        this.maxWallTime = maxWallTime;
    }

    String maxParallelism="100";
    public String getMaxParallelism() {

        return maxParallelism;
    }

    public void setMaxParallelism(String maxParallelism) {
        this.maxParallelism = maxParallelism;
    }

    public String getFileSelValue() {
        fileSelValue = selectedNode.getValue();
        return fileSelValue;
    }

    public void setFileSelValue(String comboSelValue) {
        this.fileSelValue = comboSelValue;
    }

    public String getTextEditValue() {
        textEditValue = selectedNode.getValue();
        return textEditValue;
    }

    public void setTextEditValue(String textEditValue) {
        this.textEditValue = textEditValue;
    }

    public void updateValueWithTextEdit(){
        selectedNode.setValue(textEditValue);
    }

    public void updateUserName(){
        selectedNode.setValue(getWfDefinition());

    }
    public void updateUserEmail(){
        selectedNode.setValue(fileSelValue);
    }

     public void updateValueWithFileSel(){
        selectedNode.setValue(fileSelValue);
    }

     public void updateValueWithWfSel(){
        selectedNode.setValue(impSelValue);
    }

    public void updateValueWithDepType(){
        selectedNode.setValue(depTypeValue);
    }

    public Config getNewConfig() {
        return newConfig;
    }

    public void setNewConfig(Config newConfig) {
        this.newConfig = newConfig;
    }

    public Dependency getNewDep() {
        return newDep;
    }

    public void setNewDep(Dependency newDep) {
        this.newDep = newDep;
    }
    public Parameter getNewPara() {
        return newPara;
    }

    public void setNewPara(Parameter newPara) {
        this.newPara = newPara;
    }

    public Parameter getEditPara() {
        return editPara;
    }

    public void setEditPara(Parameter editPara) {
        this.editPara = editPara;
    }

    String[] depPool;
    String[] depTypePool;
    String[] paraPool;

    public String[] getConfigPool() {
        return configPool;
    }

    public void setConfigPool(String[] configPool) {
        this.configPool = configPool;
    }

    public String[] getDepPool() {
        return depPool;
    }

    public void setDepPool(String[] depPool) {
        this.depPool = depPool;
    }
    String[] configPool;

    @Override
    public TreeNode addChildren(TreeNode tNode, Node node){
        if(node != null
                && node.getKey() != null
                && node.getKey().startsWith("dep")
                && tNode.getData() != null
                && tNode.getData().getClass() == Node.class
                && ((Node) tNode.getData()).getKey() != null
                && ((Node) tNode.getData()).getKey().equals("dependencies"))
        {
            TreeNode confNode = super.addChildren(tNode, node);
            // adding type to each depXXXX
            super.addChildren(confNode, new Node("type"));
            return confNode;
        }
        return super.addChildren(tNode, node);
    }

    ImpAttrTree() {
        super();
        dependencies = (TreeNode) new DefaultTreeNode(new Node("dependencies"), root);
        ( (Node) dependencies.getData()).setTreeNode(dependencies);
        configurations = (TreeNode) new DefaultTreeNode(new Node("configurations"), root);
        ( (Node) configurations.getData()).setTreeNode(configurations);
        title = (TreeNode) new DefaultTreeNode(new Node("title"), root);
        ( (Node) title.getData()).setTreeNode(title);
        description = (TreeNode) new DefaultTreeNode(new Node("description"), root);
        ( (Node) description.getData()).setTreeNode(description);
        definition = (TreeNode) new DefaultTreeNode(new Node("definition"), root);
        ( (Node) definition.getData()).setTreeNode(definition);
        graph = (TreeNode) new DefaultTreeNode(new Node("graph"), root);
        ( (Node) graph.getData()).setTreeNode(graph);
        language = (TreeNode) new DefaultTreeNode(new Node("language"), root);
        ( (Node) language.getData()).setTreeNode(language);
        rights = (TreeNode) new DefaultTreeNode(new Node("rights"), root);
        ( (Node) rights.getData()).setTreeNode(rights);
        licence = (TreeNode) new DefaultTreeNode(new Node("licence"), root);
        ( (Node) licence.getData()).setTreeNode(licence);
        keywords = (TreeNode) new DefaultTreeNode(new Node("keywords"), root);
        ( (Node) keywords.getData()).setTreeNode(keywords);
        uuid = (TreeNode) new DefaultTreeNode(new Node("uuid"), root);
        ( (Node) uuid.getData()).setTreeNode(uuid);
        execution = (TreeNode) new DefaultTreeNode(new Node("Submission Execution Node"), root);
        ( (Node) execution.getData()).setTreeNode(execution);

        depPool = generatePool("dep",100);
        configPool = generatePool("conf",100);
        paraPool = generatePool("para",30);

        depTypePool = new String[7];
        depTypePool[0] = "Binary";
        depTypePool[1] = "Data Resource";
        depTypePool[2] = "DCI";
        depTypePool[3] = "Library";
        depTypePool[4] = "Service";
        depTypePool[5] = "Sub Workflow";
        depTypePool[6] = "Other";

    }

    public ImpAttrTree(List<AttributeTO> aList)
    {
        this();
        super.setAttrTree(aList);
    }

    public String[] getDepTypePool() {
        return depTypePool;
    }

    public void setDepTypePool(String[] depTypePool) {
        this.depTypePool = depTypePool;
    }

    public String[] getParaTypePool() {
        return paraPool;
    }

    public void setParaTypePool(String[] paraPool) {
        this.paraPool = paraPool;
    }

    private void getParaAttr(TreeNode treeNode, boolean isInput, TreeNode parent, String portId){
        TreeNode item1;
        String name="";
        String value="";
        String port="OUTPUT_PORT";
        if(isInput){
            port="INPUT_PORT";
        }
        TreeNode result = (TreeNode) new DefaultTreeNode(new Node("type",port), parent);
        ( (Node) result.getData()).setTreeNode(result);
        Iterator index1 = treeNode.getChildren().iterator();
        
        boolean isFile=false;
        while (index1.hasNext()){
            item1 = (TreeNode) index1.next();
            name = ( (Node) item1.getData()).getName();
            value = ( (Node) item1.getData()).getValue();
            if (name.equals("datatype") && value.equals("file")){
                isFile=true;
            }
            if (name.equals("title")){                                   
                    result = (TreeNode) new DefaultTreeNode(new Node("portId",portId), parent);
                    ( (Node) result.getData()).setTreeNode(result);
                    result = (TreeNode) new DefaultTreeNode(new Node("title", value), parent);
                    ( (Node) result.getData()).setTreeNode(result);
                    result = (TreeNode) new DefaultTreeNode(new Node("defaultValue",getDefaultValueOfParameter(portId)), parent);
                    ( (Node) result.getData()).setTreeNode(result);
                    result = (TreeNode) new DefaultTreeNode(new Node("cmdLine", "true"), parent);
                    ( (Node) result.getData()).setTreeNode(result);
                    result = (TreeNode) new DefaultTreeNode(new Node("switchName", ""), parent);
                    ( (Node) result.getData()).setTreeNode(result);
                    result = (TreeNode) new DefaultTreeNode(new Node("fixed", "false"), parent);
                    ( (Node) result.getData()).setTreeNode(result);
                    if(isFile){
                        result = (TreeNode) new DefaultTreeNode(new Node("file", "true"), parent);
                        ( (Node) result.getData()).setTreeNode(result);
                    }else{
                        result = (TreeNode) new DefaultTreeNode(new Node("file", "false"), parent);
                        ( (Node) result.getData()).setTreeNode(result);
                    }
                    if(isInput){
                          result = (TreeNode) new DefaultTreeNode(new Node("input", "true"), parent);
                        ( (Node) result.getData()).setTreeNode(result);
                    }else{
                        result = (TreeNode) new DefaultTreeNode(new Node("input", "false"), parent);
                        ( (Node) result.getData()).setTreeNode(result);
                    }
                break;
            }
        }
        
        
    }

    private String getDefaultValueOfParameter(String portId){
        TreeNode item1;
        String paramId;
        String value = "";
        if(aTree.configurations.getChildCount() != 0){
        Iterator index1 = aTree.configurations.getChildren().get(0).getChildren().iterator();
        while (index1.hasNext()){
            item1 = (TreeNode) index1.next();
            paramId = ( (Node) item1.getData()).getName();
            if (paramId.equals(portId)){
                value = ( (Node) item1.getData()).getValue();
                break;
            }
        }
        }
        return value;
    }
    public void addExecutionAttr(){
                TreeNode newMaxWallTime = (TreeNode) new DefaultTreeNode(new Node("maxWallTime",getMaxWallTime()), execution);
                ( (Node) newMaxWallTime.getData()).setTreeNode(newMaxWallTime);
                TreeNode newMaxParallelism = (TreeNode) new DefaultTreeNode(new Node("maxParallelism",getMaxParallelism()), execution);
                ( (Node) newMaxParallelism.getData()).setTreeNode(newMaxParallelism);
                TreeNode newParameters = (TreeNode) new DefaultTreeNode(new Node("parameters"),execution);
                ( (Node) newParameters.getData()).setTreeNode(newParameters);
                
                TreeNode item1;
                TreeNode item2;
                TreeNode item3;
                String paraId;
                String portId;
                String name;
                String value;
                Iterator index1 = aTree.inports.getChildren().iterator();
                Iterator index2;
                while ( index1.hasNext() ){
                    item1 = (TreeNode) index1.next();
                    portId = ( (Node) item1.getData()).getName();
                    paraId = "para" + portId.substring(portId.indexOf("0"));
                    
                    
                    //paraId = "para" + item1.getData().toString().contains("port");
                    TreeNode newParameter = (TreeNode) new DefaultTreeNode(new Node(paraId), newParameters);
                    ( (Node) newParameter.getData()).setTreeNode(newParameter);
                    
                    getParaAttr(item1, true,newParameter, portId);
                    /*TreeNode attributes = (TreeNode) new DefaultTreeNode(new Node("type", "INPUT_PORT"), newParameter);
                    ( (Node) attributes.getData()).setTreeNode(attributes);
                    attributes = (TreeNode) new DefaultTreeNode(new Node("portId", portId), newParameter);
                    ( (Node) attributes.getData()).setTreeNode(attributes);
                    index2 = item1.getChildren().iterator();
                    while (index2.hasNext()){
                        item2 = (TreeNode) index2.next();
                        name = ( (Node) item2.getData()).getName();
                        value = ( (Node) item2.getData()).getValue();
                        
                        if ( name.equals("title")){
                            attributes = (TreeNode) new DefaultTreeNode(new Node(name,value), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            
                            
                        }else if(name.equals("datatype") && value.equals("file")){
                            attributes = (TreeNode) new DefaultTreeNode(new Node("defaultValue", getDefaultValueOfParameter(portId)), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("cmdLine", "true"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("switchName", ""), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("fixed", "false"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("file", "true"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            
                        }else if(name.equals("datatype") && !value.equals("file")){
                            attributes = (TreeNode) new DefaultTreeNode(new Node("defaultValue", getDefaultValueOfParameter(portId)), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("cmdLine", "true"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("switchName", ""), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("fixed", "false"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("file", "false"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                        }
                        
                    }
                    attributes = (TreeNode) new DefaultTreeNode(new Node("input", "true"), newParameter);
                    ( (Node) attributes.getData()).setTreeNode(attributes);
                 */  
                }
                index1 = aTree.outports.getChildren().iterator();
                while ( index1.hasNext() ){
                    item1 = (TreeNode) index1.next();
                    portId = ( (Node) item1.getData()).getName();
                    paraId = "para" + portId.substring(portId.indexOf("0"));
                    
                    //paraId = "para" + item1.getData().toString().contains("port");
                    TreeNode newParameter = (TreeNode) new DefaultTreeNode(new Node(paraId), newParameters);
                    ( (Node) newParameter.getData()).setTreeNode(newParameter);
                    
                    getParaAttr(item1, false,newParameter, portId);
                    /*TreeNode attributes = (TreeNode) new DefaultTreeNode(new Node("type", "OUTPUT_PORT"), newParameter);
                    ( (Node) attributes.getData()).setTreeNode(attributes);
                    attributes = (TreeNode) new DefaultTreeNode(new Node("portId", portId), newParameter);
                    ( (Node) attributes.getData()).setTreeNode(attributes);
                    index2 = item1.getChildren().iterator();
                    Parameter p = new Parameter();
                    while (index2.hasNext()){
                        item2 = (TreeNode) index2.next();
                        name = ( (Node) item2.getData()).getName();
                        value = ( (Node) item2.getData()).getValue();
                        
                        if ( name.equals("title")){
                            attributes = (TreeNode) new DefaultTreeNode(new Node(name,value), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes); 
                            
                            
                        }else if(name.equals("datatype") && value.equals("file")){
                            attributes = (TreeNode) new DefaultTreeNode(new Node("defaultValue", getDefaultValueOfParameter(portId)), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("cmdLine", "false"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("switchName", ""), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("fixed", "false"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("file", "true"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            
                        }else if(name.equals("datatype") && !value.equals("file")){
                            attributes = (TreeNode) new DefaultTreeNode(new Node("defaultValue", getDefaultValueOfParameter(portId)), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("cmdLine", "true"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("switchName", ""), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("fixed", "false"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                            attributes = (TreeNode) new DefaultTreeNode(new Node("file", "false"), newParameter);
                            ( (Node) attributes.getData()).setTreeNode(attributes);
                        }
                        
                    }
                    attributes = (TreeNode) new DefaultTreeNode(new Node("input", "false"), newParameter);
                    ( (Node) attributes.getData()).setTreeNode(attributes);
                   */
                }
                
               
                          
                
                selectedNode.setValue("");


    }

    public void addDeployer(){
        TreeNode newDeployer = (TreeNode) new DefaultTreeNode(new Node("deployer"),execution);
                ( (Node) newDeployer.getData()).setTreeNode(newDeployer);
                TreeNode newUserName = (TreeNode) new DefaultTreeNode(new Node("username",getUserName()),newDeployer);
                ( (Node) newUserName.getData()).setTreeNode(newUserName);
                TreeNode newUserEmail = (TreeNode) new DefaultTreeNode(new Node("email",getUserEmail()), newDeployer);
                ( (Node) newUserEmail.getData()).setTreeNode(newUserEmail);
                 System.out.println("Deployer is created.......................");
                selectedNode.setValue("");
    }

    public void removeDeployer() {
        TreeNode item;

        Iterator<TreeNode> iterator = execution.getChildren().iterator();
        List<TreeNode> itemsToRemove = new ArrayList<TreeNode>();
        while(iterator.hasNext()){
            item = iterator.next();
            if(item!=null
                && item.getData()!=null
                && (((Node) item.getData()).getKey().equals("deployer")))
            {
                itemsToRemove.add(item);
                //item.getParent().getChildren().remove(item);
            }
        }
        execution.getChildren().removeAll(itemsToRemove);
    }

    public void clearEditedData() {
    editParaType="";
    }

    public String clearEditDialog(){
        String clearDialog;
        clearDialog="EditParametersDialog.show();";
      return clearDialog;
    }

    public Node getParameterNode() {
        return newParameterIdNode;
    }

    public void setParameterNode(Node newParameterIdNode) {
        this.newParameterIdNode = newParameterIdNode;
    }

    public Node getParameterTypeNode() {
        return newParameterTypeNode;
    }

    public void setParameterTypeNode(Node newParameterTypeNode) {
        this.newParameterTypeNode = newParameterTypeNode;
    }

    public boolean canRemoveExecution(Node node){

       if(node.getKey().equals("Submission Execution Node")&& !doesExecutionExist()) {
            return true;
         }
      return false;
    }

    public void removeExecution() {

         execution.getParent().getChildren().remove(execution);
         execution = (TreeNode) new DefaultTreeNode(new Node("Submission Execution Node"), root);
        ( (Node) execution.getData()).setTreeNode(execution);
    }

    public boolean doesExecutionExist() {
          TreeNode item;

          Iterator<TreeNode> iterator = execution.getChildren().iterator();
          while(iterator.hasNext()){
              item = iterator.next();
              return false;
          }

          return true;
    }

    public void addDependency(){
        if(selectedNode!=null){
            String depId = newDep.getDepId();
            if(canAddDepId(depId))
            {
                TreeNode selectedTreeNode = selectedNode.getTreeNode();

                TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node(depId), selectedTreeNode);
                ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
                TreeNode newTitle = (TreeNode) new DefaultTreeNode(new Node("title",newDep.getTitle()), newTreeNode);
                ( (Node) newTitle.getData()).setTreeNode(newTitle);
                TreeNode newType = (TreeNode) new DefaultTreeNode(new Node("type",newDep.getType()), newTreeNode);
                ( (Node) newType.getData()).setTreeNode(newType);
                TreeNode newDescription = (TreeNode) new DefaultTreeNode(new Node("description",newDep.getDescription()), newTreeNode);
                ( (Node) newDescription.getData()).setTreeNode(newDescription);
                selectedNode.setValue("");
             }else{
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot create new configuration! Configuration ID '"+depId+"' already exists.", null);
           }
        }
    }

    public void addConfig()
    {
        if(selectedNode!=null){
            String configId = newConfig.getConfigId();
            if(canAddConfId(configId))
            {
                TreeNode selectedTreeNode = selectedNode.getTreeNode();

                TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node(configId), selectedTreeNode);
                ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
                /*TreeNode newPortId = (TreeNode) new DefaultTreeNode(new Node("portref",newConfig.getPortId()), newTreeNode);
                ( (Node) newPortId.getData()).setTreeNode(newPortId);
                TreeNode newValue = (TreeNode) new DefaultTreeNode(new Node("value",newConfig.getValue()), newTreeNode);
                ( (Node) newValue.getData()).setTreeNode(newValue);
                selectedNode.setValue("");*/
            }else{
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot create new configuration! Configuration ID '"+configId+"' already exists.", null);
            }
        }
    }
    public void addExecuParametre(){

        if(selectedNode!=null){
            String paraId = newPara.getParaID();

           if(canAddParaId(paraId))
            {
                if (canAddType())
                {
                    TreeNode selectedTreeNode = selectedNode.getTreeNode();

                    TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node(paraId), selectedTreeNode);
                    ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
                    TreeNode newType = (TreeNode) new DefaultTreeNode(new Node("type",newPara.getParamterType()), newTreeNode);
                    ( (Node) newType.getData()).setTreeNode(newType);
                    if (!(newPara.getParamterType().equals("CUSTOM"))){
                        TreeNode newPortId = (TreeNode) new DefaultTreeNode(new Node("portId",newPara.getIdOfSelectedType()), newTreeNode);
                        ( (Node) newPortId.getData()).setTreeNode(newPortId);
                    }
                    TreeNode newTitle = (TreeNode) new DefaultTreeNode(new Node("title",newPara.getTitle()), newTreeNode);
                    ( (Node) newTitle.getData()).setTreeNode(newTitle);
                    TreeNode newDefaultValue = (TreeNode) new DefaultTreeNode(new Node("defaultValue",newPara.getDefaultValue()), newTreeNode);
                    ( (Node) newDefaultValue.getData()).setTreeNode(newDefaultValue);
                    TreeNode newCmdLine = (TreeNode) new DefaultTreeNode(new Node("cmdLine",newPara.getCmdLine()), newTreeNode);
                    ( (Node) newCmdLine.getData()).setTreeNode(newCmdLine);
                    TreeNode newSwitchName = (TreeNode) new DefaultTreeNode(new Node("switchName",newPara.getSwitchName()), newTreeNode);
                    ( (Node) newSwitchName.getData()).setTreeNode(newSwitchName);
                    TreeNode newFixed = (TreeNode) new DefaultTreeNode(new Node("fixed",newPara.getFixed()), newTreeNode);
                    ( (Node) newFixed.getData()).setTreeNode(newFixed);
                    TreeNode newFile = (TreeNode) new DefaultTreeNode(new Node("file",newPara.getAsStringIsFile()), newTreeNode);
                    ( (Node) newFile.getData()).setTreeNode(newFile);
                    TreeNode newInput = (TreeNode) new DefaultTreeNode(new Node("input",newPara.getIsParameterInput()), newTreeNode);
                    ( (Node) newInput.getData()).setTreeNode(newInput);

                    selectedNode.setValue("");

                }else
                    {
                   addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot create new parameter! Select parameter type", null);
                    }
            }else{
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot create new parameter! Parameter ID '"+paraId+"' already exists.", null);
            }
         }
    }

   public Boolean canAddType(){
        if(newPara.getParamterType().equals("INPUT_PORT")
                ||newPara.getParamterType().equals("OUTPUT_PORT")
                ||newPara.getParamterType().equals("DEPENDENCY")
                ||newPara.getParamterType().equals("CUSTOM"))
        {
          return true;
        }

        return false;
    }

   public Boolean canEditType(){
        if(editPara.getParamterType().equals("INPUT_PORT")
                ||editPara.getParamterType().equals("OUTPUT_PORT")
                ||editPara.getParamterType().equals("DEPENDENCY")
                ||editPara.getParamterType().equals("CUSTOM"))
        {
          return true;
        }

        return false;
    }

    public void addDepToConfig()
    {
        if(selectedNode!=null && selectedNode.getTreeNode()!=null){
            String depId = newConfig.getDepRef();
            if(canAddDepIdToConf(depId,selectedNode))
            {
                TreeNode selectedTreeNode = selectedNode.getTreeNode();
                TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node(newConfig.getDepRef(), newConfig.getValue()), selectedTreeNode);
                ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
                selectedNode.setValue("");
            }else{
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot add dependency to configuration! Dependency '"+depId+"' is already added.", null);
            }        }
    }

    public void addChildDependency()
    {
        if(selectedNode!=null && selectedNode.getTreeNode()!=null){

            TreeNode selectedTreeNode = selectedNode.getTreeNode();
            TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node("dependency",newDep.getChildDep()), selectedTreeNode);
            ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
            selectedNode.setValue("");
        }
    }

    public Boolean canAddDepId(String depId){
        TreeNode item;
        if(dependencies.getChildren()!=null)
        {
            Iterator<TreeNode> iterator = dependencies.getChildren().iterator();
            while(iterator.hasNext()){
                item = iterator.next();
                if(item!=null && item.getData()!=null && ((Node) item.getData()).getKey().equals(depId)){
                    return false;
                }
            }
        }
        return true;
    }
    public Boolean canAddParaId(String paraId){
          TreeNode item1;
          TreeNode item2;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;


          iterator1 = execution.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                         if(item2!=null && item2.getData()!=null && ((Node) item2.getData()).getKey().equals(paraId)){
                    return false;
                  }
               }
           }
      return true;
    }

    public Boolean canAddConfId(String confId){
        TreeNode item;
        if(configurations.getChildren()!=null)
        {
            Iterator<TreeNode> iterator = configurations.getChildren().iterator();
            while(iterator.hasNext()){
                item = iterator.next();
                if(item!=null && item.getData()!=null && ((Node) item.getData()).getKey().equals(confId)){
                    return false;
                }
            }
        }
        return true;
    }


    public Boolean canAddDepIdToConf(String depId, Node node){
        if(node!=null
                && node.getTreeNode()!=null)
        {
            TreeNode item;
            TreeNode tNode=node.getTreeNode();
            if(tNode.getChildren()!=null)
            {
                Iterator<TreeNode> iterator = tNode.getChildren().iterator();
                while(iterator.hasNext()){
                    item = iterator.next();
                    if(item!=null && item.getData()!=null && ((Node) item.getData()).getKey().equals(depId)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Boolean canAddConfig(Node node) {
        if(node.getKey().equals("configurations")) {
            return true;
        }
        return false;
    }

    public Boolean canAddDep(Node node) {
        if(node.getKey().equals("dependencies")) {
            return true;
        }
        return false;
    }

    public Boolean canAddDepToConfig(Node node) {
        if(node!=null && node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getData()!=null
                && node.getTreeNode().getParent().getData().getClass()==Node.class) {
            if( ((Node) node.getTreeNode().getParent().getData()).getKey().equals("configurations")){
                return true;
            }
        }
        return false;
    }

    public Boolean canAddChildDep(Node node){
        if(node!=null && node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getData()!=null
                && node.getTreeNode().getParent().getData().getClass()==Node.class) {
            if( ((Node) node.getTreeNode().getParent().getData()).getKey().equals("dependencies")){
                return true;
            }
        }
        return false;
    }

    public Boolean canEditExecutionParameters(Node node) {
        if(node!=null
                && node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getParent()!=null
                && node.getTreeNode().getParent().getParent().getData()!=null
                && node.getTreeNode().getParent().getParent().getData().getClass()==Node.class
                && ((Node) node.getTreeNode().getParent().getParent().getData()).getKey().equals("parameters") )
        {
            return true;
        }
        return false;
    }
    public Boolean canEditDeployer(Node node) {

        if(node.getKey().equals("username")) {
            return true;
        }
        if(node.getKey().equals("email")) {
            return true;
        }
        return false;
    }

    public Boolean canAddExecutionParameters(Node node){
       if (node.getKey().equals("parameters")) {
           return true;
       }
        return false;
    }

    public Boolean canAddExecutionAttr(Node node){
        if(node.getKey().equals("Submission Execution Node")) {
            return true;
        }
        return false;
    }
   public Boolean canEditParameters(Node node){
       if(node!=null && node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getData()!=null
                && node.getTreeNode().getParent().getData().getClass()==Node.class)
        {
            if(( (Node) node.getTreeNode().getParent().getData()).getKey().equals("parameters"))
            {
                return true;
            }
        }
        return false;
    }

    public Boolean canEdit(Node node) {
        if(node.getTreeNode()!=null
                &&  node.getTreeNode().isLeaf()
                && !canAddDep(node)
                && !canAddConfig(node)
                && !canAddDepToConfig(node)
                && !canEditConf(node)
                && !canEditDepType(node)
                && !canAddExecutionParameters(node)
                && !canAddExecutionAttr(node)
                && !canEditExecutionParameters(node)
                && !canEditParameters(node)
                && !canEditDeployer(node))
        {
            return true;
        }
        return false;
    }

    public Boolean canEditConf(Node node) {
        if(node!=null
                && node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getParent()!=null
                && node.getTreeNode().getParent().getParent().getData()!=null
                && node.getTreeNode().getParent().getParent().getData().getClass()==Node.class
                && ((Node) node.getTreeNode().getParent().getParent().getData()).getKey().equals("configurations") )
        {
            return true;
        }
        return false;
    }

    public Boolean canEditDepType(Node node) {
        if(node!=null
                && node.getKey().equals(newDep.getType())
                /*&& node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getData()!=null
                && node.getTreeNode().getParent().getData().getClass()==Node.class
                && ((Node) node.getTreeNode().getParent().getData()).getKey().equals("dependencies") */)
        {
            return true;
        }
        return false;
    }


    public Boolean canRemove(Node node) {

        if(node!=null && node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getData()!=null
                && node.getTreeNode().getParent().getData().getClass()==Node.class)
        {
            if(( (Node) node.getTreeNode().getParent().getData()).getKey().equals("dependencies") ||
               ( (Node) node.getTreeNode().getParent().getData()).getKey().equals("configurations")||
               ( (Node) node.getTreeNode().getParent().getData()).getKey().equals("parameters"))
            {
                return true;
            }
            if(node.getTreeNode().getParent().getParent()!=null
                    && node.getTreeNode().getParent().getParent().getData()!=null
                    && node.getTreeNode().getParent().getParent().getData().getClass()==Node.class){
                if(( (Node) node.getTreeNode().getParent().getParent().getData()).getKey().equals("configurations"))
                {
                    return true;
                }
            }
            if(node.getTreeNode().getParent().getParent()!=null
                    && node.getTreeNode().getParent().getParent().getData()!=null
                    && node.getTreeNode().getParent().getParent().getData().getClass()==Node.class){
                if(( (Node) node.getTreeNode().getParent().getParent().getData()).getKey().equals("dependencies")
                        && node.getKey().equals("dependency"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public class Dependency {
        String depId="";
        String type="";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        String title="";

        public String getChildDep() {
            return childDep;
        }

        public void setChildDep(String childDep) {
            this.childDep = childDep;
        }

        public String getDepId() {
            return depId;
        }

        public void setDepId(String depId) {
            this.depId = depId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        String description;
        String childDep;
    }

    public class Config{
        String configId="";
        String depRef="";
        String value;

        public String getConfigId() {
            return configId;
        }

        public void setConfigId(String configId) {
            this.configId = configId;
        }

        public String getDepRef() {
            return depRef;
        }

        public void setDepRef(String depRef) {
            this.depRef = depRef;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }


  }

   public class Parameter {
       List<String> idList= new ArrayList<String>();
       List<String> defValueList= new ArrayList<String>();

        public List<String> getDefValueListOfInputPort(){
            List<String> defaultValueListOfInputPort=new ArrayList<String>();
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;

                iterator1 = aTree.root.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                    if(((Node) item1.getData()).getKey().equals("configurations")){
                        while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                            while(iterator3.hasNext()){
                            item3=iterator3.next();
                            for(int i=0; i<getInPortList().size(); i++){
                                if((getInPortList().get(i) !=null
                                && getInPortList().get(i).equals(((Node) item3.getData()).getName()))){
                                    defaultValueListOfInputPort.add(((Node) item3.getData()).getValue());
                                }
                              }
                            }
                         }
                     }
                  }

        return defaultValueListOfInputPort;
        }

        public List<String> getDefValueListOfSinglePort(String selectedId){
            List<String> defaultValueListOfSingleInputPort=new ArrayList<String>();
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;

                iterator1 = aTree.root.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                    if(((Node) item1.getData()).getKey().equals("configurations")){
                        while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        while(iterator3.hasNext()){
                            item3=iterator3.next();
                            if((selectedId !=null
                            && selectedId.equals(((Node) item3.getData()).getName()))){
                                defaultValueListOfSingleInputPort.add(((Node) item3.getData()).getValue());
                              }
                            }
                         }
                     }
                  }

        return defaultValueListOfSingleInputPort;
        }

        public List<String> getDefValueListOfOutPort(){
            List<String> defaultValueListOfOutPort=new ArrayList<String>();
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;

                iterator1 = aTree.root.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                    if(((Node) item1.getData()).getKey().equals("configurations")){
                        while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                            while(iterator3.hasNext()){
                            item3=iterator3.next();
                            for(int i=0; i<getOutPortList().size(); i++){
                                if((getOutPortList().get(i) !=null
                                && getOutPortList().get(i).equals(((Node) item3.getData()).getName()))){
                                    defaultValueListOfOutPort.add(((Node) item3.getData()).getValue());
                                 }
                              }
                           }
                        }
                     }
                 }

        return defaultValueListOfOutPort;
        }

        String inPortTitle;
        public String getInPortTitle(){
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;

            iterator1 = aTree.root.getChildren().iterator();
            while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            iterator3 = item2.getChildren().iterator();
                            if(((Node) item2.getData()).getKey().equals(newPara.getIdOfSelectedType())){
                                while(iterator3.hasNext()){
                                    item3=iterator3.next();
                                    if(((Node) item3.getData()).getKey().equals("title")){
                                        inPortTitle=((Node) item3.getData()).getValue();
                                    }
                                  }
                                }
                              }
                            }
        return inPortTitle;
        }

        String editInPortTitle;
        public String getEditInPortTitle(){
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;

            iterator1 = aTree.root.getChildren().iterator();
            while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            iterator3 = item2.getChildren().iterator();
                            if(((Node) item2.getData()).getKey().equals(getEditIdOfSelectedType())){
                                while(iterator3.hasNext()){
                                    item3=iterator3.next();
                                    if(((Node) item3.getData()).getKey().equals("title")){
                                        editInPortTitle=((Node) item3.getData()).getValue();
                                    }
                                  }
                                }
                              }
                            }
        return editInPortTitle;
        }
        public void setInPortTitle(String inPortTitle) {
            this.inPortTitle = inPortTitle;
        }

        String editOutPortTitle;
        public String getEditOutPortTitle(){
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;

            iterator1 = aTree.root.getChildren().iterator();
            while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            iterator3 = item2.getChildren().iterator();
                            if(((Node) item2.getData()).getKey().equals(getEditIdOfSelectedType())){
                                while(iterator3.hasNext()){
                                    item3=iterator3.next();
                                    if(((Node) item3.getData()).getKey().equals("title")){
                                        editOutPortTitle=((Node) item3.getData()).getValue();
                                    }
                                  }
                                }
                              }
                            }
        return editOutPortTitle;
        }
        public void setOutPortTitle(String outPortTitle) {
            this.outPortTitle = outPortTitle;
        }

        String outPortTitle;
        public String getOutPortTitle(){
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;

            iterator1 = aTree.root.getChildren().iterator();
                while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        if(((Node) item2.getData()).getKey().equals(newPara.getIdOfSelectedType())){
                        while(iterator3.hasNext()){
                            item3=iterator3.next();
                            if(((Node) item3.getData()).getKey().equals("title")){
                                outPortTitle=((Node) item3.getData()).getValue();
                                }
                            }
                        }
                    }
                }
        return outPortTitle;
        }

        public List<String> getInPortList() {
            List<String> inputPortList=new ArrayList<String>();
            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = aTree.root.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                    if(((Node) item1.getData()).getKey().equals("inports")){
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            inputPortList.add(((Node) item2.getData()).getName());
                            }
                        }
                    }
        return inputPortList;
        }

        public List<String> getOutPortList() {
            List<String> outputPortList= new ArrayList<String>();
            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = aTree.root.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                    if(((Node) item1.getData()).getKey().equals("outports")){
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            outputPortList.add(((Node) item2.getData()).getName());
                            }
                        }
                    }
        return outputPortList;

        }

        public List<String> getDefValueListOfDependency() {
            List<String> defValueListOfDependency= new ArrayList<String>();
            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = configurations.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            defValueListOfDependency.add(((Node) item2.getData()).getValue());
                            }
                        }
        return defValueListOfDependency;
        }

        public List<String> getDefValueListOfSingleDependency(String selectedId) {
            List<String> defValueListOfSingleDependency= new ArrayList<String>();
            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = configurations.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            if((selectedId !=null
                            && selectedId.equals(((Node) item2.getData()).getName()))){
                            defValueListOfSingleDependency.add(((Node) item2.getData()).getValue());
                            }
                        }
                   }
        return defValueListOfSingleDependency;
        }

        public List<String> getDependencyList() {
            List<String> dependencyList=new ArrayList<String>();
            TreeNode item1;
            Iterator<TreeNode> iterator1;

            iterator1 = dependencies.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    dependencyList.add(((Node) item1.getData()).getName());
                    }
        return dependencyList;
        }

        String dependencyTitle;
        public String getDependencyTitle() {
            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = dependencies.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                    if(((Node) item1.getData()).getKey().equals(newPara.getIdOfSelectedType())){
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                                if(((Node) item2.getData()).getKey().equals("title")){
                                dependencyTitle=((Node) item2.getData()).getValue();
                                }
                            }
                        }
                    }
        return dependencyTitle;
        }

        String editDependencyTitle;
        public String getEditDependencyTitle() {
            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = dependencies.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    iterator2 = item1.getChildren().iterator();
                    if(((Node) item1.getData()).getKey().equals(getEditIdOfSelectedType())){
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                                if(((Node) item2.getData()).getKey().equals("title")){
                                editDependencyTitle=((Node) item2.getData()).getValue();
                                }
                            }
                        }
                    }
        return editDependencyTitle;
        }

        String paraId;
        public String getParaID() {
            return paraId;
        }

        public void setParaID(String paraId) {
            this.paraId = paraId;
        }

        String parameter;
        public void setParamter(String parameter) {
              this.parameter=parameter;
          }


        public String getParamter() {
            //parameterType=type.toString();
            return parameter;
        }

        String idOfSelectedType;
        public String getIdOfSelectedType() {

            return idOfSelectedType;
        }

        public void setIdOfSelectedType(String idOfSelectedType) {
            this.idOfSelectedType = idOfSelectedType;
        }

        String title;
        public String getTitle() {
        if(getParamterType().equals("DEPENDENCY")){
			title = getDependencyTitle();
                }
                else if (getParamterType().equals("INPUT_PORT")
                        ||getParamterType().equals("OUTPUT_PORT")){
			title = getInPortTitle();
                }
                else if (getParamterType().equals("CUSTOM")){
                       title = getTitleOfCustom();
                }
       return title;

       }
       String titleOfCustom="";
       public String getTitleOfCustom() {
            return titleOfCustom;
       }

       public void setTitleOfCustom(String titleOfCustom) {
            this.titleOfCustom = titleOfCustom;
       }

       public void setTitle(String title) {
          this.title = title;
       }

       String defValueofParameter;
       public String getDefValueofParameter() {

            return defValueofParameter;
       }

       public void setDefValueofParameter(String defValueofParameter) {
            this.defValueofParameter = defValueofParameter;
       }

       String defaultValueCustomFile;
       public String getDefaultValueCustomFile() {
           return defaultValueCustomFile;
      }

      public void setDefaultValueCustomFile(String defaultValueCustomFile) {
            this.defaultValueCustomFile = defaultValueCustomFile;
      }

      String defaultValueCustomText;
      public String getDefaultValueCustomText(){
        return defaultValueCustomText;
      }

      public void setDefaultValueCustomText(String defaultValueCustomText) {
            this.defaultValueCustomText = defaultValueCustomText;
      }

      String defaultValueCustom;
      public String getDefaultValueCustom(){
          if( getRadioButton().equals("Select File")){
              defaultValueCustom=getDefaultValueCustomFile();
              fileCustom="true";
           }
           else if(getRadioButton().equals("Enter Value")){
              defaultValueCustom=getDefaultValueCustomText();
           }
          return defaultValueCustom;
      }

      public void setDefaultValueCustom(String defaultValueCustom) {
            this.defaultValueCustom = defaultValueCustom;
      }

      public String getParameterOutputFile(){
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;
            String file="";
            iterator1 = execution.getChildren().iterator();
                while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                if(((Node) item1.getData()).getKey().equals("parameters")){
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        while(iterator3.hasNext()){
                            item3=iterator3.next();
                            if(((Node) item3.getData()).getKey().equals("file")){
                                file=((Node) item3.getData()).getValue();
                                }
                            }
                        }
                    }
                }
        return file;
      }

      public String getParameterOutputType(){
            TreeNode item1;
            TreeNode item2;
            TreeNode item3;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;
            Iterator<TreeNode> iterator3;
            String type="";
            iterator1 = execution.getChildren().iterator();
                while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();
                if(((Node) item1.getData()).getKey().equals("parameters")){
                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        iterator3 = item2.getChildren().iterator();
                        while(iterator3.hasNext()){
                            item3=iterator3.next();
                            if(((Node) item3.getData()).getKey().equals("type")){
                                type=((Node) item3.getData()).getValue();
                                }
                            }
                        }
                    }
                }
        return type;
      }

      public boolean isOutputFile(){
          if (getParameterOutputType().equals("OUTPUT_PORT")&&
                  getParameterOutputFile().equals("true")){
               return true;
          }
          return false;
      }

      String fillDefaultValueCustom;
      public String getFillDefaultValueCustom(){
          if( getRadioButton().equals("Select File")){
              fillDefaultValueCustom=getDefaultValueCustomFile();
           }
           else if(getRadioButton().equals("Enter Value")){
              fillDefaultValueCustom=editDefaultValueCustom;
           }
          return fillDefaultValueCustom;
      }

      public void setFillDefaultValueCustom(String fillDefaultValueCustom) {
            this.fillDefaultValueCustom = fillDefaultValueCustom;
      }

      public List<String> getRadioButtonList() {
           List<String> radioBulttonOption= new ArrayList<String>();
           radioBulttonOption.add("Enter Value");
           radioBulttonOption.add("Select File");


         return radioBulttonOption;
      }

      String radioButtons="Enter Value";
      public String getRadioButton() {
         return radioButtons;
      }

      public void setRadioButton(String radioButtons) {
         this.radioButtons=radioButtons;
      }

      public void handleRadioOptionChange() {

	  if( getRadioButton().equals("Select File")){
              defaultValueCustom=getDefaultValueCustomFile();
           }
           else if(getRadioButton().equals("Enter Value")){
              defaultValueCustom=getDefaultValueCustomText();
           }
      }

      public boolean isRadioOptionFile(){
           if(getRadioButton()!=null && getRadioButton().equals("Select File")){
               return true;
           }
           return false;
      }

      public boolean isRadioOptionText(){
           if(getRadioButton()!=null && getRadioButton().equals("Enter Value")){
               return true;
           }
           return false;
      }

      String defaultValue;
      public String getDefaultValue() {
            if (getParamterType().equals("INPUT_PORT")
                        ||getParamterType().equals("OUTPUT_PORT")
                        ||getParamterType().equals("DEPENDENCY")){
		defaultValue = getDefValueofParameter();
              }
            else if (getParamterType().equals("CUSTOM")){
                defaultValue = getDefaultValueCustom();

              }
         return defaultValue;
       }

       public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
       }

        String cmdLine;
        String switchName=" ";
        public String getCmdLine() {
            return cmdLine;
        }

        public void setCmdLine(String cmdLine) {
            this.cmdLine = cmdLine;
        }

        String fileCustom="";
        public String getFileCustom() {
            if(getRadioButton()!=null && getRadioButton().equals("Select File")){
                fileCustom="true";
            }
            return fileCustom;
        }

        public void setFileCustom(String fileCustom) {
            this.fileCustom = fileCustom;
        }

        public String getSwitchName() {
            return switchName;
        }

        public void setSwitchName(String switchName) {
            this.switchName = switchName;
        }

        String fixed;
        public String getFixed() {
            return fixed;
        }

        public void setFixed(String fixed) {
            this.fixed = fixed;
        }

        String nullToEmpty;
        public String nullToEmpty(String text) {
        return nullToEmpty == null ? "" : text;
        }


        public Boolean getIsDepFile() {
            String fileName="";

            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = dependencies.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    if(((Node) item1.getData()).getKey().equals(newPara.getIdOfSelectedType())){
                    iterator2 = item1.getChildren().iterator();
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            if(((Node) item2.getData()).getKey().equals("type")){
                                fileName=((Node) item2.getData()).getValue();
                              }
                           }
                        }
                    }
           if (fileName.equals("file")) {
              return true;
          }
         return false;
        }

        public Boolean getIsEditDepFile() {
            String fileName="";

            TreeNode item1;
            TreeNode item2;
            Iterator<TreeNode> iterator1;
            Iterator<TreeNode> iterator2;

            iterator1 = dependencies.getChildren().iterator();
                while(iterator1.hasNext()){
                    item1 = iterator1.next();
                    if(((Node) item1.getData()).getKey().equals(getEditIdOfSelectedType())){
                    iterator2 = item1.getChildren().iterator();
                        while(iterator2.hasNext()){
                            item2=iterator2.next();
                            if(((Node) item2.getData()).getKey().equals("type")){
                                fileName=((Node) item2.getData()).getValue();
                              }
                           }
                        }
                    }
           if (fileName.equals("file")) {
              return true;
          }
         return false;
        }

        public Boolean getIsPortFile() {
          String fileName="";
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = aTree.root.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();

                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        if(((Node) item2.getData()).getKey().equals(newPara.getIdOfSelectedType())){
                        iterator3 = item2.getChildren().iterator();
                             while(iterator3.hasNext()){
                                item3=iterator3.next();
                                if(((Node) item3.getData()).getKey().equals("datatype")){
                                      fileName=((Node) item3.getData()).getValue();

                                    }

                                }
                             }
                          }

                      }
         if (fileName.equals("file")) {
           return true;
        }
         return false;
     }

     public Boolean getIsEditPortFile() {
          String fileName="";
          TreeNode item1;
          TreeNode item2;
          TreeNode item3;
          Iterator<TreeNode> iterator1;
          Iterator<TreeNode> iterator2;
          Iterator<TreeNode> iterator3;

          iterator1 = aTree.root.getChildren().iterator();
            while(iterator1.hasNext()){
                item1 = iterator1.next();
                iterator2 = item1.getChildren().iterator();

                    while(iterator2.hasNext()){
                        item2=iterator2.next();
                        if(((Node) item2.getData()).getKey().equals(getEditIdOfSelectedType())){
                        iterator3 = item2.getChildren().iterator();
                             while(iterator3.hasNext()){
                                item3=iterator3.next();
                                if(((Node) item3.getData()).getKey().equals("datatype")){
                                      fileName=((Node) item3.getData()).getValue();

                                    }

                                }
                             }
                          }

                      }
         if (fileName.equals("file")) {
           return true;
        }
         return false;
     }

     String asStringIsFile;
     public String getAsStringIsFile(){
         if(getParamterType().equals("INPUT_PORT")||getParamterType().equals("OUTPUT_PORT"))
         {
             asStringIsFile=getIsPortFile().toString();
         }
         if(getParamterType().equals("DEPENDENCY"))
         {
             asStringIsFile=getIsDepFile().toString();
         }
         if(getParamterType().equals("CUSTOM"))
         {
             asStringIsFile=getFileCustom();
         }
      return asStringIsFile;
    }

    public void setAsStringIsFile(String asStringIsFile) {
            this.asStringIsFile = asStringIsFile;
    }

    String input;
    public Boolean getIsInput() {
         if(getParamterType().equals("INPUT_PORT")||getParamterType().equals("DEPENDENCY")){
             return true;
            }
         else return false;
    }

    public Boolean getIsEditInput() {
         if(getEditParaType().equals("INPUT_PORT")||getEditParaType().equals("DEPENDENCY")){
             return true;
            }
         else return false;
    }

    public String getAsStringIsInput() {
            return getIsInput().toString();
    }

    public void setAsStringIsInput(String input) {
            this.input = input;
    }

    String isCustomInput;
    public String getIsCustomInput() {
            return isCustomInput;
    }

    public void setIsCustomInput(String isCustomInput) {
            this.isCustomInput = isCustomInput;
    }

    String isParameterInput;
    public String getIsParameterInput() {
        if (getParamterType().equals("INPUT_PORT")
                        ||getParamterType().equals("OUTPUT_PORT")
                        ||getParamterType().equals("DEPENDENCY")){
		isParameterInput = getAsStringIsInput();
           }
         else if (getParamterType().equals("CUSTOM")){
                isParameterInput = getIsCustomInput();
           }
       return isParameterInput;
     }

    public void setIsParameterInput(String isParameterInput) {
            this.isParameterInput = isParameterInput;
    }

    public void setParamterType(String parameterType) {
              this.parameterType=parameterType;
    }

    String parameterType;
    public String getParamterType() {
            return parameterType;
    }

    public void handleTypeChange() {
            String emptySpace="";
            List<String> emptyList=new ArrayList<String>();
            emptyList.add(emptySpace);

	       if(getParamterType().equals("INPUT_PORT")){
			idList = getInPortList();
                        defValueList =getDefValueListOfSinglePort(getInPortList().get(0));
                }
                else if(getParamterType().equals("OUTPUT_PORT")){
			idList = getOutPortList();
                        defValueList = getDefValueListOfOutPort();
                }
                else if(getParamterType().equals("DEPENDENCY")){
			idList = getDependencyList();
                        defValueList= getDefValueListOfDependency();
                }
                else if(getParamterType().equals("CUSTOM")){
			//String s=newPara.getIdOfSelectedType();
                    idList= emptyList;
                }
    }

    public void handleEditTypeChange() {
            String emptySpace="";
            List<String> emptyList=new ArrayList<String>();
            emptyList.add(emptySpace);
               //editSelectedIdList.clear();

               if( editParaType.equals("INPUT_PORT")){

			editSelectedIdList = getInPortList();
                        defValueList = getDefValueListOfInputPort();
                }
                else if(editParaType.equals("OUTPUT_PORT")){

			editSelectedIdList = getOutPortList();
                        defValueList = getDefValueListOfOutPort();
                }
                else if(editParaType.equals("DEPENDENCY")){

			editSelectedIdList = getDependencyList();
                        defValueList= getDefValueListOfDependency();
                }
                else if(editParaType.equals("CUSTOM")){

                        editSelectedIdList= emptyList;
                }

    }

    public void handleDefaultValueChange() {


            if( getParamterType().equals("INPUT_PORT")&&
                    newPara.getIdOfSelectedType()!=null){
                    defValueList= getDefValueListOfSinglePort(newPara.getIdOfSelectedType());
            }
            else if(getParamterType().equals("OUTPUT_PORT")&&
                    newPara.getIdOfSelectedType()!=null){
                    defValueList= getDefValueListOfSinglePort(newPara.getIdOfSelectedType());
            }
            else if(getParamterType().equals("DEPENDENCY")&&
                    newPara.getIdOfSelectedType()!=null){
                    defValueList= getDefValueListOfSingleDependency(newPara.getIdOfSelectedType());
            }
    }

    List<String> editSelectedIdList=new ArrayList<String>();
    public List<String> getEditSelectedIdList() {
            String emptySpace="";

               if(getEditParaType().equals("INPUT_PORT")){
			editSelectedIdList = getInPortList();
                }
                else if(getEditParaType().equals("OUTPUT_PORT")){
			editSelectedIdList = getOutPortList();
                }
                else if(getEditParaType().equals("DEPENDENCY")){
			editSelectedIdList = getDependencyList();
                }

         return editSelectedIdList;
    }

    public List<String> getEditDefaultValueList() {
            List<String> editDefaultValueList=new ArrayList<String>();
            //emptyList.add(emptySpace);

               if(getEditParaType().equals("INPUT_PORT")){
                        editDefaultValueList = getDefValueListOfInputPort();
                }
                else if(getEditParaType().equals("OUTPUT_PORT")){
                        editDefaultValueList = getDefValueListOfOutPort();
                }
                else if(getEditParaType().equals("DEPENDENCY")){
                        editDefaultValueList= getDefValueListOfDependency();
                }

         return editDefaultValueList;
    }

    public List<String> getParamterTypes() {
           List<String> items = new ArrayList<String>();

           for (ParamterType typ: ParamterType.values()) {
                items.add(typ.toString());
                    }
                return items;
     }

     public List<String> getDefValueList(){
            return defValueList;
     }

     public List<String> getIDList(){
            return idList;
     }

  }

    ParamterType type;

    public boolean isParameterTypeCustom(){
           if(newPara.getParamterType()!=null && newPara.getParamterType().equals("CUSTOM")){
               return true;
         }
        return false;
    }

    public boolean isParameterTypeInput(){
           if(newPara.getParamterType()!=null && newPara.getParamterType().equals("INPUT_PORT")){
               return true;
           }
           return false;
       }

    public boolean isParameterTypeOutput(){
           if(newPara.getParamterType()!=null && newPara.getParamterType().equals("OUTPUT_PORT")){
               return true;
           }
           return false;
    }

    public boolean isParameterTypeDep(){
           if(newPara.getParamterType()!=null && newPara.getParamterType().equals("DEPENDENCY")){
               return true;
           }
           return false;
    }

    public boolean isEditParameterTypeCustom(){
           if(getEditParaType()!=null && getEditParaType().equals("CUSTOM")){
               return true;
         }
        return false;
    }

    public boolean isEditParameterTypeInput(){
           if(getEditParaType()!=null && getEditParaType().equals("INPUT_PORT")){
               return true;
           }
           return false;
       }

    public boolean isEditParameterTypeOutput(){
           if(getEditParaType()!=null && getEditParaType().equals("OUTPUT_PORT")){
               return true;
           }
           return false;
    }

    public boolean isEditParameterTypeDep(){
           if(getEditParaType()!=null && getEditParaType().equals("DEPENDENCY")){
               return true;
           }
           return false;
    }

    public boolean isEditParaTypeCustom(){
           if(getEditParaType()!=null && getEditParaType().equals("CUSTOM")){
               return true;
           }
           return false;
    }
    public boolean isEditParaTypeInput(){
           if(getEditParaType()!=null && getEditParaType().equals("INPUT_PORT")){
               return true;
           }
           return false;
       }

    public boolean isEditParaTypeOutput(){
           if(getEditParaType()!=null && getEditParaType().equals("OUTPUT_PORT")){
               return true;
           }
           return false;
    }

    public boolean isEditParaTypeDep(){
           if(getEditParaType()!=null && getEditParaType().equals("DEPENDENCY")){
               return true;
           }
           return false;
    }

    public enum ParamterType

          {
            Please_Select_One,
             INPUT_PORT,
             OUTPUT_PORT,
             DEPENDENCY,
             CUSTOM;
          }
    /*public void reloadExecutionNode(){
        
        int index = execution.getChildren().size() - 1;
        TreeNode parent = execution.getChildren().get(index);
        Iterator index3 = aTree.inports.getChildren().iterator();
        Iterator index4 = aTree.outports.getChildren().iterator();
        TreeNode item1;
        TreeNode item3;
        String name, portId, paraId;
        
        if ( (aTree.inports.getChildCount() != 0 || aTree.outports.getChildCount() !=0 )&& aTree.configurations.getChildCount() == 0){
            addMessage(null, FacesMessage.SEVERITY_ERROR, "Can't refresh the workflow executable in the SSP, Dataset must be set", null);
        }else{
            while(index3.hasNext()){
            
                item1 = (TreeNode) index3.next();
                portId = ( (Node) item1.getData()).getName();
                paraId = "para" + portId.substring(portId.indexOf("0"));
                Iterator <TreeNode> parameters = parent.getChildren().iterator();

                while(parameters.hasNext()){
                    item3 = parameters.next();
                    name = ( (Node) item3.getData()).getName();
                    if(name.equals(paraId)){
                        parameters.remove();
                                      
                    }   
                }      
            }
        
            while (index4.hasNext()){
            
                item1 = (TreeNode) index4.next();
                portId = ( (Node) item1.getData()).getName();
                paraId = "para" + portId.substring(portId.indexOf("0"));
                Iterator<TreeNode> parameters = execution.getChildren().get(index).getChildren().iterator();
                while(parameters.hasNext()){
                    item3 = parameters.next();
                    name = ( (Node) item3.getData()).getName();
                    if(name.equals(paraId)){
                        parameters.remove();
                  
                    }
                }
            }
            getAttrForReload();
        }
    }
    
    private void getAttrForReload(){
        TreeNode item1;
        String paraId;
        String portId;
        Iterator index1 = aTree.inports.getChildren().iterator();
        TreeNode newParameters = execution.getChildren().get(execution.getChildren().size()-1);
        while ( index1.hasNext() ){
            item1 = (TreeNode) index1.next();
            portId = ( (Node) item1.getData()).getName();
            paraId = "para" + portId.substring(portId.indexOf("0"));
            TreeNode newParameter = (TreeNode) new DefaultTreeNode(new Node(paraId), newParameters);
            ( (Node) newParameter.getData()).setTreeNode(newParameter);
            getParaAttr(item1, true,newParameter, portId);
            
        }
        index1 = aTree.outports.getChildren().iterator();
        while ( index1.hasNext() ){
            item1 = (TreeNode) index1.next();
            portId = ( (Node) item1.getData()).getName();
            paraId = "para" + portId.substring(portId.indexOf("0"));
            
            TreeNode newParameter = (TreeNode) new DefaultTreeNode(new Node(paraId), newParameters);
            ( (Node) newParameter.getData()).setTreeNode(newParameter);
                    
            getParaAttr(item1, false,newParameter, portId);           
        }
        
        selectedNode.setValue("");


    }*/
        
    
    
}
