package services

import java.io.{PrintWriter, StringWriter}

object ExceptionUtil {
  def getExceptionString(e: Exception): String = {
    val sw = new StringWriter()
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}
