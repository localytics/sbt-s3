package com.localytics.sbt.s3

import com.localytics.sbt.s3.S3ProxyKeys._
import sbt.Keys._
import sbt._

object S3ProxyPlugin extends AutoPlugin {

  // auto enable plugin http://www.scala-sbt.org/0.13/docs/Plugins.html#Root+plugins+and+triggered+plugins
  override val trigger = allRequirements

  // inject project keys http://www.scala-sbt.org/0.13/docs/Plugins.html#Controlling+the+import+with+autoImport
  val autoImport = S3ProxyKeys

  // inject project settings http://www.scala-sbt.org/0.13/docs/Plugins.html#projectSettings+and+buildSettings
  override lazy val projectSettings = Seq(
    s3ProxyVersion := "1.5.1",
    s3ProxyDownloadDir := file("s3-proxy"),
    s3ProxyDownloadUrl := s"https://github.com/andrewgaul/s3proxy/releases/download/s3proxy-${s3ProxyVersion.value}/s3proxy",
    s3ProxyDownloadFile := s"s3proxy-${s3ProxyVersion.value}",
    s3ProxyPort := 8001,
    s3ProxyHeapSize := None,
    s3ProxyDataDir := "s3-proxy/data",
    s3ProxyCleanAfterStop := true,
    s3ProxyAuthorization := NoAuth,
    s3ProxyKeyStore := None,

    downloadS3Proxy := DownloadS3Proxy(s3ProxyVersion.value, s3ProxyDownloadUrl.value, s3ProxyDownloadDir.value, s3ProxyDownloadFile.value, streams.value),
    startS3Proxy := StartS3Proxy(s3ProxyDownloadDir.value, s3ProxyDownloadFile.value, s3ProxyPort.value, s3ProxyHeapSize.value, s3ProxyDataDir.value, s3ProxyAuthorization.value, s3ProxyKeyStore.value, streams.value),
    stopS3Proxy := StopS3Proxy(streams.value, s3ProxyDataDir.value, s3ProxyCleanAfterStop.value),
    s3ProxyTestCleanup := Tests.Cleanup(() => StopS3Proxy(streams.value, s3ProxyDataDir.value, s3ProxyCleanAfterStop.value)),
    startS3Proxy := startS3Proxy.dependsOn(downloadS3Proxy).value
  )
}
