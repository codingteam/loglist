Site administration
===================

How to grant API access to the client
-------------------------------------

To access certain site APIs, client must pass an API token. To create a new token,
execute the following SQL query:

```sql
insert into api_key(source, key, source_url) values ('source tag', 'api-key', 'http://url')
-- You may omit the URL, it will be null by default
```
