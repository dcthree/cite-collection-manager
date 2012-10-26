import org.apache.commons.io.FileUtils

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.MemoryCredentialStore
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jdo.auth.oauth2.JdoCredentialStore
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.oauth2.Oauth2
import com.google.api.services.oauth2.model.Tokeninfo
import com.google.api.services.oauth2.model.Userinfo

import java.io.File
import java.io.IOException
import java.util.Arrays
import java.util.List
import javax.jdo.JDOHelper

class CodeFlow {
  private List<String> scopes = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/fusiontables")
  private MemoryCredentialStore credentialStore = new MemoryCredentialStore()
  public GoogleClientSecrets secrets = null;

  public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport()
  public static final JsonFactory JSON_FACTORY = new JacksonFactory()

  public setSecrets(context) {
    secrets = GoogleClientSecrets.load(JSON_FACTORY, context.getResourceAsStream("client_secrets.json"))
  }
  
  public build() {
    return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, scopes).setCredentialStore(credentialStore).setAccessType("offline").setApprovalPrompt("force").build()
  }

  public authorized() {
    if(secrets == null) {
      return false;
    }
    else {
      return (this.build().loadCredential('administrator') != null)
    }
  }

  private static final INSTANCE = new CodeFlow()
  static synchronized getInstance(){ return INSTANCE }
  private CodeFlow() {}
}
