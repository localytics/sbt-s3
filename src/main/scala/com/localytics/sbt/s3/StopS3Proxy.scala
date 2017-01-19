package com.localytics.sbt.s3

import sbt.File
import sbt.Keys.TaskStreams
import sbt._

object StopS3Proxy {

  def apply(streamz: TaskStreams, dataDir: String, clean: Boolean) = {
    PidUtils.extractS3ProxyPid("jps -l".!!) match {
      case Some(pid) =>
        streamz.log.info("Stopping S3Proxy")
        PidUtils.killPidCommand(pid).!
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
