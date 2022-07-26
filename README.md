[![Build](https://github.com/avaje/avaje-applog-slf4j/actions/workflows/build.yml/badge.svg)](https://github.com/avaje/avaje-applog-slf4j/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.avaje/avaje-applog-slf4j.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/io.avaje/avaje-applog-slf4j)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/avaje/avaje-applog-slf4j/blob/master/LICENSE)
[![JDK EA](https://github.com/avaje/avaje-applog-slf4j/actions/workflows/jdk-ea.yml/badge.svg)](https://github.com/avaje/avaje-applog-slf4j/actions/workflows/jdk-ea.yml)

# avaje-applog-slf4j
SLF4J provider for AppLog System.Logger

### Dependency

```xml
<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-applog-slf4j</artifactId>
  <version>1.0</version>
</dependency>
```

### How to use

Add the above dependency into the classpath / module-path.

If using module-path additionally add a requires clause into module-info:

```java
requires io.avaje.applog.slf4j;
```

This means that the System.Logger returned by AppLog.getLogger() uses
`slf4j-api Logger` and all the log events are processed by slf4j-api.
