package fi.capeismi.fish.uistelupaivakirja.web.dao;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-04T17:43:24.204+0300")
@StaticMetamodel(Collection.class)
public class Collection_ {
	public static volatile SingularAttribute<Collection, Integer> id;
	public static volatile SingularAttribute<Collection, User> user;
	public static volatile SingularAttribute<Collection, Type> type;
	public static volatile SingularAttribute<Collection, Integer> revision;
	public static volatile SetAttribute<Collection, Trollingobject> trollingobjects;
}
