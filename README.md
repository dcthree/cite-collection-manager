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
* Copy the resulting `client_secrets.json` file to `src/main/resources/edu/harvard/chs/citecollectionmanager/client_secrets.json`
* Copy a built instance of the [CITE Collection Editor](https://github.com/ryanfb/cite-collection-editor) (i.e. the `build/` directory) to `src/main/webapp/cite-collection-editor` (this should eventually be rolled into a Gradle dependency)
* Create an authorization table in Google Fusion Tables. This is a table with three text columns named `E-Mail`, `Name`, and `Blocked`
* Copy `gradle.properties-dist` to `gradle.properties`, modifying the values to your email address, capabilities URL, and authorization table ID. Set the validTables parameter if you want to restrict proxied access to a set of tables. The "Administrator" email address must have "edit" permissions on the authorization Fusion Table and all Fusion Tables you wish to proxy. When not running on App Engine, setting a path (e.g. `/tmp/cite-collection-manager`) for `fileCredentialStore` will use a file-backed credential store for the offline credential, instead of a memory-backed store, which should allow the credential to persist between multiple runs of the server.
* Run `gradle jettyRunWar`

Deploying on Google App Engine
------------------------------

Unlike the CITE Collection Editor, the CITE Collection Manager requires some server-side resources and processing. This can typically be managed within the free quotas of [Google's App Engine platform](https://cloud.google.com/appengine/docs) if you don't want to manage a server yourself. The CITE Collection Manager also has code specifically for persisting credentials within the the App Engine environment.

* First, follow the general configuration steps above
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

## Deploying with the Google App Engine Gradle Plugin

* Download/install the [Google Cloud SDK](https://cloud.google.com/sdk/docs), then do the following one-time setup:
  * `gcloud components install app-engine-java`
  * `gcloud config set project myapp` where `myapp` is replaced with what you'll use for e.g. `myapp.appspot.com`
  * `gcloud auth login`
* Deploy with `gradle appengineDeploy`

## Deploying with the Google App Engine SDK for Java

* Download/unzip/install the [Google App Engine SDK for Java](https://cloud.google.com/appengine/downloads)
* Run `gradle jettyRunWar` to build/run/test your CITE Collection Manager instance locally
* Run `gradle explodedWar` to build an exploded WAR
* Run `appcfg.sh --oauth2 update build/exploded` to upload to Google App Engine (see [Google's *Uploading and Managing a Java App* documentation](https://cloud.google.com/appengine/docs/java/tools/uploadinganapp) for details)
* Check <http://myapp.appspot.com/> and the App Engine console for your app

## Using a Service Account

The CITE Collection Manager has support for using [an App Engine Service Account](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) to do the Fusion Tables request proxying. This enables you to spin up new instances of the service without having to manually log in in order for the server to store your credential. To use this feature, go to [the Service accounts page for your App Engine project](https://console.developers.google.com/permissions/serviceaccounts) and create a key for your App Engine default service account. When prompted, select JSON instead of P12, and copy the resulting JSON file download to `src/main/resources/edu/harvard/chs/citecollectionmanager/service_account.json`. You'll also need to share editing permissions on your Fusion Tables (including any authorization tables) with the email address for the service account, e.g. `myapp@appspot.gserviceaccount.com`. If a `service_account.json` file is present, the CITE Collection Manager will try to use it instead of any interactive authentication.
