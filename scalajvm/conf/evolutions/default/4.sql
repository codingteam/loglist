# --- !Ups
create table if not exists queued_quote (
  id bigserial primary key,
  time timestamp not null,
  content varchar not null,
  submitter_ip varchar
);

# --- !Downs
drop table queued_quote;
