LogList [![gitter room][gitter-logo]][gitter] [![Build status][build-status]][travis]
=======

[Reincarnation][loglist] of [the famous service][loglist-original].

Additional Documentation
------------------------

- [API][docs-api]
- [Deployment][docs-deployment]
- [Administration][docs-admin]

Database Configuration
----------------------

Install PostgreSQL, create database and user for LogList. Install and
activate the following extensions:

* pgcrypto

If you are using Debian-like distro, install extensions with apt-get install postgresql-contrib.
You can activate extension pgcrypto with following line in postgresql console:
```SQL
CREATE EXTENSION pgcrypto;
```

Development Configuration
-------------------------

### PostgreSQL container

If you want to quickly run LogList in a dockerized environment while keeping the
code locally (e.g. to debug the code), run the following commands to start
development PostgreSQL instance:

```console
$ docker-compose run --publish '5432:5432' db
```

### Environment Variables

Possible environment variables can be found in the `devenv.example`
file. Supposed workflow:

    $ cp devenv.example devenv # devenv is gitignored so you won't accidentally commit it
    $ emacs devenv
      ... Modify variables ...
      ... All variable names should be self-explanatory
    $ . ./devenv

The same sample setup for Windows:

    PS> cp devenv.ps1.example devenv.ps1
    PS> notepad devenv.ps1 # Edit the configuration...
    PS> .\devenv.ps1

### Running the application

To run the application in a container, run the following command:

```console
$ docker-compose --project-name loglist up
```

And then open `http://localhost:9000` in a browser.

To run the application locally, make sure you've set up the environment
variables as described in the previous section, and then execute the following
command:

```console
$ sbt scalajvm/run
```

Testing
-------

Automated test suite requires empty database. To start a new container with an
empty database, you may use a command like this:

```console
$ docker-compose run --rm --name 'loglist_test' --publish '5432:5432' db
```

Then, set up the environment variables and run the test suite:

```console
$ sbt test
```

License
-------

Loglist is licensed under the terms of MIT License. See License.md file for
details.

Some third-party components have their own licenses, please consult the
corresponding site section for further details.

[docs-admin]: docs/Admin.md
[docs-api]: docs/API.md
[docs-deployment]: docs/Deployment.md

[gitter]: https://gitter.im/codingteam/loglist
[loglist]: https://www.loglist.xyz/
[loglist-original]: http://loglist.ru/
[travis]: https://travis-ci.org/codingteam/loglist

[build-status]: https://travis-ci.org/codingteam/loglist.png?branch=master
[gitter-logo]: https://badges.gitter.im/Join%20Chat.svg
