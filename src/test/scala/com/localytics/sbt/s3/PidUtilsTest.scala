package com.localytics.sbt.s3

import org.scalatest.FunSpec
import org.scalatest.Matchers

class PidUtilsTest extends FunSpec with Matchers {

  describe("PidUtils") {

    it("should extract PID correctly") {
      val jpsOutput =
        """
          |76224 /usr/local/Cellar/sbt/0.13.13/libexec/sbt-launch.jar
          |76656 sun.tools.jps.Jps
          |72451
          |76364 /Users/person/code/repository/aws-mocks/s3/s3proxy-1.5.1
        """.stripMargin
      PidUtils.extractPid(jpsOutput) should equal(Some("76364"))
    }

  }

}
