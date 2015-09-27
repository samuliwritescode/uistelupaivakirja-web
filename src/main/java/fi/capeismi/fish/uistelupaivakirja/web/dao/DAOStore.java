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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
@Component
public class DAOStore {

	public static Type getType(final String type) {
		return (Type) new TransactionDecorator() {
			@Override
			public Object doQuery() {
				Query q = this.session.createQuery("from Type where name=:name");
				q.setParameter("name", type);
				List types = q.getResultList();
				for (Object o : types) {
					return o;
				}

				throw new RestfulException("no such collection: " + type);
			}
		}.getResult();
	}

	public Collection getCollection(String typename, final User user) {
		Type type = getType(typename);
		return getCollection(type, user);
	}

	private Collection getCollection(final Type typeDAO, final User user) {
		return (Collection) new TransactionDecorator() {
			@Override
			public Object doQuery() {
				Query q = this.session.createQuery("from Collection where user_id=:user and type_id=:type");
				q.setParameter("type", typeDAO);
				q.setParameter("user", user);
				List types = q.getResultList();
				for (Object o : types) {
					return o;
				}

				return new Collection();
			}
		}.getResult();
	}

	public void addUser(final User user) {
		new TransactionDecorator() {
			@Override
			public Object doQuery() {
				this.session.persist(user);
				return null;
			}
		};
	}

	public void setUser(final User user) {
		new TransactionDecorator() {
			@Override
			public Object doQuery() {
				this.session.merge(user);
				return null;
			}
		};
	}

	public User getUser(final String username) {
		return (User) new TransactionDecorator() {
			@Override
			public Object doQuery() {
				Query q = this.session.createQuery("from User where username=:user");
				q.setParameter("user", username);

				List types = q.getResultList();
				for (Object o : types) {
					return o;
				}

				return null;
			}
		}.getResult();
	}

	public void deleteObject(final int identifier, final int revision, final String type, final User user) {
		final Collection collection = getCollection(type, user);
		new TransactionDecorator() {
			@Override
			public Object doQuery() {

				if (collection.getRevision() != revision) {
					System.out.println(collection.getRevision() + " != " + revision);
					throw new RestfulException("Cannot commit. Conflict with revision");
				}

				for (Object o : collection.getTrollingobjects()) {
					Trollingobject to = (Trollingobject) o;
					if (to.getObjectIdentifier() == identifier) {
						collection.getTrollingobjects().remove(o);
						Trollingobject attached = this.session.find(Trollingobject.class, to.getId());
						this.session.remove(attached);
						// this.session.delete(o);
						break;
					}
				}

				collection.getTrollingobjects().clear();
				collection.setRevision(revision + 1);
				this.session.merge(collection);

				return null;
			}
		};
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
			return factory.createEntityManager();
		}
	}

	public void setCollection(final Collection collectionDAO, final User user) {
		new TransactionDecorator() {
			@Override
			public Object doQuery() {
				Query q = this.session.createQuery("from Collection where user_id=:user and type_id=:type");
				q.setParameter("user", user);
				q.setParameter("type", collectionDAO.getType());

				int oldRevision = 0;
				for (Object o : q.getResultList()) {
					Collection oldCollection = (Collection) o;
					oldRevision = oldCollection.getRevision();
					this.session.remove(o);
				}

				if (collectionDAO.getRevision() != oldRevision) {
					System.out.println(collectionDAO.getRevision() + " != " + oldRevision);
					throw new RestfulException("Cannot commit. Conflict with revision");
				}

				for (Trollingobject to : collectionDAO.getTrollingobjects()) {
					to.setCollection(collectionDAO);
					for (Event e : to.getEvents()) {
						e.setTrollingobject(to);
					}
				}

				collectionDAO.setUser(user);
				collectionDAO.setRevision(oldRevision + 1);
				this.session.persist(collectionDAO);
				return null;
			}
		};
	}

	public void appendCollection(final Collection collectionDAO, final User user) {
		new TransactionDecorator() {
			@Override
			public Object doQuery() {
				Query q = this.session.createQuery("from Collection where user_id=:user and type_id=:type");
				q.setParameter("user", user);
				q.setParameter("type", collectionDAO.getType());

				Collection oldCollection = null;
				for (Object o : q.getResultList()) {
					oldCollection = (Collection) o;
				}

				int id = getMaxId(oldCollection);
				for (Trollingobject inserted : collectionDAO.getTrollingobjects()) {
					id++;
					inserted.setCollection(oldCollection);
					inserted.setObjectIdentifier(id);
					oldCollection.getTrollingobjects().add(inserted);
					for (Event ev : inserted.getEvents()) {
						ev.setTrollingobject(inserted);
					}
				}

				oldCollection.setRevision(oldCollection.getRevision() + 1);
				this.session.persist(oldCollection);

				collectionDAO.setRevision(oldCollection.getRevision());
				return null;
			}
		};
	}

	private int getMaxId(Collection collection) {
		int retval = 0;
		for (Trollingobject o : collection.getTrollingobjects()) {
			if (o.getObjectIdentifier() > retval) {
				retval = o.getObjectIdentifier();
			}
		}
		return retval;
	}
}
