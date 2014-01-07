/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import uk.ac.wmin.edgi.repository.transferobjects.AttributeTO;
/**
 *
 * @author kukla
 */
public abstract class AttrTree {

    TreeNode root;
    TreeNode modified;
    TreeNode created;
    Boolean expanded = false;
    Node selectedNode = new Node("","");


    /*
    public List<String> getNonAvailIDs(){
        List<String> nonAvailIDs = new ArrayList<String>();
        if(selectedNode!=null
                && selectedNode.getTreeNode()!=null)
        {
            TreeNode treeNode=selectedNode.getTreeNode();
            Iterator<TreeNode> iterator = treeNode.getChildren().iterator();
            TreeNode item = null;
            while(iterator.hasNext()){
                item = iterator.next();
                if(item!=null
                        && item.getData()!=null
                        && item.getData().getClass()==Node.class
                        && ((Node) item.getData()).getKey()!=null)
                {
                    nonAvailIDs.add(((Node) item.getData()).getKey());
                    //System.out.println(((Node) item.getData()).getKey());
                }
            }
        }
        return nonAvailIDs;
    }

    public String[] getAvailIDs(String[] pool){
        if(selectedNode==null
                || (selectedNode!=null && selectedNode.getTreeNode()==null)
                || (selectedNode!=null && selectedNode.getKey().equals("")))
        {
            return pool;
        }
        List<String> fullPool = Arrays.asList(pool);
        List<String> nonAvailPool = getNonAvailIDs();
        List<String> availPool = new ArrayList<String>();
        //availPool.removeAll(getNonAvailIDs());
        String item;
        String item2;

        Iterator<String> iter = fullPool.iterator();
        Iterator<String> iter2;
        outerLoop:
        for(int i=0; iter.hasNext(); i++){
            item=iter.next();
            iter2= nonAvailPool.iterator();
            while(iter2.hasNext()){
                item2 = iter2.next();
                if(item.equals(item2) || item.equals(item2)){
                   //System.out.println("Removed: "+item);
                   continue outerLoop;
                }
            }
            availPool.add(item);
        }

        iter = availPool.iterator();
        String[] rtnPool = new String[availPool.size()];
        for(int i=0; iter.hasNext(); i++){
            item=iter.next();
            rtnPool[i]=item;
            //System.out.println("Element: "+item);
        }
        return rtnPool;
    }*/

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }
    Integer itemID;

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Node getSelectedNode() {
        //System.out.println("get: "+selectedNode.getKey()+"-"+selectedNode.getValue());
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        //System.out.println("set: "+selectedNode.getKey()+"-"+selectedNode.getValue());
        this.selectedNode = selectedNode;
    }

    public TreeNode getRoot() {
        return root;
    }

    String[] generatePool(String prefix, int size){
        String[] pool = new String[size];
        String num;
        for(int i=0; i<size; i++){
            num = "000"+(i+1);
            pool[i]=prefix+num.substring(num.length()-4,num.length());
        }

        return pool;
    }

    AttrTree(){
        root = (TreeNode) new DefaultTreeNode("root", null);
    }

    public void setAttrTree(List<AttributeTO> aList)
    {
        //System.out.println("constructor invoked");
        AttributeTO item;
        String[] str;
        Iterator<AttributeTO> iterator = aList.iterator();
        TreeNode tNode;
        while(iterator.hasNext())
        {
            item = iterator.next();
            str = item.getName().split("\\.");
            tNode=root;
            //System.out.println(item.getName()+": "+str.length);
            for(int i=0; i<(str.length-1); i++)
            {
                // nonleaf gets empty value
                tNode = addChildren(tNode, new Node(str[i],""));
            }
            // leaf gets value
            if(str.length>0){
                tNode = addChildren(tNode, new Node(str[str.length-1],item.getValue()));
            }
        }
    }

    public TreeNode addChildren(TreeNode tNode, Node node){
        if(tNode==null || tNode.getChildren()==null){
            return tNode;
        }
        Iterator<TreeNode> iterator = tNode.getChildren().iterator();
        TreeNode item;
        TreeNode rtnItem=null;
        while(iterator.hasNext()){
            item = iterator.next();
            if(item!=null && item.getData()!=null && item.getData().getClass()==Node.class){
                // exists already return with the matching treenode
                if(((Node) item.getData()).getKey().equals(node.getKey())){
                    rtnItem=item;
                    //System.out.println("found treenode with matching key: '"+ ((Node) rtnItem.getData()).longKey+ "' with original value: '"+((Node) rtnItem.getData()).value+"'. Replacing with new value: '"+node.value+"");
                    // replace only if no value is provided for existing node
                    if(((Node) rtnItem.getData()).getValue() == null
                            || ((Node) rtnItem.getData()).getValue().equals(""))
                    {
                        ((Node) rtnItem.getData()).setValue(node.getValue());
                    }
                }
            }
        }
        // does not exist, new treenode has to be added
        if(rtnItem==null){
            rtnItem = (TreeNode) new DefaultTreeNode(node, tNode);
            ( (Node) rtnItem.getData()).setTreeNode(rtnItem);
        }
        return rtnItem;
    }

    public List<AttributeTO> getAttrList(){
        TreeNode item1;
        TreeNode item2;
        TreeNode item3;
        TreeNode item4;
        Iterator<TreeNode> iterator1;
        Iterator<TreeNode> iterator2;
        Iterator<TreeNode> iterator3;
        Iterator<TreeNode> iterator4;
        Node node;
        List<AttributeTO> aList = new ArrayList<AttributeTO>();

        iterator1 = root.getChildren().iterator();
        if(iterator1!=null){
            while(iterator1.hasNext())
            {
                item1=iterator1.next();
                iterator2 = item1.getChildren().iterator();
        if(iterator2!=null){
            while(iterator2.hasNext())
            {
                item2=iterator2.next();
                iterator3 = item2.getChildren().iterator();
                if(iterator3!=null){
                    while(iterator3.hasNext())
                    {
                        item3=iterator3.next();
                        iterator4 = item3.getChildren().iterator();
                        if(iterator4!=null){
                            while(iterator4.hasNext())
                            {
                                item4=iterator4.next();
                                node = ((Node) item4.getData());
                                if(item4.isLeaf() && node!=null)
                                {
                                    aList.add(new AttributeTO(node.getLongKey(),node.getValue()));
                                }
                            }
                        }
                        node = ((Node) item3.getData());
                        if(item3.isLeaf() && node!=null)
                        {
                            aList.add(new AttributeTO(node.getLongKey(),node.getValue()));
                        }
                    }
                }
                node = ((Node) item2.getData());
                if(item2.isLeaf() && node!=null)
                {
                    aList.add(new AttributeTO(node.getLongKey(),node.getValue()));
                }
            }
        }
                node = ((Node) item1.getData());
                if(item1.isLeaf() && node!=null)
                {
                    aList.add(new AttributeTO(node.getLongKey(),node.getValue()));
                }
            }
        }
        return aList;
    }

    public void updateValue(){
    }



    public void removeItem()
    {
        if(selectedNode!=null){
            TreeNode selectedTreeNode = selectedNode.getTreeNode();
            //System.out.println("remove invoked");
            selectedTreeNode.getParent().getChildren().remove(selectedNode.getTreeNode());
        }
        //System.out.println("remove executed: "+inports.getChildren().toString());
    }

    public void expandColapse(){
        expanded = !expanded;
        traverseChildren(root);
    }

    public void traverseChildren(TreeNode r){
        if(r.getChildCount() != 0){
            for(TreeNode n : r.getChildren()){
                n.setExpanded(expanded);
                if (n.getChildCount() > 0){
                    traverseChildren(n);
                }
            }
        }
    }

    public class Node{
        String key;
        String name;
        String value;
        String longKey;
        String longName;
        TreeNode treeNode;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean hasTreeNode() {
            return !key.equals("");
        }

        public TreeNode getTreeNode() {
            return treeNode;
        }

        public void setTreeNode(TreeNode treeNode) {
            if(treeNode!=null &&
                    treeNode.getParent()!=null &&
                    treeNode.getParent().getData()!=null &&
                    treeNode.getParent().getData().getClass()==Node.class){
                this.longKey = ((Node) treeNode.getParent().getData()).getLongKey()+"."+this.key;
                this.longName = ((Node) treeNode.getParent().getData()).getLongName()+"."+this.name;
            }
            this.treeNode = treeNode;
        }

        Node(String key, String name, String value){
            this.key=key;
            this.longKey=key;
            this.longName=name;
            this.name=name;
            this.value=value;
        }

        Node(String key, String value){
            this.key=key;
            this.longKey=key;
            this.longName=key;
            this.name=key;
            this.value=value;
        }

        Node(String key){
            this.key=key;
            this.longKey=key;
            this.longName=key;
            this.name=key;
            this.value="";
        }


        public String getKey() {
            return key;
        }

        public String getLongKey() {
            return longKey;
        }

        public String getLongName() {
            return longName;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            if(treeNode!=null
                    && !treeNode.isLeaf())
            {
                return "";
            }
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean canHaveFileValue() {
            return (key.startsWith("port")
                    || key.startsWith("dep")
                    || key.equals("definition")
                    || key.equals("graph")
                    );
        }

        public boolean canHaveWfImpValue() {
            return (key.startsWith("dep"));
        }

    }

    //put a message into growl
    void addMessage(String clientID, Severity severity, String summary, String detail) {
        FacesMessage message = new FacesMessage(severity, summary,  detail);
        FacesContext.getCurrentInstance().addMessage(clientID, message);
    }

}