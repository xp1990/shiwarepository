/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.*;
import org.apache.commons.io.IOUtils;


/**
 *
 * @author sasvara
 */
public class DomainHandler {    
    private String json = "";
    private JSONObject jsonObject;
    private Set<String> domains = new LinkedHashSet<String>();
    private File configFile;
     
    private static volatile DomainHandler INSTANCE;
           
     
    private DomainHandler(File f) {
        configFile = f;
        init();
    }      
    
    public static DomainHandler create(File f) {
        if (INSTANCE == null) {
            INSTANCE = new DomainHandler(f);
        }
        return INSTANCE;
    }
    
    public static DomainHandler recreate(File f) {               
        INSTANCE = new DomainHandler(f);
        return INSTANCE;
    }
    
    public static DomainHandler getInstance() {        
        return INSTANCE;
    }
    
            
    public Set<String> extractDomains() {        
         for ( Object o : getJSONDomains()) {
            domains.add((String)((JSONObject)o).get("domain"));
         }
         
        return domains;
    }
    
    public Map<String, Set<String>> extractSubdomains() {
         Map<String, Set<String>> subdomainMap = 
                 new HashMap<String, Set<String> >();
         
         for ( Object o : getJSONDomains()) {
             JSONObject elem = ((JSONObject) o);
            String d  = (String)elem.get("domain");
            Set<String> subdomains = new LinkedHashSet<String>();
            
            if (elem.containsKey("subdomain")) {
                for (Object subdom : (JSONArray)elem.get("subdomain")) {
                    subdomains.add((String)subdom);
                }
                subdomainMap.put(d, subdomains);
            }
            else {
                subdomainMap.put(d, new LinkedHashSet<String>());
            }
         }
         
        return subdomainMap;
    }
    
     public Set<String> extractSubdomains(String domain) {
        Map<String, Set<String>> subdomainMap = extractSubdomains();         
        Set<String> subdomains =  subdomainMap.get(domain);
        return (subdomains == null) ? new LinkedHashSet<String>(): subdomains;
    }
    
    
    public void extractDomainsAndSubdomains(List<String> domains, 
             Map<String, Set<String>> subdomainMap) {         
         domains.addAll(extractDomains());                  
         subdomainMap.putAll(extractSubdomains());
         
    }
    
    public void addDomain(String domain) {        
        if (domains.contains(domain)) {
            Logger.getAnonymousLogger().info("domain already added");
            return;
        }
        
        JSONObject newDomain = (JSONObject)JSONSerializer.toJSON(
                String.format("{\'domain\':\'%s\'}", domain));
        
        (getJSONDomains()).add(newDomain);
    }
    
    public void addSubdomain(String domain, String newSubdomain) {
        for (Object o : getJSONDomains()) {
            String d = (String) ((JSONObject) o).get("domain");
            if (d.equals(domain)) {                
                JSONObject elem = ((JSONObject) o);
                
                if (elem.containsKey("subdomain")) {
                    JSONArray subdomains = ((JSONArray) elem.get("subdomain"));
                    if (!subdomains.contains(newSubdomain)) {
                        ((JSONArray) elem.get("subdomain")).add(newSubdomain);                    
                    }
                }
                else {
                    
                    JSONArray arr = new JSONArray();
                    arr.add(newSubdomain);
                    ((JSONObject) o).put("subdomain", arr);
                }
                
                break;
            }
        }
    }
    
    public void removeSubdomain(String domain, String subdomain) {
        for (Object o : getJSONDomains()) {
            JSONObject domElem = ((JSONObject) o);
            String d = (String) domElem.get("domain");
            if (d.equals(domain)) {                                
                if (domElem.containsKey("subdomain")) {
                    
                    Iterator it = ((JSONArray)domElem.get("subdomain")).iterator();
                    while (it.hasNext()) {
                        String sd = (String)it.next();
                        if (subdomain.equals(sd)) {
                            it.remove();                            
                        }
                    }             
                }                
                
                break;
            }
        }
    }
    
    public void removeDomain(String domain) {                
        
        Iterator it = (getJSONDomains()).iterator();
        while (it.hasNext()) {
            JSONObject elem = (JSONObject)it.next();
            if (domain.equals(elem.get("domain"))) {
                it.remove();
                domains.remove(domain);
            }
        }                        
    }
    
    
    public void persist() {
 
        PrintWriter os = null;
        try {
            
            os = new PrintWriter(new FileOutputStream(configFile));
            os.write(jsonObject.toString(4));
        } catch (IOException ex) {
            Logger.getLogger(DomainHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (os != null) {                
                    os.close();                
            }
        }
    }
    
    public synchronized void reReadCfg() { 
        init();
        domains = new LinkedHashSet<String>();
    }

    private void init() {        
        InputStream is = null;
        byte b[] = null;
        
        try {
            is = new FileInputStream(configFile);
            b = IOUtils.toByteArray(is);
        } catch (IOException ex) {
            Logger.getLogger(DomainHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(DomainHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        try {
            json = new String(b, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DomainHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        jsonObject = (JSONObject) JSONSerializer.toJSON(json);
    }

    private JSONArray getJSONDomains() {
        return (JSONArray) jsonObject.get("domains");
    }
    
    public String jsonToString(){
        return jsonObject.toString(4);
    }
}
