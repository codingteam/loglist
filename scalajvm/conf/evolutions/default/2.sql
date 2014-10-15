# --- !Ups
alter table "QUOTE" rename column "ID" to id;
alter table "QUOTE" rename column "TIME" to time;
alter table "QUOTE" rename column "CONTENT" to content;
alter table "QUOTE" rename to quote;

# --- !Downs
alter table quote rename column id to "ID";
alter table quote rename column time to "TIME";
alter table quote rename column content to "CONTENT";
alter table quote rename to "QUOTE";
