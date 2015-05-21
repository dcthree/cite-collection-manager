CITE Collection Manager
=======================

Overview
--------

This is a server-side Groovlet for proxying instances of the [CITE Collection Editor](https://github.com/ryanfb/cite-collection-editor).
It will store an administrator's authorization credentials and proxy Google Fusion Tables
requests on behalf of end-users who don't have direct access to the fusion tables themselves.

Configuration
-------------

* Get a Google Fusion Tables API key, following the instructions in the [CITE Collection Editor README](https://github.com/ryanfb/cite-collection-editor/blob/master/README.md)
* On the Google API Access page, click the "Download JSON" link for the Client ID
* Copy the resulting `client_secrets.json` file to `src/main/resources/client_secrets.json`
* Copy a built instance of the [CITE Collection Editor](https://github.com/ryanfb/cite-collection-editor) (i.e. the `build/` directory) to `src/main/webapp/cite-collection-editor` (this should eventually be rolled into a Gradle dependency)
* Create an authorization table in Google Fusion Tables. This is a table with three string columns named `E-Mail`, `Name`, and `Blocked`
* Copy `gradle.properties-dist` to `gradle.properties`, modifying the values to your email address, capabilities URL, and authorization table ID. Set the validTables parameter if you want to restrict proxied access to a set of tables.
* Run `gradle jettyRunWar`

Deploying on Google App Engine
------------------------------

Unlike the CITE Collection Editor, the CITE Collection Manager requires some server-side resources and processing. This can typically be managed within the free quotas of [Google's App Engine platform](https://cloud.google.com/appengine/docs) if you don't want to manage a server yourself. The CITE Collection Manager also has code specifically for persisting credentials within the the App Engine environment.

* Create an application at <http://appengine.google.com/>
* Copy `appengine-web.example.xml` to `src/main/webapp/WEB-INF/appengine-web.xml`, editing the `<application>` tag with the name of your App Engine application
* Add the following to "Redirect URIs" for your Google API Credentials:

        http://myapp.appspot.com/oauth2callback
        http://myapp.appspot.com/editor
        https://myapp.appspot.com/oauth2callback
        https://myapp.appspot.com/editor

* Add the following to "JavaScript Origins" for your Google API Credentials:

        http://myapp.appspot.com/
        https://myapp.appspot.com/

* Download/unzip/install the [Google App Engine SDK for Java](https://cloud.google.com/appengine/downloads)
* Run `gradle jettyRunWar` to build/run/test your CITE Collection Manager instance locally
* Run `appcfg.sh update build/tmp/jettyRunWar/webapp` to upload to Google App Engine (see [Google's *Uploading and Managing a Java App* documentation](https://cloud.google.com/appengine/docs/java/tools/uploadinganapp) for details)
* Check <http://myapp.appspot.com/> and the App Engine console for your app
