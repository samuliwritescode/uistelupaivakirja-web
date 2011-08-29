drop view if exists trollingproperty_view;
create view trollingproperty_view as
select trolling_id, 
    object_identifier,
    user_id,
    keyname,    
    value 
    from trollingobject 
    join collection on(collection_id=collection.id)
    join trollingproperty on(trolling_id=trollingobject.id) 
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
;

drop view if exists spinneritem_view;
create view spinneritem_view as
select * from eventproperty_view
where keyname in('fish_species', 'fish_method', 'fish_getter') 
group by user_id, value, keyname
order by keyname, value
;

drop view if exists event_view;
create view event_view as
select
    event.id,
    trolling_id,
    group_concat(IF(keyname = 'fish_species', value, NULL)) as fish_species,
    group_concat(IF(keyname = 'fish_coord_lon', value, NULL)) as fish_coord_lon,
    group_concat(IF(keyname = 'fish_coord_lat', value, NULL)) as fish_coord_lat,
    group_concat(IF(keyname = 'fish_length', value, NULL)) as fish_length,
    group_concat(IF(keyname = 'fish_weight', value, NULL)) as fish_weight,
    group_concat(IF(keyname = 'fish_time', value, NULL)) as fish_time,
    group_concat(IF(keyname = 'fish_method', value, NULL)) as fish_method,
    group_concat(IF(keyname = 'fish_getter', value, NULL)) as fish_getter,
    group_concat(IF(keyname = 'fish_group_amount', value, NULL)) as fish_group_amount,
    group_concat(IF(keyname = 'lure', value, NULL)) as lure_id,
    group_concat(IF(keyname = 'type', value, NULL)) as type
from
    event
    join eventproperty on(event.id=event_id)
group by event.id
;

drop view if exists trip_view;
create view trip_view as
select
    trollingobject.id as id,
    type_id,
    user_id,
    collection_id,
    object_identifier,
    group_concat(IF(keyname = 'date', value, NULL)) as date,
    group_concat(IF(keyname = 'description', value, NULL)) as description,
    group_concat(IF(keyname = 'place', value, NULL)) as place_id,    
    group_concat(IF(keyname = 'time_start', value, NULL)) as time_start,
    group_concat(IF(keyname = 'time_end', value, NULL)) as time_end
from
    trollingobject
    join trollingproperty on(trolling_id=trollingobject.id)
    join collection on(collection_id=collection.id)
    join type on(type_id=type.id)   
where type.name = 'trip'
group by trollingobject.id
;

drop view if exists lure_view;
create view lure_view as
select
    trollingobject.id as id,
    type_id,
    user_id,
    collection_id,
    object_identifier,
    group_concat(IF(keyname = 'maker', value, NULL)) as lure_maker,
    group_concat(IF(keyname = 'model', value, NULL)) as lure_model,
    group_concat(IF(keyname = 'color', value, NULL)) as lure_color,
    group_concat(IF(keyname = 'size', value, NULL)) as lure_size
from
    trollingobject
    join trollingproperty on(trolling_id=trollingobject.id)
    join collection on(collection_id=collection.id)
    join type on(type_id=type.id)   
where type.name = 'lure'
group by trollingobject.id
;

drop view if exists place_view;
create view place_view as
select
    trollingobject.id as id,
    type_id,
    user_id,
    collection_id,
    object_identifier,
    group_concat(IF(keyname = 'city', value, NULL)) as place_city,
    group_concat(IF(keyname = 'name', value, NULL)) as place_name
from
    trollingobject
    join trollingproperty on(trolling_id=trollingobject.id)
    join collection on(collection_id=collection.id)
    join type on(type_id=type.id)   
where type.name = 'place'
group by trollingobject.id
;


drop view if exists fishstat_view;
create view fishstat_view as 
select    
    trolling_id,
    trip_view.object_identifier,
    trip_view.user_id,
    user.username,
    type,
    place_name,
    fish_species,
    lure_id,
    fish_weight,
    fish_length,
    fish_time,
    trip_view.date,
    fish_group_amount,
    trip_view.place_id
from event_view
    join trip_view on(trolling_id=trip_view.id)
    join user on(user_id=user.id)
    join place_view on(place_id=place_view.object_identifier)
having 
    (type=1 OR type=3)
order by date desc, fish_time desc limit 10
;

--- RECORDS ---
drop view if exists fishrecord_view;
create view fishrecord_view as 
select 
    (select value from trollingproperty join event on(event.trolling_id = trollingproperty.trolling_id) where event.id = event_id and keyname='date') as date,
    (
        select value 
        from trollingproperty
            join trollingobject on(trolling_id=trollingobject.id)
        where 
            object_identifier in(select value from eventproperty e2 where e2.event_id = eventproperty.event_id and keyname='lure') and
            keyname='maker'
    ) as lure_maker,
    (
        select value 
        from trollingproperty
            join trollingobject on(trolling_id=trollingobject.id)
        where 
            object_identifier in(select value from trollingproperty join event on(event.trolling_id = trollingproperty.trolling_id) where event.id = event_id and keyname='place') and
            keyname='name'
    ) as place_name,
    (select value from eventproperty e2 where e2.event_id = eventproperty.event_id and keyname='fish_species') as fish_species,
    (select value from eventproperty e2 where e2.event_id = eventproperty.event_id and keyname='fish_getter') as fish_getter,
    (select value from eventproperty e2 where e2.event_id = eventproperty.event_id and keyname='fish_length') as fish_length,
    user.username,
    cast(value as unsigned) as fish_weight
from eventproperty
    join event on(event.ID = event_id)
    join trollingobject on(trolling_id=trollingobject.id)
    join collection on(collection_id=collection.id)
    join user on(user_id=user.id)
where 
    keyname='fish_weight' and
    user.id=1
order by fish_weight desc limit 10;

--- GPX ---
drop view if exists fishmap_view;
create view fishmap_view as 
select    
    trolling_id,
    object_identifier,
    user_id,
    user.username,
    type,
    fish_species,
    fish_coord_lon,
    fish_coord_lat,
    fish_weight,
    fish_time,
    date
from event_view
    join trip_view on(trolling_id=trip_view.id)
    join user on(user_id=user.id)
having 
    (type=1 OR type=3) and
    fish_coord_lon is not null and
    fish_coord_lat is not null
order by date desc, fish_time desc
;


drop view if exists tripstat_view;
create view tripstat_view as
select
    date,
    time_start,
    time_end,
    description,
    coalesce((select sum(fish_group_amount) from event_view where trolling_id=trip_view.id and type in('1', '3')),0) as fishes
from trip_view
    join type on(type_id=type.id)
    join user on(user_id=user.id)
where
    type.name = 'trip' and
    user.publishtrip = true
order by date desc, time_end desc
limit 10
;