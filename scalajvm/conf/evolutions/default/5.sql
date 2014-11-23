# --- !Ups
create index quote_time on quote (time desc);
create index quote_rating on quote (rating desc);

# --- !Downs
drop index quote_time;
drop index quote_rating;
