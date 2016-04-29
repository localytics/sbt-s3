sbt-s3
===============

Support for running [S3Proxy](https://github.com/andrewgaul/s3proxy) in tests.

[![MIT license](https://img.shields.io/badge/license-MIT%20License-blue.svg)](LICENSE) 

Installation
------------
Add the following to your `project/plugins.sbt` file:

```
addSbtPlugin("com.localytics" % "sbt-s3" % "0.4.0")
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
testOptions in Test <+= s3ProxyTestCleanup
```

To set the version of the S3Proxy jar to download ("1.3.0" is the default)

```
s3ProxyVersion := "1.3.0"
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

The authorization type can either be set to `none` in which case all requests are accepted or `aws-v2` in which case
requests must include a key/secret pair matching `s3ProxyIdentity`/`s3ProxyCredential`. `none` is the default.

```
s3ProxyAuthorization := "none"
```

The identity is the AWS key allowed to access the S3Proxy when `s3ProxyAuthorization` is set to `aws-v2`. Defaults to `identity`.

```
s3ProxyIdentity := "identity"
```

The credential is the AWS secret key allowed to access the S3Proxy when `s3ProxyAuthorization` is set to `aws-v2`. Defaults to `credential`.

```
s3ProxyCredential := "credential"
```

Thanks
------

Thanks to [Andrew Gaul](https://github.com/andrewgaul) for the excellent [S3Proxy](https://github.com/andrewgaul/s3proxy) library.
