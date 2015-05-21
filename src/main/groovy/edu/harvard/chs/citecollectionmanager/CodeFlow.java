package edu.harvard.chs.citecollectionmanager;

import org.apache.commons.io.FileUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.jdo.JDOHelper;

class CodeFlow {
  private static final Logger log = Logger.getLogger( CodeFlow.class.getName() );
  public static List<String> scopes = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/fusiontables");
  public GoogleClientSecrets secrets = null;

  public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  public static final JsonFactory JSON_FACTORY = new JacksonFactory();

  public void setSecrets(ClassLoader context) throws IOException {
    secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(context.getResourceAsStream("client_secrets.json")));
  }

  private static String getFileCredentialStorePath() throws IOException {
    Properties prop = new Properties();
    InputStream input_stream = CodeFlow.class.getResourceAsStream("build.properties");
    prop.load(input_stream);
    input_stream.close();
    return prop.getProperty("fileCredentialStore");
  }
  
  public GoogleAuthorizationCodeFlow build() {
    try {
      String appEngineEnvironment = System.getProperty("com.google.appengine.runtime.environment");
      if (appEngineEnvironment == null) {
        if (getFileCredentialStorePath().isEmpty()) {
          log.finer("Not running on appEngine, using MemoryDataStore");
          DataStore<StoredCredential> credentialStore = MemoryDataStoreFactory.getDefaultInstance().getDataStore("CodeFlow");
          return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, scopes).setCredentialDataStore(credentialStore).setAccessType("offline").setApprovalPrompt("force").build();
        }
        else {
          log.finer("Not running on appEngine and fileCredentialStore is set, using FileCredentialStore");
          DataStore<StoredCredential> credentialStore = StoredCredential.getDefaultDataStore(new FileDataStoreFactory(new File(getFileCredentialStorePath())));
          return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, scopes).setCredentialDataStore(credentialStore).setAccessType("offline").setApprovalPrompt("force").build();
        }
      }
      else {
        log.finer("Running on appEngine, using AppEngineDataStore");
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, scopes).setDataStoreFactory(AppEngineDataStoreFactory.getDefaultInstance()).setAccessType("offline").setApprovalPrompt("force").build();
      }
    }
    catch(IOException e) {
      log.severe(e.getMessage());
      return null;
    }
  }

  public boolean authorized() throws IOException {
    if(secrets == null) {
      return false;
    }
    else {
      return (this.build().loadCredential("administrator") != null);
    }
  }

  private static final CodeFlow INSTANCE = new CodeFlow();
  static synchronized CodeFlow getInstance(){ return INSTANCE; }
  private CodeFlow() {}
}
