# --- !Ups
alter table quote
add column source_url varchar(128) null;

alter table api_key
add column source_url varchar(128) null;

# --- !Downs
alter table quote
drop column source_url;

alter table api_key
drop column source_url;
