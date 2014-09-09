CITE Collection Manager
=======================

Overview
--------

This is a server-side Groovlet for proxying instances of the CITE Collection Editor.
It will store an administrator's authorization credentials and proxy Google Fusion Tables
requests on behalf of end-users who don't have direct access to the fusion tables themselves.

Configuration
-------------

* Get a Google Fusion Tables API key, following the instructions in the CITE Collection Editor README
* On the Google API Access page, click the "Download JSON" link for the Client ID
* Copy the resulting `client_secrets.json` file to `src/main/resources/client_secrets.json`
* Copy a built instance of the CITE Collection Editor (i.e. the `build/` directory) to `src/main/webapp/cite-collection-editor` (this should eventually be rolled into a Gradle dependency)
* Create an authorization table in Google Fusion Tables. This is a table with three string columns named `E-Mail`, `Name`, and `Blocked`
* Copy `gradle.properties-dist` to `gradle.properties`, modifying the values to your email address, capabilities URL, and authorization table ID. Set the validTables parameter if you want to restrict proxied access to a set of tables.
* Run `gradle jettyRunWar`
