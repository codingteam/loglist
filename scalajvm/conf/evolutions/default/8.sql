# --- !Ups
create table if not exists staged_quote (
  id serial primary key,
  token varchar(32) not null default encode(gen_random_bytes(16), 'hex'),
  content varchar not null,
  time timestamp not null,
  stager_ip varchar
);

create table if not exists maintainer (
  id serial primary key,
  name varchar not null,
  email varchar not null
);

create table if not exists disabled_action (
  name varchar not null unique
);

# --- !Downs
drop table if exists disabled_action;
drop table if exists maintainer;
drop table if exists staged_quote;
