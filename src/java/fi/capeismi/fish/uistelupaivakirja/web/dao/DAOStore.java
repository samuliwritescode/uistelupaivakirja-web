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
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;



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
            List types = q.getResultList();
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
            List types = q.getResultList();
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
    
    public void setUser(final User user) {
        new TransactionDecorator() { public Object doQuery() {
            this.session.merge(user);
            return null;
        }};
    }
    
    public User getUser() {
        User retval = getUser(this._user);
        if(retval == null) {
            throw new RestfulException("no user");
        }
        
        return retval;                        
    }
    
    public User getUser(final String username) {        
        return (User) new TransactionDecorator() {
            public Object doQuery() {
                Query q = this.session.createQuery("from User where username=:user");
                q.setParameter("user", username);

                List types = q.getResultList();
                for(Object o: types) {
                    return o;
                }
                
                return null;
            }
        }.getResult();                        
    }

    public void deleteObject(final int identifier, final int revision, final String type) {
        final Collection collection = getCollection(type);
        new TransactionDecorator() { public Object doQuery() {
            
            if(collection.getRevision() != revision) {
                System.out.println(collection.getRevision()+" != " +revision);
                throw new RestfulException("Cannot commit. Conflict with revision");
            }
                        
            for(Object o: collection.getTrollingobjects()) {
                Trollingobject to = (Trollingobject)o;
                if(to.getObjectIdentifier() == identifier) {
                    collection.getTrollingobjects().remove(o);
                    Trollingobject attached = this.session.find(Trollingobject.class, to.getId());
                    this.session.remove(attached);
                    //this.session.delete(o);
                    break;
                }
            }
            
            collection.getTrollingobjects().clear();
            collection.setRevision(revision+1);
            this.session.merge(collection);            
            
            return null;
        }};
    }
    
    private Trollingobject containsTrollingobject(Collection collection, int identifier) {
        for(Object o: collection.getTrollingobjects()) {
            Trollingobject object = (Trollingobject)o;
            if(object.getObjectIdentifier() == identifier) {
                return object;
            }
        }
        
        return null;
    }

    public void updateObject(final Trollingobject object, final int revision) {
        new TransactionDecorator() { public Object doQuery() {
            Collection collection = object.getCollection();
            if(collection.getRevision() != revision) {
                System.out.println(collection.getRevision()+" != " +revision);
                throw new RestfulException("Cannot commit. Conflict with revision");
            }
            
            Query query = this.session.createQuery("from Trollingobject where collection_id=:collection and object_identifier=:obid");
            query.setParameter("collection", collection);
            query.setParameter("obid", object.getObjectIdentifier());
            
            for(Object o: query.getResultList()) {
            	Trollingobject tobject = (Trollingobject)o;
            	System.out.println("deleting "+tobject.getObjectIdentifier());
            	this.session.remove(tobject);
            }
            
            for(Trollingobject o: collection.getTrollingobjects()) {
                //find if already exist, then remove old
                if(o.getObjectIdentifier() == object.getObjectIdentifier()) {
                    collection.getTrollingobjects().remove(o);
                    //this.session.remove(o);
                    //break;
                }
            }
            
            System.out.println("collection size: "+collection.getTrollingobjects().size());
            
            collection.getTrollingobjects().add(object);
            this.session.persist(object);
            
            collection.getTrollingobjects().clear();
            collection.setRevision(collection.getRevision()+1);            
            this.session.merge(collection);
            return null;
        }};
    }

    
    private static abstract class TransactionDecorator {
        Object result;
        EntityManager session;
        private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("uisteluweb");
        
        public TransactionDecorator() {
            doTransaction();
        }
        
        private void doTransaction() {
            this.session = getSession();
            EntityTransaction tx = this.session.getTransaction();
            tx.begin();
            try {
                this.result = doQuery();
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                tx.rollback();
                throw new RestfulException(e);
            }
        }
        
        public abstract Object doQuery() throws Exception;
        
        public Object getResult() {
            return this.result;
        }
        
        private EntityManager getSession() {
        	//EntityManager entityManager = Persistence.createEntityManagerFactory("uisteluweb").createEntityManager();
        	return factory.createEntityManager();
        }
    }
    
    public void setCollection(final Collection collectionDAO) {
        final User user = getUser();
        new TransactionDecorator() { public Object doQuery() {
            Query q = this.session.createQuery("from Collection where user_id=:user and type_id=:type");
            q.setParameter("user", user);
            q.setParameter("type", collectionDAO.getType());
            
            int oldRevision = 0;
            for(Object o: q.getResultList()) {
                Collection oldCollection = (Collection)o;
                oldRevision = oldCollection.getRevision();
                this.session.remove(o);
            }
            
            if(collectionDAO.getRevision() != oldRevision) {
                System.out.println(collectionDAO.getRevision()+" != " +oldRevision);
                throw new RestfulException("Cannot commit. Conflict with revision");
            }
            
            for(Trollingobject to: collectionDAO.getTrollingobjects()) {
                to.setCollection(collectionDAO);
                for(Event e: to.getEvents()) {
                    e.setTrollingobject(to);
                }
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
            for(Object o: q.getResultList()) {
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
            final ViewContainer orm = new ViewContainer(view);

            /*
            this.session.doWork(new Work() {

				@Override
				public void execute(Connection conn) throws SQLException {
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
					
				}
            	
            });
*/
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
