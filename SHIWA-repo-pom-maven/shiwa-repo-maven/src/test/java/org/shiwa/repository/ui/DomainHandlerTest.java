/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.collections.map.ListOrderedMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sasvara
 */
public class DomainHandlerTest {
    
    public DomainHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDomains method, of class JSONParser.
     */
    @Test
    public void testGetDomains() {
        System.out.println("getDomains");        
          
        
        DomainHandler domainParser = null;
        
//        domainParser = DomainJSONParser.create(this.getClass().getResourceAsStream("/domains.json"));
        domainParser = DomainHandler.create(new File(this.getClass().getResource(("/domains.json")).getFile()));
        
        Set<String> domains = domainParser.extractDomains();
        
        String expDomains[] = 
            {"Astrophysics", "Computational Chemistry", "Heliophysics", "Life Sciences", "Multimedia", "Test", "Demo", "SCI-BUS"};
        assertArrayEquals(expDomains, domains.toArray());
        
        Map<String, Set<String>> subDomains = domainParser.extractSubdomains();
        
        assertArrayEquals(new String[]{"Neuroimaging", "Next Generation Sequencing", "Mass Spectrometry"}, 
                subDomains.get("Life Sciences").toArray());
        
        assertArrayEquals(new String[]{"Neuroimaging", "Next Generation Sequencing", "Mass Spectrometry"}, 
                domainParser.extractSubdomains("Life Sciences").toArray());
    }
    
    @Test
    public void testAddDomain() {                              
        DomainHandler domainParser =  DomainHandler.create(new File (this.getClass().getResource("/domains.json").getFile()));
        domainParser.reReadCfg();
                       
        domainParser.addDomain("ER-FLOW");
        Set<String> domains = domainParser.extractDomains();
        
        String expDomains[] = 
            {"Astrophysics", "Computational Chemistry", "Heliophysics", "Life Sciences", "Multimedia", "Test", "Demo", "SCI-BUS", "ER-FLOW"};
        assertArrayEquals(expDomains, domains.toArray());                
    
    }
    
    
    @Test
    public void testRemoveDomain() {                              
        DomainHandler domainParser =  DomainHandler.create(new File (this.getClass().getResource("/domains.json").getFile()));
        domainParser.reReadCfg();
//        Set<String> domains = domainParser.extractDomains();
//        System.out.println(domains);
        
        domainParser.removeDomain("SCI-BUS");
        
        String expDomains[] = 
            {"Astrophysics", "Computational Chemistry", "Heliophysics", "Life Sciences", "Multimedia", "Test", "Demo"};
        assertArrayEquals(expDomains, domainParser.extractDomains().toArray());                
    }
    
    @Test
    public void testAddSubdomain() {                              
        DomainHandler domainParser =  DomainHandler.create(new File (this.getClass().getResource("/domains.json").getFile()));
        domainParser.reReadCfg();
        Map<String, Set<String>> subDomains = domainParser.extractSubdomains();
        assert(subDomains.get("Test").toArray().length == 0);                
        
        domainParser.addSubdomain("Test", "test1");
        domainParser.addSubdomain("Test", "test2");
        
        subDomains = domainParser.extractSubdomains();
        assertArrayEquals(new String[]{"test1", "test2"}, 
                subDomains.get("Test").toArray());                
                       
    }
    
    @Test
    public void testRemoveSubdomain() {                              
        DomainHandler domainParser =  DomainHandler.create(new File (this.getClass().getResource("/domains.json").getFile()));
        domainParser.reReadCfg();
        
        Map<String, Set<String>> subDomains = domainParser.extractSubdomains();
        assert(subDomains.get("Test").toArray().length == 0);                
        
        domainParser.addSubdomain("Test", "test1");
        domainParser.addSubdomain("Test", "test2");                        
        
        domainParser.removeSubdomain("Test", "test1");
        subDomains = domainParser.extractSubdomains();
        
        assertArrayEquals(new String[]{"test2"}, 
                subDomains.get("Test").toArray());                   
    }
    
    @Test
    public void testPersist() throws IOException {
        DomainHandler domainParser =  DomainHandler.create(new File (this.getClass().getResource("/domains.json").getFile()));
        domainParser.reReadCfg();
        
        PrintWriter os = new PrintWriter(new FileOutputStream("/tmp/x")); 
        os.write(domainParser.jsonToString()); 
        os.close();
        
        domainParser =  DomainHandler.recreate(
                new File ("/tmp/x")
                );
    
        Set<String> domains = domainParser.extractDomains();
        
        String expDomains[] = 
            {"Astrophysics", "Computational Chemistry", "Heliophysics", "Life Sciences", "Multimedia", "Test", "Demo", "SCI-BUS"};
        assertArrayEquals(expDomains, domains.toArray());
        
    }
}
