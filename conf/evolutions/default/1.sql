# --- !Ups
create table if not exists "QUOTE" (
  "ID" bigserial primary key,
  "TIME" timestamp not null,
  "CONTENT" varchar not null
);

# --- !Downs
drop table if exists "QUOTE";
