# --- !Ups
create table if not exists approver (
  id serial primary key,
  name varchar not null,
  email varchar not null
);

alter table queued_quote
  add column token text not null default
  encode(gen_random_bytes(128), 'hex');

# --- !Downs
alter table queued_quote drop column token;
drop table if exists approver;