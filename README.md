sbt-s3
===============

Support for running [S3Proxy](https://github.com/andrewgaul/s3proxy) in tests.

[![MIT license](https://img.shields.io/badge/license-MIT%20License-blue.svg)](LICENSE) 

Installation
------------
Add the following to your `project/plugins.sbt` file:

```
addSbtPlugin("com.localytics" % "sbt-s3" % "0.2.0")
```

sbt 0.13.6+ is supported, 0.13.5 should work with the right bintray resolvers

Usage
-----

To use S3Proxy in your project you can call `start-s3-proxy` and `stop-s3-proxy` directly in `sbt`.

Configuration
-------------

To have S3Proxy automatically start and stop around your tests

```
startS3Proxy <<= startS3Proxy.dependsOn(compile in Test)
test in Test <<= (test in Test).dependsOn(startS3Proxy)
test in Test <<= (test in Test, stopS3Proxy) { (test, stop) => test doFinally stop }
```

To download the S3Proxy jar to a specific location ("s3-proxy" is the default)

```
s3ProxyDownloadDir := file("my-dir")
```

To specify a port other than the default `8001`

```
s3ProxyPort := 8081
```

To override the default JVM heap size (specified in MB)

```
s3ProxyHeapSize := Some(1024)
```

To change the directory for the S3Proxy data ("s3-proxy" is the default)

```
s3ProxyDataDir := "some/directory/here"
```

The default on stop is to cleanup any data directory if specified. This can be changed using

```
s3ProxyCleanAfterStop := false
```

Thanks
------

Thanks to [Andrew Gaul](https://github.com/andrewgaul) for the excellent [S3Proxy](https://github.com/andrewgaul/s3proxy) library.
