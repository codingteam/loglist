LogList [![Docker Image][badge.docker]][docker-hub]
=======

[Reincarnation][loglist] of [the famous service][loglist-original].

Additional Documentation
------------------------

- [API][docs-api]
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

Publishing
----------

This application uses Docker for deployment. To create a Docker image, use the
following command:

```console
$ docker build -t codingteam/loglist:$LOGLIST_VERSION -t codingteam/loglist:latest -f loglist.dockerfile .
```

(where `$LOGLIST_VERSION` is the version for the image to publish)

Then push the image to the Docker Hub:

```console
$ docker login # if necessary
$ docker push codingteam/loglist:$LOGLIST_VERSION
$ docker push codingteam/loglist:latest
```

Deployment
----------
Consider using the following [Ansible][ansible] task for deployment:
```yaml
- name: Set up the application container
  community.docker.docker_container:
    name: loglist.app
    image_name_mismatch: recreate
    image: codingteam/loglist:{{ loglist_version }}
    published_ports:
      - '9000:9000'
    env:
      APPLY_EVOLUTIONS_SILENTLY: 'true'
      APPROVAL_EMAIL: '{{ loglist_secrets.approval_email.name }}'
      APPROVAL_EMAIL_PASSWORD: '{{ loglist_secrets.approval_email.password }}'
      APPROVAL_SMTP_HOST: '{{ loglist_secrets.approval_email.smtp_host }}'
      BASIC_AUTH_PASSWORD: '{{ loglist_secrets.basic_auth.password }}'
      BASIC_AUTH_USERNAME: '{{ loglist_secrets.basic_auth.username }}'
      DATABASE_URL: 'jdbc:postgresql://loglist.postgresql/loglist?user=loglist&password={{ loglist_secrets.db_password }}'
      JAVA_OPTS: '-Xmx200m -Xss512k -XX:+UseCompressedOops'
      RECAPTCHA_PRIVATE_KEY: '{{ loglist_secrets.recaptcha.private_key }}'
      RECAPTCHA_PUBLIC_KEY: '{{ loglist_secrets.recaptcha.public_key }}'
      HTTP_SECRET_KEY: '{{ loglist_secrets.http_secret_key }}'
    volumes:
      - '{{ host_config_dir }}/application.conf:/app/conf/application.conf'
    default_host_ip: ''
```

See a full example in the [devops repository][devops].

License
-------

LogList is licensed under the terms of MIT License. See License.md file for
details.

Some third-party components have their own licenses, please consult the
corresponding site section for further details.

[ansible]: https://docs.ansible.com/
[badge.docker]: https://img.shields.io/docker/v/codingteam/loglist?sort=semver
[devops]: https://github.com/codingteam/devops/blob/HEAD/xmpp2/loglist.yml
[docker-hub]: https://hub.docker.com/r/codingteam/loglist
[docs-admin]: docs/Admin.md
[docs-api]: docs/API.md
[docs-loglist.env]: docs/loglist.env
[loglist-original]: http://loglist.ru/
[loglist]: https://www.loglist.xyz/
