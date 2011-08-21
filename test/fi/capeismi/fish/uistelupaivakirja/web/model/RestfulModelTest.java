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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Event;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Collection;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Eventproperty;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingproperty;

import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class RestfulModelTest {
    
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
        String[] users = {"cape", "testuser", "keijjo"};
                
        for(String user: users) {
            RestfulModel model = new RestfulModel(user);
            Collection collection = new Collection();
            collection.setType(model.getType("trip"));
            collection.setRevision(0);
            collection.setUser(model.getUser());
            model.setTrollingObjects(collection);
        }
    }
    
    @Test
    public void testSetBasicData() {
        String[] users = {"cape", "testuser", "keijjo"};
        for(String user: users) {
            RestfulModel model = new RestfulModel(user);
            Collection collection = new Collection();
            collection.setType(model.getType("trip"));
            collection.setRevision(1);
            collection.setUser(model.getUser());
            setTrollingObject(collection, 1, generateTestProps(user+1), generateTestEvents(user+1));
            setTrollingObject(collection, 2, generateTestProps(user+2), generateTestEvents(user+2));
            setTrollingObject(collection, 3, generateTestProps(user+3), generateTestEvents(user+3));
            model.setTrollingObjects(collection);
        }
    }
    
    @Test
    public void testSetDataForNoSuchUser() {
        try {
            RestfulModel model = new RestfulModel("nouser");
            setTrollingObject(model.getTrollingObjects("trip"), 1, generateTestProps(""+1), generateTestEvents(""+1));
            fail("user should not be found");
        } catch(RestfulException e) {
            assertEquals("no user", e.toString());
        }
    }
    
        
    @Test
    public void testGetBasicData() {
        String[] users = {"cape", "testuser", "keijjo"};
        for(String user: users) {
            RestfulModel model = new RestfulModel(user);
            assertEquals(3, model.getTrollingObjects("trip").getTrollingobjects().size());        
            compareUserObjects(model, user, 1, generateTestProps(user+1), generateTestEvents(user+1));
            compareUserObjects(model, user, 2, generateTestProps(user+2), generateTestEvents(user+2));
            compareUserObjects(model, user, 3, generateTestProps(user+3), generateTestEvents(user+3));
        }
    }
    
    @Test
    public void testRemoveObject() {
        String[] users = {"cape", "testuser", "keijjo"};
        for(String user: users) {
            RestfulModel model = new RestfulModel(user);
            assertEquals(3, model.getTrollingObjects("trip").getTrollingobjects().size());            
            compareUserObjects(model, user, 1, generateTestProps(user+1), generateTestEvents(user+1));
            compareUserObjects(model, user, 2, generateTestProps(user+2), generateTestEvents(user+2));
            compareUserObjects(model, user, 3, generateTestProps(user+3), generateTestEvents(user+3));
        } 
    }
    
    private void compareUserObjects(RestfulModel model, 
            String user, 
            int id, 
            Map<String, String> compTo, 
            List<Map<String, String>> eventsCompTo) {
               
        for(Trollingobject tobject: model.getTrollingObjects("trip").getTrollingobjects()) {
            if(tobject.getObjectIdentifier() == id) {
                compareTrollingObject(tobject, compTo, eventsCompTo);
                return;
            }
        }
        fail("no such object");
    }
    
    private void compareTrollingObject(Trollingobject tobject, Map<String, String> compTo, List<Map<String, String>> eventsCompTo) {
        Map<String, String> kvs = new HashMap<String, String>();
        for(Trollingproperty prop: tobject.getTrollingproperties()) {
            kvs.put(prop.getKeyname(), prop.getValue());
        }
        assertEquals(compTo, kvs);        
        assertEquals(tobject.getEvents().size(), eventsCompTo.size());
        
        Iterator<Map<String, String>> iterEvents = eventsCompTo.iterator();
        for(Event event: tobject.getEvents()) {
            Map<String, String> eventprops = iterEvents.next();
            compareEvent(event, eventprops);
        }
    }
    
    private void compareEvent(Event event, Map<String, String> compTo) {
        Map<String, String> kvs = new HashMap<String, String>();
        for(Eventproperty prop: event.getEventproperties()) {
            kvs.put(prop.getKeyname(), prop.getValue());
        }
        System.out.print("Compare "+compTo.keySet().toString()+ " <=> "+kvs.keySet().toString());
        System.out.println(" with "+compTo.values().toString()+ " <=> "+kvs.values().toString());
        assertEquals(compTo, kvs);
    }
        
    private Map<String, String> generateTestProps(String user) {
        Map<String, String> props = new HashMap<String, String>();
        for(int loop=0; loop < 5; loop++) {
            props.put("property"+loop, user+"value"+loop);
        }
        
        return props;
    }
    
    private List<Map<String, String>> generateTestEvents(String user) {
        List<Map<String, String>> events = new ArrayList<Map<String, String>>();
        for(int i=0; i < 10; i++) {
            Map<String, String> props = new HashMap<String, String>();
            for(int j=0; j < 3; j++) {
                props.put("property"+j, user+"value"+i+j);
            }
            events.add(props);
        }
        return events;
    }
    
    private void setTrollingObject(Collection collection, int identifier, Map<String, String> properties, List<Map<String, String> > events) {
        Trollingobject trollingobject = new Trollingobject(collection, identifier);
        
        for(Map.Entry<String, String> entry: properties.entrySet()) {
            trollingobject.getTrollingproperties().add(new Trollingproperty(trollingobject, entry.getKey(), entry.getValue()));
        }
        
        for(Map<String, String> eventproperties: events) {
            Event event = new Event(trollingobject);
            trollingobject.getEvents().add(event);
            for(Map.Entry<String, String> property: eventproperties.entrySet()) {            
                event.getEventproperties().add(new Eventproperty(event, property.getKey(), property.getValue()));                
            }
        }
        
        collection.getTrollingobjects().add(trollingobject);
    }    
}
