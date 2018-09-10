# Setting up the Environment on Stockport Council Windows Lenovo Thinkpad Laptops

---

## Setting Up Proxy

In environmets variable set http_proxy to  `http://172.16.0.126:8080` and https_proxy to `http://172.16.0.126:8080` 

---

## Installing Docker for windows 10

1. To Go into BIOS press 'ESC' and 'Enter' got to the Security and enable Virtualizatiom
2. Save and exit
3. Boot into windows
5. Open Web Browser
6. Got to [Docker Website (https://docs.docker.com/docker-for-windows/release-notes/#stable-releases-of-2017)]
7. Download Docker Community Edition 17.09.0-ce-win33 2017-10-06
8. Install
9. Opensettings and set https and http proxy to `http://172.16.0.126:8080` setting bypass to `.stockport.gov.uk,.sock,localhost,127.0.0.1,::1,192.168.59.103`

---

## Installing Clojure

1. Make sure you have recent version of Java SDK installed. This requires Java 1.8
2. 

----

## Installing IntelliJ and Cursive

1. Go to [The Jet Brains Website (https://www.jetbrains.com/idea/download/#section=windows)] and download the Community Edition.
2. Got to the [Cursive Website  (https://cursive-ide.com/)] get a Non-Commercial License and download the Plugin
3. Open Settings and install

---

#Cloning Signposts

1. Go to [the git hub page (https://github.com/smbc-digital/signposts) https://github.com/smbc-digital/signposts] copy the ssh url [(git@github.com:smbc-digital/signposts.git)git@github.com:smbc-digital/signposts.git] and clone in your git client. e.g
`git clone git@github.com:smbc-digital/signposts.git` 

----

## Installing Sqlite

This is required to log search history for audting and debug purpose and retrieve recent searches for the welcome page.

1. Go to [Sqlite Web Page (https://www.sqlite.org/index.html)] and download the windows 64 bit edition
2. Go to [DB Browser for SQLite(https://sqlitebrowser.org/)] and download the windows 64 bit edition
3. Open the database browser and create a database visualis.db an run the following script: 
  `CREATE TABLE QueryLog( QueryID INTEGER PRIMARY KEY autoincrement, User text, Query text, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP )`
4. Save database and copy to `db\visualise.db` in the Visualise folder

---