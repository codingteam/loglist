# --- !Ups
alter table queued_quote rename to suggested_quote;

# --- !Downs
alter table suggested_quote rename to queued_quote;