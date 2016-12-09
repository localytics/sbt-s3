package com.localytics.sbt.s3

import java.io.File

import org.scalatest.FunSpec
import org.scalatest.Matchers

class DownloadS3ProxyTest extends FunSpec with Matchers {

  describe("DownloadS3Proxy") {

    it("should identify a valid jar") {
      DownloadS3Proxy.validJar(new File(getClass.getResource("/valid.jar").getFile)) should be(true)
    }

    it("should identify an invalid jar") {
      DownloadS3Proxy.validJar(new File(getClass.getResource("/invalid.jar").getFile)) should be(false)
    }

  }

}
