LogList Release Notes
=====================

2.0.1 (2021-01-13)
------------------
**Fixed issues**:
- [#235: NullPointerException when fetching the quotes through the API][issue-235]

2.0.0 (2020-10-30)
------------------
This is the first release after Play framework update.

Remember to update the configuration file (`application.conf`):
- `evolutions.autocommit` is now `play.evolutions.autocommit`
- `applyEvolutions.default` is now `play.evolutions.db.default.autoApply`
- `application.secret` is now `play.http.secret.key`
- `application.langs` is now `play.i18n.langs` (and it should be a list rather
  than a single string)
- there's a new required line: `play.modules.enabled += "scalikejdbc.PlayModule"`

- `logger.*` parameters has been migrated to `logback.xml`, remove them from
  your config

The following section is now required for use in production:

```
play.filters.enabled += play.filters.hosts.AllowedHostsFilter
play.filters.hosts {
  allowed = ["loglist.xyz"]
}
```

[issue-235]: https://github.com/codingteam/loglist/issues/235
