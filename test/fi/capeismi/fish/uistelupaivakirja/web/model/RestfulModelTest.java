/*
 * Copyright (C) 2011 Samuli Penttilä <samuli.penttila@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fi.capeismi.fish.uistelupaivakirja.web.model;

import java.io.FileInputStream;
import java.io.File;
import fi.capeismi.fish.uistelupaivakirja.web.controller.RestfulController;
import fi.capeismi.fish.uistelupaivakirja.web.controller.XMLReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Event;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Collection;
import fi.capeismi.fish.uistelupaivakirja.web.dao.DAOStoreTest;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;

import fi.capeismi.fish.uistelupaivakirja.web.dao.User;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class RestfulModelTest {
    
    private static final String XMLFILELOCATION = "/Users/cape/uistelu/database/";
    
    public RestfulModelTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
       
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

 
    @Test
    public void testCheckCollectionsAreEmpty() {
        RestfulModel model = new RestfulModel("cape");
        
        assertEquals(model.getTrollingObjects("trip").getTrollingobjects().size(), 0);
        assertEquals(model.getTrollingObjects("lure").getTrollingobjects().size(), 0);
        assertEquals(model.getTrollingObjects("place").getTrollingobjects().size(), 0);
    }
    
    @Test
    public void testNonexistentCollections() {
        RestfulModel model = new RestfulModel("cape");
        
        try {
            model.getTrollingObjects("idontexist").getTrollingobjects();
            fail("exception was expected");
        } catch(RestfulException e) {
            assertEquals("no such collection", e.toString());
        }
    }
    
    @Test
    public void testSetCollections() {
        for(String user: DAOStoreTest.getUsers()) {
            RestfulModel model = new RestfulModel(user);
            Collection collection = new Collection();
            collection.setType(model.getType("trip"));
            collection.setRevision(0);
            model.setTrollingObjects(collection);
        }
    }
    
    @Test
    public void testBasicData() throws Exception {
        for(String user: DAOStoreTest.getUsers()) { 
            RestfulModel model = new RestfulModel(user);
            Collection collection = generateTestData("trip", 1, 5, "kekkuli"+user);   
            model.setTrollingObjects(collection);
            assertEquals(5, model.getTrollingObjects("trip").getTrollingobjects().size());

            Collection collectionLocal = generateTestData("trip", 1, 5, "kekkuli"+user);
            assertTrue(compareCollections(model.getTrollingObjects("trip"), collectionLocal));
        }  
    }
    
    @Test
    public void testSetDataForNoSuchUser() {
        try {
            RestfulModel model = new RestfulModel("nouser");
            model.setTrollingObjects(new Collection());
            
            fail("user should not be found");
        } catch(RestfulException e) {
            assertEquals("no user", e.toString());
        }
    }
    
    
    @Test
    public void testRemoveObject() throws Exception {
        for(String user: DAOStoreTest.getUsers()) {
            RestfulModel model = new RestfulModel(user);            
            Collection collection = generateTestData("trip", 2, 1, user);
            model.setTrollingObjects(collection);
            assertEquals(1, model.getTrollingObjects("trip").getTrollingobjects().size());
        } 
    }    
    
    @Test
    public void testFillWithRealData() throws ParserConfigurationException, SAXException, IOException {
        
        for(String user: DAOStoreTest.getUsers()) {
            RestfulModel model = new RestfulModel(user);
            for(String type : new String[] {"trip", "lure", "place"}) {
                File file = new File(String.format("%s%s.xml", XMLFILELOCATION, type));
                FileInputStream stream = new FileInputStream(file);
                XMLReader reader = new XMLReader(stream);
                Collection collection = reader.getTrollingObjects();
                collection.setRevision(model.getTrollingObjects(type).getRevision());
                collection.setType(model.getType(type));
                int before = model.getTrollingObjects(type).getTrollingobjects().size();
                model.setTrollingObjects(collection);
                int after = model.getTrollingObjects(type).getTrollingobjects().size();
                assertTrue(after > before && after > 10);                
            }
        } 
    }
        
    @Test
    public void testModifyUser() {
        RestfulModel model = new RestfulModel("cape");
        User user = new User();
        assertNull(user.getPublishfish());        
        
        String originalPassword = user.getPassword();
        user.setPublishfish(Boolean.TRUE);
        user.setId(6666);
        model.setUser(user);
        
        User userafter = model.getUser();
        assertTrue(userafter.getPublishfish());
        assertFalse(userafter.getPassword().equalsIgnoreCase(originalPassword));
        assertFalse(userafter.getId().intValue() == 6666);
        assertNotNull(userafter.getPassword());
        assertNotNull(userafter.getSalt());
    }
    
    @Test
    public void testChangePassword() {
        RestfulModel model = new RestfulModel("cape");
        User user = new User();
        String originalPassword = user.getPassword();
        user.setPlaintextpassword("new");

        model.setUser(user);
        
        User userafter = model.getUser();
        assertFalse(userafter.getPassword().equalsIgnoreCase(originalPassword));
        assertNotNull(userafter.getPassword());
        assertNotNull(userafter.getSalt());
    }
    
    @Test
    public void testChangeUsername() {
        RestfulModel model = new RestfulModel("cape");
        User user = model.getUser();
        user.setUsername("thisisnotallowed");
        model.setUser(user);
        assertFalse(model.getUser().getUsername().equalsIgnoreCase("thisisnotallowed"));
    }
    
    private Collection generateTestData(String type, int revision, int count, String contentseed) throws Exception {
        String content = String.format("<TrollingObjects revision=\"%d\">", revision);
        for(int loop=0; loop < count; loop++) {
            content += String.format("<TrollingObject type=\"%s\" id=\"%d\">"
                        + "<date>%s</date>"
                        + "<description>%s%d</description>"
                    + "</TrollingObject>", type, loop, contentseed, contentseed, loop+666);
        }
        
        content += "</TrollingObjects>";
        
        XMLReader reader = new XMLReader(RestfulController.stringToInputStream(content));
        Collection collection = reader.getTrollingObjects();
        collection.setType(new RestfulModel("").getType(type));
        return collection;
    }
    
    private boolean compareCollections(Collection c1, Collection c2) {
        assertEquals(c1.getTrollingobjects().size(), c2.getTrollingobjects().size());
        assertEquals(c1.getType().getName(), c2.getType().getName());
        
        Iterator<Trollingobject> iter = c2.getTrollingobjects().iterator();
        
        for(Trollingobject t1: c1.getTrollingobjects()) {
            Trollingobject t2 = iter.next();
            assertTrue(compareTrollingObjects(t1, t2));            
        }
        
        return true;
    }
    
    private boolean compareTrollingObjects(Trollingobject t1, Trollingobject t2) {

        assertEquals(t1.getProperties(), t2.getProperties());        
        assertEquals(t1.getEvents().size(), t2.getEvents().size());
        
        Iterator<Event> iter = t2.getEvents().iterator();
        for(Event e1: t1.getEvents()) {
            Event e2 = iter.next();
            assertTrue(compareEvents(e1, e2));
        }
        
        return true;
    }
    
    private boolean compareEvents(Event e1, Event e2) {        
        assertEquals(e1.getProperties(), e2.getProperties());
        return true;
    }     
}
