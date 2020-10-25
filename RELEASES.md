LogList Release Notes
=====================

2.0.0
-----
This is the first release after Play framework update.

Remember to update the configuration file (`application.conf`):
- `evolutions.autocommit` is now `play.evolutions.autocommit`
- `application.secret` is now `play.http.secret.key`
- `application.langs` is now `play.i18n.langs` (and it should be a list rather
  than a single string)
