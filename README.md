# LogList [![BuildStatus](https://travis-ci.org/codingteam/loglist.png?branch=master)](https://travis-ci.org/codingteam/loglist)

[![codingteam/loglist](http://issuestats.com/github/codingteam/loglist/badge/pr?style=flat-square)](http://www.issuestats.com/github/codingteam/loglist) [![codingteam/loglist](http://issuestats.com/github/codingteam/loglist/badge/issue?style=flat-square)](http://www.issuestats.com/github/codingteam/loglist)

Reincarnation of [the famous service](http://www.loglist.net/).

# Development Configuration #

## PostgreSQL ##

Install PostgreSQL, create database and user for LogList. Install and
activate the following extensions:

* pgcrypto

## Environment Variables ##

Possible environment variables can be found in the `devenv.example`
file. Supposed workflow:

    $ cp devenv.example devenv # devenv is gitignored so you won't accidentally commit it 
    $ emacs devenv
      ... Modify variables ...
      ... All variable names should be self-explanatory
    $ . ./devenv
    $ sbt run

The same sample setup for Windows:

    PS> cp devenv.ps1.example devenv.ps1
    PS> notepad devenv.ps1 # Edit the configuration...
    PS> .\devenv.ps1
    PS> sbt run

The quick brown mixer jumps over the lazy fornever.
