drop view if exists eventview;
create view eventview as 
select    
    event.id,
    trolling_id,
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='type' and event_id=event.id) as 'type',
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_species' and event_id=event.id) as 'fish_species',
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_method' and event_id=event.id) as 'fish_method',
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_getter' and event_id=event.id) as 'fish_getter',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_weight' and event_id=event.id) as SIGNED) as 'fish_weight',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_width' and event_id=event.id) as SIGNED) as 'fish_width',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_coord_lat' and event_id=event.id) as SIGNED) as 'fish_coord_lat',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_coord_lon' and event_id=event.id) as SIGNED) as 'fish_coord_lon',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_air_temp' and event_id=event.id) as SIGNED) as 'fish_air_temp',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_group' and event_id=event.id) as SIGNED) as 'fish_group',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_group_amount' and event_id=event.id) as SIGNED) as 'fish_group_amount',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_spot_depth' and event_id=event.id) as SIGNED) as 'fish_spot_depth',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_time' and event_id=event.id) as SIGNED) as 'fish_time'
from event;
    
drop view if exists toplist;
create view toplist as
select 
    user.username,
    object_identifier, 
    (select max(value) from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) join event as ev on(ev.id=event_id) where ev.id=event.id and keyname = 'fish_species') as species,
    cast((select max(value) from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) join event as ev on(ev.id=event_id) where ev.id=event.id and keyname = 'fish_weight') as SIGNED) as weight,
    (select max(value) from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) join event as ev on(ev.id=event_id) where ev.id=event.id and keyname = 'fish_coord_lat') as lat,
    (select max(value) from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) join event as ev on(ev.id=event_id) where ev.id=event.id and keyname = 'fish_coord_lon') as lon
from user, type
    join collection on(type_id=type.id)
    join trollingobject on(collection_id=collection.id)
    join event on(trolling_id=trollingobject.id)
where 
    user.id=user_id and
    type.name = 'trip'
having
    species is not null and
    weight is not null;
