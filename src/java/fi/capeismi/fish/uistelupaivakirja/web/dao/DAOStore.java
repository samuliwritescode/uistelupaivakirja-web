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

import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class DAOStore {
    
    private static DAOStore _instance = null;
    private String _user = null;
    
    private DAOStore(String user) {
        this._user = user;
    }
        
    public static DAOStore instance(String user) {
        if(_instance == null)
        {
            _instance = new DAOStore(user);
        }
        
        return _instance;
    }

    public static Type getType(String type) {
        Session ses = getSession();
        ses.beginTransaction();
         try {
        
            Query q = ses.createQuery("from Type where name=:name");
            q.setParameter("name", type);
            List types = q.list();
            for(Object o: types) {
                return (Type)o;
            }
            
            ses.getTransaction().commit();
        } 
        catch(Exception e)
        {
            ses.getTransaction().rollback();
            throw new RestfulException(e.toString());
        }
         
        return null;  
    }

    public Collection getCollection(String typename) {
        Type type = getType(typename);
        if(type == null) {
            return null;
        }

        return getCollection(type);
    }    
            
    private Collection getCollection(Type typeDAO) {
        Session ses = getSession();
        ses.beginTransaction();
         try {
        
            Query q = ses.createQuery("from Collection where user_id=:user and type_id=:type");
            q.setParameter("type", typeDAO);
            q.setParameter("user", getUser());
            List types = q.list();
            for(Object o: types) {
                return (Collection)o;
            }
                        
            ses.getTransaction().commit();      
            return new Collection();
        } 
        catch(Exception e)
        {
            ses.getTransaction().rollback();
            throw new RestfulException(e.toString());
        }
    }
    
    public User getUser() {
        Session ses = getSession();
        ses.beginTransaction();
         try {
        
            Query q = ses.createQuery("from User where username=:user");
            q.setParameter("user", this._user);
            
            List types = q.list();
            for(Object o: types) {
                return (User)o;
            }
            
            ses.getTransaction().commit();        
        } 
        catch(Exception e)
        {
            ses.getTransaction().rollback();
            throw new RestfulException(e);
        }
         
        throw new RestfulException("no user");
    }
    
    public void setCollection(Collection collectionDAO) {
        User user = getUser();
        Session ses = getSession();
        ses.beginTransaction();
        try {
            Query q = ses.createQuery("from Collection where user_id=:user and type_id=:type");
            q.setParameter("user", user);
            q.setParameter("type", collectionDAO.getType());
            
            int oldRevision = 0;
            for(Object o: q.list()) {
                Collection oldCollection = (Collection)o;
                oldRevision = oldCollection.getRevision();
                ses.delete(o);
            }
            
            if(collectionDAO.getRevision() != oldRevision) {
                System.out.println(collectionDAO.getRevision()+" != " +oldRevision);
                throw new RestfulException("Cannot commit. Conflict with revision");
            }
                
            collectionDAO.setUser(getUser());
            collectionDAO.setRevision(oldRevision+1);
            ses.persist(collectionDAO);
            ses.getTransaction().commit();        
        } 
        catch(Exception e)
        {
            ses.getTransaction().rollback();
            throw new RestfulException(e.toString());
        } 
    }
    
    public void appendCollection(Collection collectionDAO) {
        User user = getUser();
        Session ses = getSession();
        ses.beginTransaction();
        try {
            Query q = ses.createQuery("from Collection where user_id=:user and type_id=:type");
            q.setParameter("user", user);
            q.setParameter("type", collectionDAO.getType());
                        
            Collection oldCollection = null;
            for(Object o: q.list()) {
                oldCollection = (Collection)o;            
            }
            
            int id = getMaxId(oldCollection);
            for(Trollingobject inserted: collectionDAO.getTrollingobjects())
            {
                id++;
                inserted.setCollection(oldCollection);
                inserted.setObjectIdentifier(id);
                oldCollection.getTrollingobjects().add(inserted);
            }
            
            oldCollection.setRevision(oldCollection.getRevision()+1);
            ses.persist(oldCollection);
            ses.getTransaction().commit();        
            collectionDAO.setRevision(oldCollection.getRevision());
        } 
        catch(Exception e)
        {
            ses.getTransaction().rollback();
            throw new RestfulException(e.toString());
        } 
    }
    
    private int getMaxId(Collection collection) {
        int retval = 0;
        for(Trollingobject o: collection.getTrollingobjects()) {
            if(o.getObjectIdentifier() > retval) {
                retval = o.getObjectIdentifier();
            }                
        }
        return retval;
    }
    
    private static Session getSession() {
        SessionFactory fac = TrollingHibernateUtil.getSessionFactory();
        return fac.getCurrentSession();
    }

    public View getView(String view) {
        User user = getUser();
        Session ses = getSession();
        ses.beginTransaction();        

        try {
            View orm = View.ViewFactory.getInstance(view);
            
            Connection conn = ses.connection();
            Statement st = conn.createStatement();
           
            ResultSet res = st.executeQuery("select * from " +view+ "_view where user_id="+user.getId().toString());           
            List<String> columns = new ArrayList<String>();
            for(int loop=1; loop <= res.getMetaData().getColumnCount(); loop++) {                
                String colname = res.getMetaData().getColumnName(loop);
                columns.add(colname);
                orm.addColumn(colname);
            }
            
            
            while(res.next()) {
                Map<String, String> row = new HashMap<String, String>();
                for(String colname: columns) {
                    row.put(colname, res.getString(colname));
                }
                orm.add(row);
            }
            
            ses.getTransaction().commit();                   
            return orm;
        } 
        catch(Exception e)
        {
            ses.getTransaction().rollback();
            throw new RestfulException(e.toString());
        } 
    }
    
}
