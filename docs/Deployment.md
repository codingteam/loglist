LogList Deployment
==================

This page documents our current production environment and process of deploying
LogList to it.

Target environment
------------------

LogList targets Linux Ubuntu 16.04. There should be `loglist` user created on
the target machine, and he should have full access to `/opt/loglist` directory.

Also there should be PostgreSQL database and the `loglist` user should be
granted access to his own database instance.

`loglist.service` file (see it in the same directory as this document) should be
copied to `/etc/systemd/system` directory; `loglist.conf` should be copied to
`/etc/loglist`. The latter file contains various settings and credentials; make
sure to set them up.

CI environment
--------------

There is a Jenkins CI server set up to deploy `master` branch after any
maintainer decides it's time to release.

Build is operated by [Jenkins pipeline plugin][jenkins-pipeline-plugin]. Make
sure it's installed and all dependencies are met. Consult
`scripts/Jenkinsfile.deploy` and set up all the build parameters on your server.

The deployment is performed over SSH, so make sure that CI have SSH access to
the target machine as the same `loglist` user and a permission to execute
`sudo /usr/bin/systemctl start loglist` and
`sudo /usr/bin/systemctl stop loglist` commands (and nothing else).

[jenkins-pipeline-plugin]: https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Plugin
