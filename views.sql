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

drop view if exists fishmapview;
create view fishmapview as 
select    
    event.id,
    trolling_id,
    user_id,
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='type' and event_id=event.id) as 'type',
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_species' and event_id=event.id) as 'fish_species',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_weight' and event_id=event.id) as SIGNED) as 'fish_weight',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_length' and event_id=event.id) as SIGNED) as 'fish_length',
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_coord_lat' and event_id=event.id) as 'fish_coord_lat',
    (select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_coord_lon' and event_id=event.id) as 'fish_coord_lon',    
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_group_amount' and event_id=event.id) as SIGNED) as 'fish_group_amount',
    cast((select value from keyvalue join eventproperty on(keyvalue.id=keyvalue_id) where keyname='fish_time' and event_id=event.id) as TIME) as 'fish_time',
    cast((select value from keyvalue join property on(keyvalue.id=keyvalue_id) where keyname='date' and trolling_id=trollingobject.id) as DATE) as 'date'
from event
    join trollingobject on(trolling_id=trollingobject.id)
    join collection on(collection_id=collection.id)
having fish_coord_lat is not null and
    fish_coord_lon is not null and
    (type=1 OR type=3)
;

drop view if exists spinneritem;
create view spinneritem as
select user_id, keyname, value 
from collection 
join trollingobject on(collection_id=collection.id) 
join event on(trolling_id=trollingobject.id) 
join eventproperty on(event_id=event.id) 
join keyvalue on(keyvalue_id=keyvalue.id) 
where keyname in('fish_species', 'fish_method', 'fish_getter') 
group by user_id, value, keyname
order by keyname, value
;