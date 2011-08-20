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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
        getSession().beginTransaction();
        for(Object item: getSession().createQuery("from User").list()) {
            getSession().delete(item);
        }
        getSession().getTransaction().commit();
        
        assertEquals(getNumberOfRowsInTable("User"), 0);
        assertEquals(getNumberOfRowsInTable("Collection"), 0);
        assertEquals(getNumberOfRowsInTable("Trollingobject"), 0);
        assertEquals(getNumberOfRowsInTable("Trollingproperty"), 0);
        assertEquals(getNumberOfRowsInTable("Event"), 0);
        assertEquals(getNumberOfRowsInTable("Eventproperty"), 0);        
        
    }        
    
    private static Session getSession() {
        SessionFactory fac = TrollingHibernateUtil.getSessionFactory();
        return fac.getCurrentSession();
    }
    
    private static int getNumberOfRowsInTable(String table) {
        getSession().beginTransaction();
        int retval = getSession().createQuery("from "+table).list().size();
        getSession().getTransaction().commit();
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
        DAOStore store = new DAOStore(null);
        User user = new User();
        user.setUsername("cape");
        user.setPassword(LoginService.getMD5Hash("kek"));
        store.setUser(user);
        
        assertEquals(getNumberOfRowsInTable("User"), 1);
        
    }
}
