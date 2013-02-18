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
package fi.capeismi.fish.uistelupaivakirja.web.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import fi.capeismi.fish.uistelupaivakirja.web.model.SearchObject;
import fi.capeismi.fish.uistelupaivakirja.web.controller.LoginService;

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
public class DAOStoreTest {   
    public DAOStoreTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    	EntityManager em = getSession();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for(Object item: em.createQuery("from User").getResultList()) {
        	em.remove(item);
        }
        tx.commit();
        
        assertEquals(getNumberOfRowsInTable("User"), 0);
        assertEquals(getNumberOfRowsInTable("Collection"), 0);
        assertEquals(getNumberOfRowsInTable("Trollingobject"), 0);
        assertEquals(getNumberOfRowsInTable("Event"), 0);
        
    }        
    
    private static EntityManager getSession() {
    	EntityManager entityManager = Persistence.createEntityManagerFactory("uisteluweb").createEntityManager();
    	return entityManager;
    }
    
    
    private static int getNumberOfRowsInTable(String table) {
        EntityTransaction transaction = getSession().getTransaction();
        transaction.begin();
        int retval = getSession().createQuery("from "+table).getResultList().size();
        transaction.commit();
        return retval;
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
    public void testCreateUser() {        
        for(String user: getUsers()) {
            createUser(user);
        }
    }
    
    @Test
    public void testOpenTransactions() {
        final DAOStore store = new DAOStore("cape");
        new Runner() {
            public void runInTx() {
                store.getUser();       
            }            
        };
        
        new Runner() {
            public void runInTx() {
                store.getType("trip");       
            }            
        };
        
        new Runner() {
            public void runInTx() {
                store.getCollection("trip");       
            }            
        };
        
        new Runner() {
            public void runInTx() {
                SearchObject search = store.searchObject("spinneritem");
                search.doSearch();
            }            
        };        
                
    }
        
    
    public static String[] getUsers() {
        return new String[] {"cape", "testuser", "keijjo"};
    }
    
    private void createUser(String username) {
        int usersBefore = getNumberOfRowsInTable("User");
        DAOStore store = new DAOStore(null);
        User user = new User();
        user.setUsername(username);
        user.setSalt(LoginService.generateSalt());
        user.setPassword(LoginService.getMD5Hash("pw"+username, user.getSalt()));
        store.addUser(user);
        
        assertEquals(usersBefore+1, getNumberOfRowsInTable("User"));
        assertEquals(new DAOStore(username).getUser().getUsername(), username);
    }
    
    private abstract class Runner {
        private EntityManager session;
        public Runner() {
            before();
            runInTx();
            after();
        }
        
        public abstract void runInTx();
        
        private void before() {
            this.session = getSession();
        }
        
        private void after() {
        	assertFalse(this.session.getTransaction().isActive());            

        }
    }
}
