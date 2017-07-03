Site administration
===================

How to grant API access to the client
-------------------------------------

To access some site API, client should pass API token. To add an API token,
execute the following SQL query:

```sql
insert into api_key(source, key) values ('source tag', 'api-key')
```
