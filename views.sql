drop view if exists spinneritem_view;
create view spinneritem_view as
select distinct 'fish_species' as keyname, fish_species as value, user_id from event join trollingobject on(trolling_id=trollingobject.id) join collection on(collection_id=collection.id) where fish_species is not null
union
select distinct 'fish_method' as keyname, fish_method as value, user_id from event join trollingobject on(trolling_id=trollingobject.id) join collection on(collection_id=collection.id) where fish_method is not null
union
select distinct 'fish_getter' as keyname, fish_getter as value, user_id from event join trollingobject on(trolling_id=trollingobject.id) join collection on(collection_id=collection.id) where fish_getter is not null
order by keyname, value
;

