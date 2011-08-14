drop view if exists trollingproperty_view;
create view trollingproperty_view as
select trolling_id, 
    object_identifier,
    user_id,
    keyname,    
    value 
    from trollingobject 
    join collection on(collection_id=collection.id)
    join property on(trolling_id=trollingobject.id) 
    join keyvalue on(keyvalue_id=keyvalue.id)
;

drop view if exists eventproperty_view;
create view eventproperty_view as
select trolling_id, 
    object_identifier,
    user_id,
    event_id,
    keyname,    
    value 
    from trollingobject 
    join collection on(collection_id=collection.id)
    join event on(trolling_id=trollingobject.id)
    join eventproperty on(event_id=event.id) 
    join keyvalue on(keyvalue_id=keyvalue.id)
;

drop view if exists spinneritem_view;
create view spinneritem_view as
select * from eventproperty_view
where keyname in('fish_species', 'fish_method', 'fish_getter') 
group by user_id, value, keyname
order by keyname, value
;

drop view if exists fishmap_view;
create view fishmap_view as 
select    
    trolling_id,
    object_identifier,
    user_id,
    user.username,
    (select value from eventproperty_view where event_id=event.id and keyname = 'type') as 'type',
    (select value from eventproperty_view where event_id=event.id and keyname = 'fish_species' and event_id=event.id) as 'fish_species',
    (select value from eventproperty_view where event_id=event.id and keyname = 'fish_coord_lon') as 'fish_coord_lon',
    (select value from eventproperty_view where event_id=event.id and keyname = 'fish_coord_lat') as 'fish_coord_lat',
    cast((select value from eventproperty_view where event_id=event.id and keyname ='fish_weight' and event_id=event.id) as SIGNED) as 'fish_weight',
    cast((select value from eventproperty_view where event_id=event.id and keyname ='fish_length' and event_id=event.id) as SIGNED) as 'fish_length',
    cast((select value from eventproperty_view where event_id=event.id and keyname ='fish_time' and event_id=event.id) as TIME) as 'fish_time',
    cast((select value from trollingproperty_view where keyname='date' and trolling_id=trollingobject.id) as DATE) as 'date'
from event
    join trollingobject on(trolling_id=trollingobject.id)
    join collection on(trollingobject.collection_id=collection.id)
    join user on(user_id=user.id)
having 
    (type=1 OR type=3) and
    fish_coord_lon is not null and
    fish_coord_lat is not null
order by date desc, fish_time desc
;

drop view if exists fishstat_view;
create view fishstat_view as 
select    
    trolling_id,
    object_identifier,
    user_id,
    user.username,
    (select value from eventproperty_view where event_id=event.id and keyname = 'type') as 'type',
    (select value from eventproperty_view where event_id=event.id and keyname = 'fish_species' and event_id=event.id) as 'fish_species',
    (select value from eventproperty_view where event_id=event.id and keyname = 'lure') as 'lure_id',
    (select value from trollingproperty_view where trolling_id=trollingobject.id and keyname = 'place') as 'place_id',
    (select value from trollingproperty_view where object_identifier=lure_id and keyname='maker') as 'lure_maker',
    (select value from trollingproperty_view where object_identifier=place_id and keyname='name') as 'place_name',
    cast((select value from eventproperty_view where event_id=event.id and keyname ='fish_weight' and event_id=event.id) as SIGNED) as 'fish_weight',
    cast((select value from eventproperty_view where event_id=event.id and keyname ='fish_length' and event_id=event.id) as SIGNED) as 'fish_length',
    coalesce(cast((select value from eventproperty_view where event_id=event.id and keyname ='fish_group_amount' and event_id=event.id) as SIGNED), 1) as 'fish_group_amount',
    cast((select value from eventproperty_view where event_id=event.id and keyname ='fish_time' and event_id=event.id) as TIME) as 'fish_time',
    cast((select value from trollingproperty_view where keyname='date' and trolling_id=trollingobject.id) as DATE) as 'date'
from event
    join trollingobject on(trolling_id=trollingobject.id)
    join collection on(trollingobject.collection_id=collection.id)
    join user on(user_id=user.id)
having 
    (type=1 OR type=3)
order by date desc, fish_time desc limit 10
;