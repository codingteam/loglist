# LogList [![BuildStatus](https://travis-ci.org/codingteam/loglist.png?branch=master)](https://travis-ci.org/codingteam/loglist)

Reincarnation of [the famous service](http://loglist.herokuapp.com/).

# Development Configuration #

First of all, you need to set up PostgreSQL, create database and user
for LogList. Then set up required environment variables to configure
the application. Possible environment variables can be found in the
`devenv.example` file. Supposed workflow:

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
