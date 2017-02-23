package com.localytics.sbt.s3

import java.io.File

import sbt.Keys._
import sbt._

object S3ProxyKeys {
  lazy val s3ProxyVersion = settingKey[String]("S3Proxy version to download. Defaults to 1.5.1.")
  lazy val s3ProxyDownloadUrl = settingKey[String]("URL to download the S3Proxy file from.")
  lazy val s3ProxyDownloadDir = settingKey[File]("The directory the S3Proxy file will be downloaded to. Defaults to s3-proxy.")
  lazy val s3ProxyDownloadFile = settingKey[String]("The name of the S3Proxy file. Defaults to s3-proxy-{version}.")
  lazy val s3ProxyPort = settingKey[Int]("The port number that S3Proxy will use to communicate with your application. Defaults to 8000.")
  lazy val s3ProxyHeapSize = settingKey[Option[Int]]("The size of the heap for S3Proxy. Defaults to the JVM default.")
  lazy val s3ProxyDataDir = settingKey[String]("The directory where S3Proxy will write its data files. Defaults to s3-proxy.")
  lazy val s3ProxyCleanAfterStop = settingKey[Boolean]("Clean the local data directory after S3Proxy shutdown. Defaults to true.")

  sealed trait Authorization
  case object NoAuth extends Authorization
  case class AwsV2Auth(identity: String, credential: String) extends Authorization

  lazy val s3ProxyAuthorization = settingKey[Authorization]("Authorization type for S3Proxy. Defaults to NoAuth.")

  case class KeyStore(path: String, pass: String)

  lazy val s3ProxyKeyStore = settingKey[Option[KeyStore]]("KeyStore information required for HTTPS endpoint. Defaults to None.")

  lazy val downloadS3Proxy = TaskKey[File]("download-s3-proxy")
  lazy val startS3Proxy = TaskKey[String]("start-s3-proxy")
  lazy val stopS3Proxy = TaskKey[Unit]("stop-s3-proxy")
  lazy val s3ProxyTestCleanup = TaskKey[Tests.Cleanup]("s3-proxy-test-cleanup")

  // http://www.scala-sbt.org/0.13/docs/Plugins-Best-Practices.html#Provide+raw+settings+and+configured+settings
  lazy val baseS3ProxySettings = Seq(
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
