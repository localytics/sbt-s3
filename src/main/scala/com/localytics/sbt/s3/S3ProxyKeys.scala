package com.localytics.sbt.s3

import java.io.File

import sbt._

import scala.concurrent.duration._

object S3ProxyKeys {
  lazy val s3ProxyVersion = settingKey[String]("S3Proxy version to download. Defaults to 1.3.0.")
  lazy val s3ProxyDownloadUrl = settingKey[String]("URL to download the S3Proxy file from.")
  lazy val s3ProxyDownloadDir = settingKey[File]("The directory the S3Proxy file will be downloaded to. Defaults to s3-proxy.")
  lazy val s3ProxyDownloadFile = settingKey[String]("The name of the S3Proxy file. Defaults to s3-proxy-{version}.")
  lazy val s3ProxyPort = settingKey[Int]("The port number that S3Proxy will use to communicate with your application. Defaults to 8000.")
  lazy val s3ProxyHeapSize = settingKey[Option[Int]]("The size of the heap for S3Proxy. Defaults to the JVM default.")
  lazy val s3ProxyDataDir = settingKey[String]("The directory where S3Proxy will write its data files. Defaults to s3-proxy.")
  lazy val s3ProxyCleanAfterStop = settingKey[Boolean]("Clean the local data directory after S3Proxy shutdown. Defaults to true.")

  lazy val s3ProxyAuthorization = settingKey[String]("Authorization type for S3Proxy. Allowed values are 'none' and 'aws-v2'. Defaults to none.")
  lazy val s3ProxyIdentity = settingKey[String]("The AWS Key for the allowed user. Only relevant when Authorization is not 'none'. Defaults to identity.")
  lazy val s3ProxyCredential = settingKey[String]("The AWS Secret Key for the allowed user. Only relevant when Authorization is not 'none'. Defaults to credential.")

  lazy val downloadS3Proxy = TaskKey[Unit]("download-s3-proxy")
  lazy val startS3Proxy = TaskKey[Unit]("start-s3-proxy")
  lazy val stopS3Proxy = TaskKey[Unit]("stop-s3-proxy")
  lazy val s3ProxyTestCleanup = TaskKey[Tests.Cleanup]("s3-proxy-test-cleanup")
}
