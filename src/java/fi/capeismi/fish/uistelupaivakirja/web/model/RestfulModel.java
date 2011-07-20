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

import fi.capeismi.fish.uistelupaivakirja.web.dao.Keyvalue;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Propertylist;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Propertylistitem;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Revision;
import fi.capeismi.fish.uistelupaivakirja.web.dao.TrollingHibernateUtil;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;
import fi.capeismi.fish.uistelupaivakirja.web.dao.User;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class RestfulModel {
    private String m_user = null;
    
    public enum EType {unknown, trip, place, lure};    
    
    public RestfulModel(String user)
    {
        this.m_user = user;
    }
    
    private static Session getSession() {
        SessionFactory fac = TrollingHibernateUtil.getSessionFactory();
        return fac.getCurrentSession();
    }
    
    private static User getUser(Session ses, String username) {
        Query q = ses.createQuery("from User where username=:user");
        q.setString("user", username);
        List userlist = q.list();
        if(userlist.isEmpty()) {
            throw new RestfulException("No such user: "+username);            
        }
        else if(userlist.size() > 1) {
            throw new RestfulException("duplicate users with username: "+username);
        }
        
        return (User)userlist.get(0);
    }       
    
    private static Revision getLatestRevision(Session ses, EType type, User user) {
        SQLQuery q = ses.createSQLQuery("select revision_id from trollingobject "
                + "join revision on(revision_id=revision.id) where type=:type and user_id=:user "
                + "group by type, revision_id order by revision_id desc limit 1;");
        q.setString("type", type.toString());
        q.setParameter("user", user.getId());
        
        if(q.list().size() == 0)
        {
            Revision rev = new Revision();
            rev.setId(new Integer(0));
            return rev;
        }
        
        Integer revision = (Integer)q.list().get(0);
        System.out.println("Old revision: "+revision +" for "+type);
        Query revisionquery = ses.createQuery("from Revision where id=:id");
        revisionquery.setParameter("id", revision);        
        
        if(revisionquery.list().size() == 0)
        {
            Revision rev = new Revision();
            rev.setId(new Integer(0));
            return rev;
        }
        
        return (Revision)revisionquery.list().get(0);
    }
    
    public TrollingObjectCollection getTrollingObjects(EType type)
    {
        TrollingObjectCollection retval = new TrollingObjectCollection();
        
        Session ses = getSession();
        ses.beginTransaction();
        try {
        
            User user = getUser(ses, m_user);         
            Revision latest = getLatestRevision(ses, type, user);         
            retval.setRevision(latest);

            Query q = ses.createQuery("from Trollingobject where type=:type and revision_id=:revision order by object_identifier");
            q.setParameter("revision", latest.getId());
            q.setString("type", type.name());
            for(Object o: q.list())
            {
                Trollingobject dao = (Trollingobject)o;
                dao.getKeyvalues().size();
                for(Object o2: dao.getPropertylists())
                {
                    Propertylist plist = (Propertylist)o2;
                    plist.getPropertylistitems().size();
                }
                
                TrollingObject object = new TrollingObject(dao);
                retval.addTrollingObject(object);
            }

            ses.getTransaction().commit();
        
        } catch(IndexOutOfBoundsException e) {
            ses.getTransaction().rollback();
            throw new RestfulException("no revision data");
        }
        catch(Exception e)
        {
            ses.getTransaction().rollback();
            throw new RestfulException(e.toString());
        }
        
        return retval;
    }
    
    public TrollingObject newTrollingObject(EType type, int id)
    {        
        Trollingobject dao = new Trollingobject();
        dao.setType(type.name());
        dao.setObjectIdentifier(id);
        return new TrollingObject(dao);
    }
    
    private Integer getMaxId(Session ses, Revision rev)
    {
        SQLQuery q = ses.createSQLQuery("select max(object_identifier) from trollingobject where revision_id=:rev");        
        q.setParameter("rev", rev);
        
        if(q.list().size() == 0)
        {
            return new Integer(1);
        }
        
        return (Integer)q.list().get(0);
    }

    public Integer appendTrollingObjects(EType type, TrollingObjectCollection objects)
    {       
        TrollingObjectCollection collection = getTrollingObjects(type);
        for(TrollingObject o: collection.getObjects())
        {
            Trollingobject dao = o.getDAO();
            dao.setId(null);
            for(Object kv: dao.getKeyvalues())
            {
                ((Keyvalue)kv).setId(null);
                
            }
            
            for(Object proplist: dao.getPropertylists())
            {
                ((Propertylist)proplist).setId(null);
                for(Object propitem: ((Propertylist)proplist).getPropertylistitems())
                {
                    ((Propertylistitem)propitem).setId(null);
                }
            }
        }
        setTrollingObjects(type, collection);
        
        Session ses = getSession();
        Integer retval = new Integer(0);
        ses.beginTransaction();
        try {
            User user = getUser(ses, m_user);
            Revision revision = getLatestRevision(ses, type, user);
            retval = revision.getId();
            int maxId = getMaxId(ses, revision).intValue();

            for(TrollingObject object: objects.getObjects())
            {
                Trollingobject dao = object.getDAO();                
                dao.setRevision(revision);
                maxId++;
                dao.setObjectIdentifier(maxId);
                ses.persist(dao);

                Set kvs = dao.getKeyvalues();
                for(Object o: kvs)
                {
                    Keyvalue kv = (Keyvalue)o;           
                    ses.persist(kv);
                }
                
                Set proplists = dao.getPropertylists();
                for(Object o: proplists)
                {
                    Propertylist proplist = (Propertylist)o;
                    ses.persist(proplist);
                    
                    Set propitems = proplist.getPropertylistitems();
                    for(Object o2: propitems)
                    {
                        Propertylistitem item = (Propertylistitem)o2;
                        ses.persist(item);
                    }
                }

            }        

            ses.getTransaction().commit();
        } catch (Exception e) {
            ses.getTransaction().rollback();
            e.printStackTrace();
            throw new RestfulException(e.toString());
        }
        
        return retval; 
    }
    
    public Integer setTrollingObjects(EType type, TrollingObjectCollection objects)
    {                
        Session ses = getSession();
        Integer retval = new Integer(0);
        ses.beginTransaction();
        try {
            User user = getUser(ses, m_user);
            checkForConflict(objects, ses, user);
            Revision revision = new Revision();
            revision.setUser(user);
            revision.setCreationDate(new Date());
            ses.persist(revision);
            retval = revision.getId();

            for(TrollingObject object: objects.getObjects())
            {
                Trollingobject dao = object.getDAO();                
                dao.setRevision(revision);
                ses.persist(dao);

                Set kvs = dao.getKeyvalues();
                for(Object o: kvs)
                {
                    Keyvalue kv = (Keyvalue)o;           
                    ses.persist(kv);
                }
                
                Set proplists = dao.getPropertylists();
                for(Object o: proplists)
                {
                    Propertylist proplist = (Propertylist)o;
                    ses.persist(proplist);
                    
                    Set propitems = proplist.getPropertylistitems();
                    for(Object o2: propitems)
                    {
                        Propertylistitem item = (Propertylistitem)o2;
                        ses.persist(item);
                    }
                }

            }        

            ses.getTransaction().commit();
        } catch (Exception e) {
            ses.getTransaction().rollback();
            e.printStackTrace();
            throw new RestfulException(e.toString());
        }
        
        return retval;
    }
    
    private static void checkForConflict(TrollingObjectCollection objects, Session ses, User user) {
        Revision currentRevision = objects.getRevision();
        if(objects.getObjects().size() == 0)
        {
            throw new RestfulException("Cannot commit. No data");
        }
        
        EType type = objects.getObjects().iterator().next().getType();
        Revision oldRevision = getLatestRevision(ses, type, user);

        if(!oldRevision.getId().equals(currentRevision.getId())) {
            String error = oldRevision.getId().toString()+" != "+currentRevision.getId().toString();
            throw new RestfulException("Cannot commit. Conflict with revision: "+error);
        }
    }
    
    
    public void appendTrollingObject(EType type, List<TrollingObject> objects)
    {
        
    }    
    
}
