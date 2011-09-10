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

import fi.capeismi.fish.uistelupaivakirja.web.model.TableView;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;
import fi.capeismi.fish.uistelupaivakirja.web.model.SearchObject;
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
import org.hibernate.Transaction;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class DAOStore {
    
    private static DAOStore _instance = null;
    private String _user = null;
    
    public DAOStore(String user) {
        this._user = user;
    }        

    public static Type getType(final String type) {
        return (Type) new TransactionDecorator() { public Object doQuery() {
            Query q = this.session.createQuery("from Type where name=:name");
            q.setParameter("name", type);
            List types = q.list();
            for(Object o: types) {
                return o;
            }

            throw new RestfulException("no such collection");
        }}.getResult();       
    }

    public Collection getCollection(String typename) {
        Type type = getType(typename);
        return getCollection(type);
    }    
            
    private Collection getCollection(final Type typeDAO) {
        final User user = getUser();
        return (Collection) new TransactionDecorator() { public Object doQuery() {
            Query q = this.session.createQuery("from Collection where user_id=:user and type_id=:type");
            q.setParameter("type", typeDAO);
            q.setParameter("user", user);
            List types = q.list();
            for(Object o: types) {
                return o;
            }

            return new Collection();
        }}.getResult();
    }
    
    public void addUser(final User user) {
        new TransactionDecorator() { public Object doQuery() {
            this.session.persist(user);
            return null;
        }};
    }
    
    public User getUser() {        
        return (User) new TransactionDecorator() {
            public Object doQuery() {
                Query q = this.session.createQuery("from User where username=:user");
                q.setParameter("user", _user);

                List types = q.list();
                for(Object o: types) {
                    return o;
                }
                
                throw new RestfulException("no user");
            }
        }.getResult();                        
    }
    

    
    private static abstract class TransactionDecorator {
        Object result;
        Session session;
        public TransactionDecorator() {
            doTransaction();
        }
        
        private void doTransaction() {
            this.session = getSession();
            Transaction tx = this.session.beginTransaction();
            try {
                this.result = doQuery();
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new RestfulException(e);
            }
        }
        
        public abstract Object doQuery() throws Exception;
        
        public Object getResult() {
            return this.result;
        }
        
        private Session getSession() {
            SessionFactory fac = TrollingHibernateUtil.getSessionFactory();
            return fac.getCurrentSession();
        }
    }
    
    public void setCollection(final Collection collectionDAO) {
        final User user = getUser();
        new TransactionDecorator() { public Object doQuery() {
            Query q = this.session.createQuery("from Collection where user_id=:user and type_id=:type");
            q.setParameter("user", user);
            q.setParameter("type", collectionDAO.getType());
            
            int oldRevision = 0;
            for(Object o: q.list()) {
                Collection oldCollection = (Collection)o;
                oldRevision = oldCollection.getRevision();
                this.session.delete(o);
            }
            
            if(collectionDAO.getRevision() != oldRevision) {
                System.out.println(collectionDAO.getRevision()+" != " +oldRevision);
                throw new RestfulException("Cannot commit. Conflict with revision");
            }
                
            collectionDAO.setUser(user);
            collectionDAO.setRevision(oldRevision+1);
            this.session.persist(collectionDAO);
            return null;
        }};
    }
    
    public void appendCollection(final Collection collectionDAO) {
        final User user = getUser();
        new TransactionDecorator() { public Object doQuery() {
            Query q = this.session.createQuery("from Collection where user_id=:user and type_id=:type");
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
            this.session.persist(oldCollection);
            this.session.getTransaction().commit();        
            collectionDAO.setRevision(oldCollection.getRevision());
            return null;
        }};
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

    private TableView getView(final String view, final ConcreteSearchObject search) {
        return (TableView) new TransactionDecorator() { public Object doQuery() throws Exception{
            //TODO: SQL injection hardening
            ViewContainer orm = new ViewContainer(view);
            
            Connection conn = this.session.connection();
            Statement st = conn.createStatement();
            String searchSQL = "select * from " +view+ "_view "+search.getSQL();
            
            ResultSet res = st.executeQuery(searchSQL);           
            List<String> columns = new ArrayList<String>();
            for(int loop=1; loop <= res.getMetaData().getColumnCount(); loop++) {                
                String colname = res.getMetaData().getColumnName(loop);
                columns.add(colname);
            }            
            
            while(res.next()) {
                Map<String, String> row = new HashMap<String, String>();
                for(String colname: columns) {
                    row.put(colname, res.getString(colname));
                }
                orm.add(row);
            }
            return orm;
        }}.getResult();
    }
    
    public SearchObject searchObject(String view) {
        return new ConcreteSearchObject(view);
    }
    
    private class ConcreteSearchObject implements SearchObject {
        private Map<String, String> constraints;
        private String view;
        
        public ConcreteSearchObject(String view) {
            this.constraints = new HashMap<String, String>();
            this.view = view;
        }
        
        public void setUser(User user) {
            addFieldConstraint("user_id", user.getId().toString());
        }

        public void addFieldConstraint(String field, String constraint) {
            this.constraints.put(field, constraint);
        }
        
        public String getSQL() {
            String searchSQL = "where ";
            for(Map.Entry<String, String> kvpair: this.constraints.entrySet()) {
                searchSQL += kvpair.getKey()+"='"+kvpair.getValue()+"' AND ";                
            }
            
            searchSQL += " 1=1 limit 15";
            return searchSQL;
        }

        public TableView doSearch() {
            return getView(this.view, this);
        }

        public void setView(String view) {
            this.view = view;
        }
    }
}
