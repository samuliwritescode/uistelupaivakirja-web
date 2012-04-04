package fi.capeismi.fish.uistelupaivakirja.web.dao;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-03T18:08:32.381+0300")
@StaticMetamodel(Type.class)
public class Type_ {
	public static volatile SingularAttribute<Type, Integer> id;
	public static volatile SingularAttribute<Type, String> name;
	public static volatile SetAttribute<Type, Collection> collections;
}
