package com.localytics.sbt.s3

import scala.util.Try

private[s3] object S3ProxyUtils {

  private val ProcessIDRegex = """\d+ s3proxy""".r

  def extractS3ProxyPid(input: String): Option[String] = ProcessIDRegex.findFirstIn(input).map(_.split(" ")(0))

  def isS3ProxyRunning(port: Int): Boolean = {
    Try {
      val socket = new java.net.Socket("localhost", port)
      socket.close()
    }.isSuccess
  }

  def killPidCommand(pid: String): String = {
    val osName = System.getProperty("os.name") match {
      case n: String if !n.isEmpty => n
      case _ => System.getProperty("os")
    }
    if (osName.toLowerCase.contains("windows")) {
      s"Taskkill /PID $pid /F"
    } else {
      s"kill $pid"
    }
  }

}
