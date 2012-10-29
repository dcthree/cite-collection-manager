package edu.harvard.chs.citecollectionmanager

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
    CodeFlow.instance.build()
    response.setContentType("text/html;charset=UTF-8")
    response.setStatus(HttpServletResponse.SC_OK)
    PrintWriter out = response.getWriter()

    out.println("ok")
  }
}
