package fi.capeismi.fish.uistelupaivakirja.web.entity;

import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Test;

import fi.capeismi.fish.uistelupaivakirja.web.dao.Event;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;


public class JPATest {
    @Test
    public void testJPAread() {
    	EntityManager entityManager = Persistence.createEntityManagerFactory("uisteluweb").createEntityManager();
    	Query query = entityManager.createQuery("from Trollingobject");
    	for(Object result: query.getResultList()) {
    		Trollingobject collection = (Trollingobject)result;
    		System.out.println("collection: "+collection.getDate());
    		for(Event e: collection.getEvents()) {
    			System.out.println("event: "+e.getFish_coord_lat());
    		}

    	}
    	assertTrue(query.getResultList().size() > 0);
    }
}
