drop view if exists spinneritem_view;
create view spinneritem_view as
select fish_species, fish_method, fish_getter from event
group by fish_species, fish_method, fish_getter
;
