package com.localytics.sbt.s3

object PidUtils {

  private val ProcessIDRegex = """\d+ .*s3proxy""".r

  def extractS3ProxyPid(input: String): Option[String] = ProcessIDRegex.findFirstIn(input).map(_.split(" ")(0))

  def osName: String = System.getProperty("os.name") match {
    case n: String if !n.isEmpty => n
    case _ => System.getProperty("os")
  }

  def killPidCommand(pid: String): String =
    if (osName.toLowerCase.contains("windows")) s"Taskkill /PID $pid /F" else s"kill $pid"

}
