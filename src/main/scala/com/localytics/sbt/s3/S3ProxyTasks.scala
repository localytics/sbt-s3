package com.localytics.sbt.s3

import java.io.File
import java.net.URL

import com.localytics.sbt.s3.S3ProxyKeys._
import com.localytics.sbt.s3.S3ProxyUtils._
import sbt.Keys._
import sbt._

object S3ProxyTasks {

  def downloadS3ProxyTask = (s3ProxyVersion, s3ProxyDownloadUrl, s3ProxyDownloadDir, s3ProxyDownloadFile, streams) map {
    case (ver, url, downloadDir, downloadFile, streamz) =>
      import sys.process._
      val outputFile = new File(downloadDir, downloadFile)
      if (!downloadDir.exists()) {
        streamz.log.info(s"Creating S3Proxy directory $downloadDir")
        downloadDir.mkdirs()
      }
      if (!outputFile.exists()) {
        streamz.log.info(s"Downloading S3Proxy from [$url] to [${outputFile.getAbsolutePath}]")
        (new URL(url) #> outputFile).!!
      }
      if (!outputFile.exists()) {
        sys.error(s"Cannot find S3Proxy file at [${outputFile.getAbsolutePath}]")
      }
  }

  def startS3ProxyTask = (downloadS3Proxy, s3ProxyDownloadDir, s3ProxyDownloadFile, s3ProxyPort,
    s3ProxyHeapSize, s3ProxyDataDir, s3ProxyAuthorization, s3ProxyKeyStore, streams) map {
    case (_, downloadDir, downloadFile, port, heapSize, dataDir, authorization, keyStoreOpt, streamz) =>

      val httpsArgs = keyStoreOpt.map { ks =>
        Seq(
          s"-Ds3proxy.keystore-path=${ks.path}",
          s"-Ds3proxy.keystore-password=${ks.pass}",
          s"-Ds3proxy.secure-endpoint=http://127.0.0.1:$port"
        )
      }

      val authArgs = authorization match {
        case NoAuth =>
          Seq("-Ds3proxy.authorization=none")
        case AwsV2Auth(identity, credential) =>
          Seq(
            "-Ds3proxy.authorization=aws-v2",
            s"-Ds3proxy.identity=$identity",
            s"-Ds3proxy.credential=$credential"
          )
      }

      val args = Seq("java") ++
        heapSize.map(mb => Seq(s"-Xms${mb}m", s"-Xmx${mb}m")).getOrElse(Nil) ++
        Seq("-Djclouds.provider=filesystem") ++
        Seq(s"-Djclouds.filesystem.basedir=$dataDir") ++
        Seq(s"-Ds3proxy.endpoint=http://127.0.0.1:$port") ++
        httpsArgs.getOrElse(Nil) ++
        authArgs ++
        Seq("-jar", new File(downloadDir, downloadFile).getAbsolutePath) ++
        Seq("--properties", "/dev/null")

      if (isS3ProxyRunning(port)) {
        streamz.log.warn(s"S3Proxy is already running on port $port")
      } else {
        streamz.log.info("Starting S3Proxy:")
        Process(args).run()
        do {
          streamz.log.info(s"Waiting for S3Proxy to boot on port $port")
          Thread.sleep(500)
        } while (!isS3ProxyRunning(port))
      }
      if (extractS3ProxyPid("jps".!!).isEmpty) {
        sys.error(s"Cannot find S3Proxy PID")
      }
  }

  def stopS3ProxyTask = (streams, s3ProxyDataDir, s3ProxyCleanAfterStop) map {
    case (streamz, dataDir, clean) => stopS3ProxyHelper(streamz, dataDir, clean)
  }


  def s3ProxyTestCleanupTask = (streams, s3ProxyDataDir, s3ProxyCleanAfterStop) map {
    case (streamz, dataDir, clean) => Tests.Cleanup(() => stopS3ProxyHelper(streamz, dataDir, clean))
  }

  def stopS3ProxyHelper(streamz: Keys.TaskStreams, dataDir: String, clean: Boolean) = {
    extractS3ProxyPid("jps".!!) match {
      case Some(pid) =>
        streamz.log.info("Stopping S3Proxy")
        killPidCommand(pid).!
      case None =>
        streamz.log.warn("Cannot find S3Proxy PID")
    }
    if (clean) {
      streamz.log.info("Cleaning S3Proxy")
      val dir = new File(dataDir)
      if (dir.exists()) sbt.IO.delete(dir)
    }
  }

}
