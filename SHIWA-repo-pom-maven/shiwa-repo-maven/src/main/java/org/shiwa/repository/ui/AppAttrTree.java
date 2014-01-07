/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shiwa.repository.ui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.faces.application.FacesMessage;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import uk.ac.wmin.edgi.repository.transferobjects.AttributeTO;

/**
 *
 * @author kukla
 */
public class AppAttrTree extends AttrTree {

    TreeNode inports;
    TreeNode outports;
    TreeNode tasktype;
    TreeNode application;
    TreeNode domain;
    TreeNode subdomain;
    TreeNode keywords;
    TreeNode configurations;

    String[] portPool;
    String[] configPool;
    String[] typePool;
    int[] depthRange;
    String[] testPool;
    
    Node newDatatypeNode= new Node("","");
    
    String textEditValue = "";
    String fileSelValue = "";
    
    private String selectedDomain = "";
    private String selectedSubDomain = "";
    
    
    DataTypeManager dataTypeManager = new DataTypeManager();

    public DataTypeManager getDataTypeManager() {
        return dataTypeManager;
    }

    public void setDataTypeManager(DataTypeManager dataTypeManager) {
        this.dataTypeManager = dataTypeManager;
    }        
    
    static List<String> domains = new CopyOnWriteArrayList<String>();
    
    static Map<String, Set<String>> subDomains = new ConcurrentHashMap<String, Set<String>>();
    
    static DomainHandler domainHandler = DomainHandler.getInstance();
    
    static void refreshDomains() {
        domains = new CopyOnWriteArrayList<String>();
        subDomains = new ConcurrentHashMap<String, Set<String>>();
        domainHandler.extractDomainsAndSubdomains(domains, subDomains);
    }
    
    static {        
        
        domainHandler.extractDomainsAndSubdomains(domains, subDomains);
    }       
    
    public boolean isDomain(Node node) {
        return node.getName().matches("domain");
    } 
    
    public boolean isDomainOrSubdomain(Node node) {
        return node.getName().matches(".*domain.*");
    } 
    
    public List<String> getDomains() {
        return domains;
    }
    
    public List<String> getSubDomains(String domain) {
        Set<String> sub_domains = subDomains.get(domain);
        if (sub_domains == null || sub_domains.isEmpty()) {
            return Arrays.asList(new String[]{"-"});
        }
        return new ArrayList<String>(sub_domains);
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
    
    public void updateValueWithFileSel(){
        selectedNode.setValue(fileSelValue);
    }    
    

    public Node getNewDatatypeNode() {
        return newDatatypeNode;
    }

    public void setNewDatatypeNode(Node newDatatypeNode) {
        this.newDatatypeNode = newDatatypeNode;
    }

    public String[] getTypePool() {
        return typePool;
    }

    public void setTypePool(String[] typePool) {
        this.typePool = typePool;
    }

    public int[]  getDepthRange() {
        return depthRange;
    }

    public void setDepthRange(int[] depthRange) {
        this.depthRange = depthRange;
    }

    public String[] getPortPool(){
        return portPool;
    }   
    
    public void setPortPool(String[] portPool) {
        this.portPool = portPool;
    }

    Port newPort = new Port();
    Config newConfig = new Config();


    public String[] getConfigPool() {
        return configPool;
    }

    public void setConfigPool(String[] configPool) {
        this.configPool = configPool;
    }

    public Config getNewConfig() {
        return newConfig;
    }

    public void setNewConfig(Config newConfig) {
        this.newConfig = newConfig;
    }
    
    public Port getNewPort() {
        return newPort;
    }

    public void setNewPort(Port newPort) {
        this.newPort = newPort;
    }
    
    public String getFriendlyName(String key){
        if(key!=null
                && key.startsWith("conf"))
        {
            return key.replace("conf", "dataset");
        }
        return key;        
    }
    
    @Override
    public TreeNode addChildren(TreeNode tNode, Node node){
        if(node != null
                && node.getKey() != null
                && node.getKey().startsWith("conf")
                && !node.getKey().equals("configurations")) 
        {
            // adding friendly name for confXXXX ids
            node.setName(getFriendlyName(node.key));
            TreeNode confNode = super.addChildren(tNode, node);
            // adding description to each confXXXX
            super.addChildren(confNode, new Node("description"));
            return confNode;
        }
        return super.addChildren(tNode, node);
    }    
    
    AppAttrTree() {
        super();
        inports = (TreeNode) new DefaultTreeNode(new Node("inports","inputs",""), root);
        ( (Node) inports.getData()).setTreeNode(inports);
        outports = (TreeNode) new DefaultTreeNode(new Node("outports", "outputs", ""), root);
        ( (Node) outports.getData()).setTreeNode(outports);
        configurations = (TreeNode) new DefaultTreeNode(new Node("configurations","datasets", ""), root);
        ( (Node) configurations.getData()).setTreeNode(configurations);
        tasktype = (TreeNode) new DefaultTreeNode(new Node("tasktype"), root);
        ( (Node) tasktype.getData()).setTreeNode(tasktype);
        application = (TreeNode) new DefaultTreeNode(new Node("application"), root);
        ( (Node) application.getData()).setTreeNode(application);
        domain = (TreeNode) new DefaultTreeNode(new Node("domain"), root);
        ( (Node) domain.getData()).setTreeNode(domain);
        subdomain = (TreeNode) new DefaultTreeNode(new Node("subdomain"), root);
        ( (Node) subdomain.getData()).setTreeNode(subdomain);
        keywords = (TreeNode) new DefaultTreeNode(new Node("keywords"), root);
        ( (Node) keywords.getData()).setTreeNode(keywords);


        portPool = generatePool("port",100);
        configPool = generatePool("conf",100);

        typePool = new String[5];
        typePool[0] = "string";
        typePool[1] = "int";
        typePool[2] = "double";
        typePool[3] = "boolean";
        typePool[4] = "file";
        
        depthRange = new int[10];
        String num;
        for(int i=0; i<10; i++){
            depthRange[i]=i;
        }
                
        /*typePool = new String[22];
        typePool[0] = "anyURI";
        typePool[1] = "base64Binary";
        typePool[2] = "boolean";
        typePool[3] = "byte";
        typePool[4] = "date";
        typePool[5] = "dateTime";
        typePool[6] = "decimal";
        typePool[7] = "double";
        typePool[8] = "duration";
        typePool[9] = "hexBinary";
        typePool[10] = "file";
        typePool[11] = "float";
        typePool[12] = "integer";
        typePool[13] = "string";
        typePool[14] = "time";
        typePool[15] = "long";
        typePool[16] = "nonNegativeInteger";
        typePool[17] = "positiveInteger";
        typePool[18] = "unsignedByte";
        typePool[19] = "unsignedInt";
        typePool[20] = "unsignedLong";
        typePool[21] = "unsignedShort";*/
        
    }

    public AppAttrTree(List<AttributeTO> aList)
    {
        this();
        super.setAttrTree(aList);
        for (AttributeTO a : aList) {
            if (a.getName().equals("domain")) {
                selectedDomain = a.getValue();                
            }
            if (a.getName().equals("subdomain")) {
                selectedSubDomain = a.getValue();
            }
        }
        
        if (!domains.contains(selectedDomain)) {
            selectedDomain = "Other";
        }
            
    }

    public void addPort()
    {
        //System.out.println("AddPort methind invoked.");
        if(selectedNode!=null){
            String portId = newPort.getPortId();
            if(canAddPortId(portId))
            {
                TreeNode selectedTreeNode = selectedNode.getTreeNode();

                TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node(portId), selectedTreeNode);
                ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
                TreeNode newTitle = (TreeNode) new DefaultTreeNode(new Node("title",newPort.getTitle()), newTreeNode);
                ( (Node) newTitle.getData()).setTreeNode(newTitle);
                TreeNode newDescription = (TreeNode) new DefaultTreeNode(new Node("description",newPort.getDescription()), newTreeNode);
                ( (Node) newDescription.getData()).setTreeNode(newDescription);
                TreeNode newDatatype = (TreeNode) new DefaultTreeNode(new Node("datatype",newPort.getPrettyDatatype()), newTreeNode);
                ( (Node) newDatatype.getData()).setTreeNode(newDatatype);
                selectedNode.setValue("");
            }else{
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot create new port! PortID '"+portId+"' already exists.", null);
            }
        }
    }

    public void addConfig()
    {
        if(selectedNode!=null){
            String configId = newConfig.getConfigId();
            String description = newConfig.getDescription();
            if(canAddConfId(configId))
            {
                TreeNode selectedTreeNode = selectedNode.getTreeNode();

                TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node(configId,getFriendlyName(configId),""), selectedTreeNode);
                ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
                TreeNode descNode = (TreeNode) new DefaultTreeNode(new Node("description",description), newTreeNode);
                ( (Node) descNode.getData()).setTreeNode(descNode);
                /*TreeNode newPortId = (TreeNode) new DefaultTreeNode(new Node("portref",newConfig.getPortId()), newTreeNode);
                ( (Node) newPortId.getData()).setTreeNode(newPortId);
                TreeNode newValue = (TreeNode) new DefaultTreeNode(new Node("value",newConfig.getValue()), newTreeNode);
                ( (Node) newValue.getData()).setTreeNode(newValue);
                selectedNode.setValue("");*/
            }else{
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot create new dataset. Dataset id'"+getFriendlyName(configId)+"' already exists.", null);
            }
        }
    }

    public void addPortToConfig()
    {
        if(selectedNode!=null && selectedNode.getTreeNode()!=null){
            String portId = newConfig.getPortId();
            if(canAddPortIdToConf(portId,selectedNode))
            {
                TreeNode selectedTreeNode = selectedNode.getTreeNode();
                TreeNode newTreeNode = (TreeNode) new DefaultTreeNode(new Node(newConfig.getPortId(), newConfig.getValue()), selectedTreeNode);
                ( (Node) newTreeNode.getData()).setTreeNode(newTreeNode);
                selectedNode.setValue("");
            }else{
                addMessage(null, FacesMessage.SEVERITY_ERROR, "Cannot add port to configuration! Port '"+portId+"' is already added.", null);
            }
        }
    }
   
    public Boolean canAddPort(Node node) {
        if(node.getKey().equals("inports") || node.getKey().equals("outports")) {
            return true;
        }
        return false;
    }    

    public Boolean canAddConfig(Node node) {
        if(node.getKey().equals("configurations")) {
            return true;
        }
        return false;
    }

    public Boolean canAddPortToConfig(Node node) {
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

    
    public Boolean canEdit(Node node) {
        if(node.getTreeNode()!=null &&
                node.getTreeNode().isLeaf() &&
                !canEditDatatype(node) &&
                !canAddPort(node) &&
                !canAddConfig(node) &&
                !canAddPortToConfig(node) &&
                !canEditConf(node))
        {
            return true;
        }
        return false;
    }
    
    public Boolean canEditDatatype(Node node) {
        if(node.getKey().equals("datatype"))
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
    

    public Boolean canRemove(Node node) {
        if(node!=null && node.getTreeNode()!=null
                && node.getTreeNode().getParent()!=null
                && node.getTreeNode().getParent().getData()!=null
                && node.getTreeNode().getParent().getData().getClass()==Node.class)
        {
            if(( (Node) node.getTreeNode().getParent().getData()).getKey().equals("inports") 
                    || ( (Node) node.getTreeNode().getParent().getData()).getKey().equals("outports") 
                    || ( (Node) node.getTreeNode().getParent().getData()).getKey().equals("configurations") )
            {
                return true;
            }
            if(node.getTreeNode().getParent().getParent()!=null
                    && node.getTreeNode().getParent().getParent().getData()!=null
                    && node.getTreeNode().getParent().getParent().getData().getClass()==Node.class)
            {
                if(( (Node) node.getTreeNode().getParent().getParent().getData()).getKey().equals("configurations")
                        && !node.getKey().equals("description"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean canAddPortId(String portId){
        TreeNode item;
        if(inports.getChildren()!=null)
        {
            Iterator<TreeNode> iterator = inports.getChildren().iterator();
            while(iterator.hasNext()){
                item = iterator.next();
                if(item!=null && item.getData()!=null && ((Node) item.getData()).getKey().equals(portId)){
                    return false;
                }
            }
        }
        if(outports.getChildren()!=null)
        {
            Iterator<TreeNode> iterator = outports.getChildren().iterator();
            while(iterator.hasNext()){
                item = iterator.next();
                if(item!=null && item.getData()!=null && ((Node) item.getData()).getKey().equals(portId)){
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean canAddPortIdToConf(String portId, Node node){
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
                    if(item!=null && item.getData()!=null && ((Node) item.getData()).getKey().equals(portId)){
                        return false;
                    }
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

    /**
     * @return the selectedDomain
     */
    public String getSelectedDomain() {
        return selectedDomain;
    }

    /**
     * @param selectedDomain the selectedDomain to set
     */
    public void setSelectedDomain(String selectedDomain) {
        this.selectedDomain = selectedDomain;
        ((Node)domain.getData()).setValue(selectedDomain);
    }

    /**
     * @return the selectedSubDomain
     */
    public String getSelectedSubDomain() {
        return selectedSubDomain;
    }

    /**
     * @param selectedSubDomain the selectedSubDomain to set
     */
    public void setSelectedSubDomain(String selectedSubDomain) {
        this.selectedSubDomain = (selectedSubDomain == null)
                ? "" : selectedSubDomain;
        ((Node)subdomain.getData()).setValue(selectedSubDomain);
    }
    
    public class Port{
        String title="";
        String description="";
        String datatype="";
        String portId="";
        int depth=0;

        public String getPortId() {
            return portId;
        }

        public void setPortId(String portId) {
            this.portId = portId;
        }
        
        public String getDatatype() {
            return datatype;
        }

        public void setDatatype(String datatype) {
            this.datatype = datatype;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }
        
        public String getPrettyDatatype() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                sb.append("<");
            }
            sb.append(datatype);
            for (int i = 0; i < depth; i++) {
                sb.append(">");
            }
            return sb.toString();
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
    }

    public class Config{
        String configId="";
        String portId="";
        String value="";
        String description="";

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getConfigId() {
            return configId;
        }

        public void setConfigId(String configId) {
            this.configId = configId;
        }

        public String getPortId() {
            return this.portId;
        }

        public void setPortId(String portId) {
            this.portId = portId;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            System.out.println("Port Value: "+value);
            this.value = value;
        }

    }   
    
    public DataTypeManager processDataType(Node node) {
        DataTypeManager dtm = new DataTypeManager();
        if(node.getKey().equals("datatype"))
        {
            String dataTypeBig = node.getValue();
            
            int count = 0;
            
            for (char c : dataTypeBig.toCharArray()) {
                if (c == '<') {
                    count++;
                } else {
                    break;
                }
            }
            
            dtm.setTitle(node.getLongName());
            dtm.setDepth(count);
            dtm.setDatatype(dataTypeBig.replace("<", "").replace(">", ""));
            dtm.setNode(node);
        }        
        
        return dtm;        
    }
    
    public class DataTypeManager {
        String title = "";
        String datatype="";
        int depth=0;
        Node node = null;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDatatype() {
            return datatype;
        }

        public void setDatatype(String datatype) {
            this.datatype = datatype;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }
    }
    
    public void updateDataTypeManager() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataTypeManager.getDepth(); i++) {
            sb.append("<");
        }
        sb.append(dataTypeManager.getDatatype());
        for (int i = 0; i < dataTypeManager.getDepth(); i++) {                    
            sb.append(">");
        }
        
        dataTypeManager.getNode().setValue(sb.toString());
    }
}