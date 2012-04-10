package fi.capeismi.fish.uistelupaivakirja.web.dao;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-04-10T17:56:44.160+0300")
@StaticMetamodel(Trollingobject.class)
public class Trollingobject_ {
	public static volatile SingularAttribute<Trollingobject, Integer> id;
	public static volatile SingularAttribute<Trollingobject, Collection> collection;
	public static volatile SingularAttribute<Trollingobject, Integer> object_identifier;
	public static volatile SingularAttribute<Trollingobject, String> city;
	public static volatile SingularAttribute<Trollingobject, String> color;
	public static volatile SingularAttribute<Trollingobject, String> color_back;
	public static volatile SingularAttribute<Trollingobject, String> color_belly;
	public static volatile SingularAttribute<Trollingobject, String> color_class;
	public static volatile SingularAttribute<Trollingobject, String> color_contrast;
	public static volatile SingularAttribute<Trollingobject, String> color_side;
	public static volatile SingularAttribute<Trollingobject, String> date;
	public static volatile SingularAttribute<Trollingobject, String> description;
	public static volatile SingularAttribute<Trollingobject, String> favorite;
	public static volatile SingularAttribute<Trollingobject, String> lure_type;
	public static volatile SingularAttribute<Trollingobject, String> maker;
	public static volatile SingularAttribute<Trollingobject, String> model;
	public static volatile SingularAttribute<Trollingobject, String> name;
	public static volatile SingularAttribute<Trollingobject, String> nickname;
	public static volatile SingularAttribute<Trollingobject, String> place;
	public static volatile SingularAttribute<Trollingobject, String> routefile;
	public static volatile SingularAttribute<Trollingobject, String> size;
	public static volatile SingularAttribute<Trollingobject, String> time_end;
	public static volatile SingularAttribute<Trollingobject, String> time_start;
	public static volatile SingularAttribute<Trollingobject, String> waypointfile;
	public static volatile SetAttribute<Trollingobject, Event> events;
	public static volatile SingularAttribute<Trollingobject, String> invisible;
	public static volatile SingularAttribute<Trollingobject, String> notvisible;
}
