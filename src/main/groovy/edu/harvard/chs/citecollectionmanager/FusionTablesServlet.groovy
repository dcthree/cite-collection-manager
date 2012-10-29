package edu.harvard.chs.citecollectionmanager

import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.fusiontables.Fusiontables
import com.google.api.services.fusiontables.Fusiontables.Builder
import com.google.api.services.fusiontables.model.Table
import com.google.api.services.fusiontables.model.TableList
import com.google.api.services.fusiontables.model.Sqlresponse

import java.io.*
import java.net.URL
import java.util.*
import javax.servlet.*
import javax.servlet.http.*

import edu.harvard.chs.citecollectionmanager.CodeFlow

public class FusionTablesServlet extends HttpServlet {
  /**
   * Handle a servlet GET request
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    Credential credential = CodeFlow.instance.build().loadCredential('administrator')
    String table_id = request.getRequestURL().toString().split("/").last()
    Fusiontables fusiontables = new Fusiontables.Builder(CodeFlow.HTTP_TRANSPORT, CodeFlow.JSON_FACTORY, credential).build()

    Table table = fusiontables.table().get(table_id).execute()

    response.setContentType("text/html;charset=UTF-8")
    response.setStatus(HttpServletResponse.SC_OK)
    PrintWriter out = response.getWriter()

    out.println(table.toPrettyString())
  }
}
