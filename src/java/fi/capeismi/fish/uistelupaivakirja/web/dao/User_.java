package fi.capeismi.fish.uistelupaivakirja.web.dao;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-03T18:50:43.511+0300")
@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, Integer> id;
	public static volatile SingularAttribute<User, String> username;
	public static volatile SingularAttribute<User, String> password;
	public static volatile SingularAttribute<User, String> salt;
	public static volatile SingularAttribute<User, Boolean> publishlocation;
	public static volatile SingularAttribute<User, Boolean> publishplace;
	public static volatile SingularAttribute<User, Boolean> publishlure;
	public static volatile SingularAttribute<User, Boolean> publishfish;
	public static volatile SingularAttribute<User, Boolean> publishtrip;
	public static volatile SetAttribute<User, Collection> collection;
}
