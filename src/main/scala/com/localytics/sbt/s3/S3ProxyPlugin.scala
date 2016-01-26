package com.localytics.sbt.s3

import com.localytics.sbt.s3.S3ProxyKeys._
import com.localytics.sbt.s3.S3ProxyTasks._
import sbt._

import scala.concurrent.duration._

object S3ProxyPlugin extends AutoPlugin {

  // auto enable plugin http://www.scala-sbt.org/0.13/docs/Plugins.html#Root+plugins+and+triggered+plugins
  override val trigger = allRequirements

  // inject project keys http://www.scala-sbt.org/0.13/docs/Plugins.html#Controlling+the+import+with+autoImport
  val autoImport = S3ProxyKeys

  // inject project settings http://www.scala-sbt.org/0.13/docs/Plugins.html#projectSettings+and+buildSettings
  override lazy val projectSettings = Seq(
    s3ProxyVersion := "1.4.0",
    s3ProxyDownloadDir := file("s3-proxy"),
    s3ProxyDownloadUrl := s"https://github.com/andrewgaul/s3proxy/releases/download/s3proxy-${s3ProxyVersion.value}/s3proxy",
    s3ProxyDownloadFile := s"s3proxy-${s3ProxyVersion.value}",
    s3ProxyPort := 8001,
    s3ProxyDataDir := "s3-proxy/data",
    s3ProxyCleanAfterStop := true,
    downloadS3Proxy <<= downloadS3ProxyTask,
    startS3Proxy <<= startS3ProxyTask,
    stopS3Proxy <<= stopS3ProxyTask
  )
}
