package com.localytics.sbt.s3

import java.io.File

import com.localytics.sbt.s3.S3ProxyKeys.Authorization
import com.localytics.sbt.s3.S3ProxyKeys.AwsV2Auth
import com.localytics.sbt.s3.S3ProxyKeys.KeyStore
import com.localytics.sbt.s3.S3ProxyKeys.NoAuth
import sbt.Keys.TaskStreams
import sbt.Process
import sbt._

import scala.util.Try

object StartS3Proxy {
  def apply(jarDir: File, jarFile: String, port: Int, heapSize: Option[Int], dataDir: String, auth: Authorization, keyStoreOpt: Option[KeyStore], streamz: TaskStreams): String = {

    val args = Seq("java") ++
      heapSize.map(mb => Seq(s"-Xms${mb}m", s"-Xmx${mb}m")).getOrElse(Nil) ++
      Seq("-Djclouds.provider=filesystem") ++
      Seq(s"-Djclouds.filesystem.basedir=$dataDir") ++
      Seq(s"-Ds3proxy.endpoint=http://127.0.0.1:$port") ++
      httpsArgs(port, keyStoreOpt).getOrElse(Nil) ++
      authArgs(auth) ++
      Seq("-jar", new File(jarDir, jarFile).getAbsolutePath) ++
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
    PidUtils.extractS3ProxyPid("jps -l".!!).getOrElse {
      sys.error(s"Cannot find S3Proxy PID")
    }
  }

  private def httpsArgs(port: Int, keyStoreOpt: Option[KeyStore]): Option[Seq[String]] =
    keyStoreOpt.map { ks =>
      Seq(
        s"-Ds3proxy.keystore-path=${ks.path}",
        s"-Ds3proxy.keystore-password=${ks.pass}",
        s"-Ds3proxy.secure-endpoint=http://127.0.0.1:$port"
      )
    }

  private def authArgs(auth: Authorization): Seq[String] =
    auth match {
      case NoAuth =>
        Seq("-Ds3proxy.authorization=none")
      case AwsV2Auth(identity, credential) =>
        Seq(
          "-Ds3proxy.authorization=aws-v2",
          s"-Ds3proxy.identity=$identity",
          s"-Ds3proxy.credential=$credential"
        )
    }

  private def isS3ProxyRunning(port: Int): Boolean = Try(new java.net.Socket("localhost", port).close()).isSuccess

}
