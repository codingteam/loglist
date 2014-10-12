# --- !Ups
alter table quote add column rating int not null default 0;

# --- !Downs
alter table quote drop column rating;
