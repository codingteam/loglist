# --- !Ups
alter table quote
add column source varchar(128) not null default '';

alter table suggested_quote
add column source varchar(128) not null default '';

create table api_key (
  id bigserial primary key,
  key varchar(256) not null,
  source varchar(128) not null
);

update quote
set source = case
  when id <= 7915 then 'loglist.ru'
  else 'user'
end;

update suggested_quote
set source = 'user';

# --- !Downs
alter table quote
drop column source;

alter table suggested_quote
drop column source;

drop table api_key;