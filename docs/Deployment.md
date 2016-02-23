LogList Deployment
==================

This page documents our current production environment and process of deploying
LogList to it.

Target environment
------------------

LogList targets Linux Ubuntu 14.04. There should be `loglist` user created on
the target machine, and he should have full access to `/opt/loglist` directory.

Also there should be PostgreSQL database and the `loglist` user should be
granted access to his own database instance.

`loglist.conf` file (see it in the same directory as this document) should be
deployed into `/etc/init` directory. It contains various settings and
credentials; make sure to set them up.

CI environment
--------------

There is a Jenkins CI server set up to automatically deploy `master` branch
after every commit.

It executes the following instructions to rebuild the project:

    sbt clean dist

Make sure that CI have SSH access to target machine as the same `loglist` user
and a permission to execute `sudo /sbin/stop loglist` and `sudo /sbin/start
loglist` commands (and nothing else).

After build it should to the target machine through SSH and execute the
following script:

    sudo /sbin/stop loglist
    cd /opt/loglist
    rm -r bin
    rm -r conf
    rm -r lib
    rm -r share

Then upload `scalajvm/target/universal/loglist-*.zip` file from local CI
directory to `/opt/loglist` directory on the target machine and execute the
following script to start the new LogList version:

    cd /opt/loglist
    unzip *.zip
    mv loglist-jvm-*/* .
    rm -r loglist-jvm-*
    sudo /sbin/start loglist
