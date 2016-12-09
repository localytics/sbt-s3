package com.localytics.sbt.s3

import java.io.File
import java.net.URL
import java.util.zip.ZipFile

import sbt.Keys.TaskStreams

import scala.sys.process._
import scala.util.Try

object DownloadS3Proxy {

  private[s3] def validJar(file: File): Boolean = Try(new ZipFile(file)).isSuccess

  def apply(ver: String, url: String, dir: File, file: String, streamz: TaskStreams): File = {
    val outputFile = new File(dir, file)
    if (!dir.exists()) {
      streamz.log.info(s"Creating S3Proxy directory $dir")
      dir.mkdirs()
    }
    if (!outputFile.exists()) {
      streamz.log.info(s"Downloading S3Proxy from [$url] to [${outputFile.getAbsolutePath}]")
      (new URL(url) #> outputFile).!!
    }
    if (!validJar(outputFile)) sys.error(s"Invalid jar file at [${outputFile.getAbsolutePath}]")
    outputFile
  }
}
