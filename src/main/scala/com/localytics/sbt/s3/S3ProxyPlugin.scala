package com.localytics.sbt.s3

import sbt._

object S3ProxyPlugin extends AutoPlugin {

  // auto enable plugin http://www.scala-sbt.org/0.13/docs/Plugins.html#Root+plugins+and+triggered+plugins
  override val trigger = allRequirements

  // inject project keys http://www.scala-sbt.org/0.13/docs/Plugins.html#Controlling+the+import+with+autoImport
  val autoImport = S3ProxyKeys

  // inject project settings http://www.scala-sbt.org/0.13/docs/Plugins.html#projectSettings+and+buildSettings
  override lazy val projectSettings =  S3ProxyKeys.baseS3ProxySettings
}
