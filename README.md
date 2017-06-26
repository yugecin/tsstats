# tsstats
Java bot that sits on your teamspeak3 server 24/7 to collect data about your clients every 5 minutes in order to generate fancy stats :)

See [yugecin/tsstats-webviewer](https://github.com/yugecin/tsstats-webviewer) for a web based viewer of these stats.

### Examples/Demo
* [https://tsstats.thisisgaming.org/](https://tsstats.thisisgaming.org/) (since 24/08/2014)
* [http://exp-gaming.net/teamspeak/](http://exp-gaming.net/teamspeak/) (since 01/05/2017)
* *do you use this? let me know, and I'll put a link here if you want*

Notes
-----
* This was made in the summer of 2014, and most parts have been left untouched since then. Expect weird code styles.
* On some systems, this likes to crash. On my raspberry pi it crashes sometimes, so I let it restart automatically using a cronjob every 6 hours. On a friend's server, it crashed constantly. On my vps, the last restart was in september so it has been running for 7 months now. See for yourself what you need. (By crashing I mean the client times out, but the process stays active. Haven't really looked into that.)

Requirements
------------
* Teamspeak server query login with some permissions < TODO (make sure it gets passed the flood/spam filter)
* Some java runtime
* MySQL/MariaDB

Installation
------------
* Get a database and import `tsstats.sql`
* Copy `tsstats.sample.ini` to `tsstats.ini` and edit everything as needed
* For the db user, it only needs `SELECT,INSERT,UPDATE,DELETE` privileges, but `ALTER` might be useful if some structures ever change (probably not)
* Start the bot (`nohup java -jar tsstats.jar&` on a unix system)
* Optionally: make a cron job to restart the bot (see notes)

Getting query login details
---------------------------
* Connect to you ts server
* (Make sure you have enough permissions)
* Tools > ServerQuery Login
* Enter a username (this will be your queryuser)
* A password will be generated (this will be your querypass)

Getting the bot to show in your client
--------------------------------------
* If you toolbar is hidden, right-click the menu and click 'Toolbar'
* Right-click the toolbar and click 'Customize Toolbar'
* Find 'Toggle ServerQueryClients' in the left list and select it
* Press the arrow pointed to the right to add that action to the toolbar and close the dialog
* Press the 'Toggle ServerQueryClients' button (the blue S) to unhide server query clients.

PSA
---
Your server query login is you! If you demote a query user that uses your credentials (i.e. the statsbot), **then you are demoting yourself**! Don't mess with a query user's servergroup.

License
-------
[GPL-3.0](/LICENSE)

