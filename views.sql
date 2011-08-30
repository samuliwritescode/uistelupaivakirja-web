drop view if exists spinneritem_view;
create view spinneritem_view as
select distinct 'fish_species' as keyname, fish_species as value, user_id from event join trollingobject on(trolling_id=trollingobject.id) join collection on(collection_id=collection.id) where fish_species is not null
union
select distinct 'fish_method' as keyname, fish_method as value, user_id from event join trollingobject on(trolling_id=trollingobject.id) join collection on(collection_id=collection.id) where fish_method is not null
union
select distinct 'fish_getter' as keyname, fish_getter as value, user_id from event join trollingobject on(trolling_id=trollingobject.id) join collection on(collection_id=collection.id) where fish_getter is not null
order by keyname, value
;

drop view if exists fishstat_view;
create view fishstat_view as
select fish_species, place.name as place_name, fish_weight, fish_length, lure.maker as lure_maker, user_id, trollingobject.date, username
from event 
    join trollingobject on(event.trolling_id=trollingobject.id)
    join collection on(trollingobject.collection_id=collection.id)
    join user on(user_id=user.id)
    join trollingobject place on(trollingobject.place=place.object_identifier)
    join trollingobject lure on(event.lure=lure.object_identifier)
where
    fish_species is not null and
    place.id in(select trollingobject.id from trollingobject join collection on(collection_id=collection.id) and type_id=2) and
    lure.id in(select trollingobject.id from trollingobject join collection on(collection_id=collection.id) and type_id=3) and
    type_id=1
order by date desc, fish_time desc
limit 10
;

drop view if exists fishrecord_view;
create view fishrecord_view as
select fish_species, place.name as place_name, cast(fish_weight as unsigned) as fish_weight, fish_length, lure.maker as lure_maker, user_id, trollingobject.date, username
from event 
    join trollingobject on(event.trolling_id=trollingobject.id)
    join collection on(trollingobject.collection_id=collection.id)
    join user on(user_id=user.id)
    join trollingobject place on(trollingobject.place=place.object_identifier)
    join trollingobject lure on(event.lure=lure.object_identifier)
where
    fish_species is not null and
    type_id=1 and
    place.id in(select trollingobject.id from trollingobject join collection on(collection_id=collection.id) and type_id=2) and
    lure.id in(select trollingobject.id from trollingobject join collection on(collection_id=collection.id) and type_id=3)
order by fish_weight desc, fish_time desc
limit 10
;

drop view if exists tripstat_view;
create view tripstat_view as
select trollingobject.date, 
    username, 
    place.name as place_name, 
    coalesce((select sum(coalesce(cast(fish_group_amount as unsigned), 1)) from event where event.trolling_id=trollingobject.id and type in('1', '3')), 'MP') as fish_amount
from trollingobject 
    join collection on(trollingobject.collection_id=collection.id)
    join user on(user_id=user.id)
    join trollingobject place on(trollingobject.place=place.object_identifier)
where
    place.id in(select trollingobject.id from trollingobject join collection on(collection_id=collection.id) and type_id=2) and
    type_id=1
order by date desc
limit 10
;

select * from tripstat_view;