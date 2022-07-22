package io.avaje.applog.slf4j;

import ch.qos.logback.classic.spi.LoggingEvent;
import io.avaje.applog.AppLog;
import org.junit.jupiter.api.Test;

import java.lang.System.Logger.Level;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;

class Slf4jProviderTest {

  @Test
  void logger() {
    System.Logger log = AppLog.get(Slf4jProvider.class);
    log.log(Level.INFO, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getMessage()).isEqualTo("Hello world");
    assertThat(event.getLoggerName()).isEqualTo(Slf4jProvider.class.getName());
    assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.INFO);

    ResourceBundle barBundle = ResourceBundle.getBundle("io.foo.bar");
    log.log(Level.WARNING, barBundle, "key0", "BundleWorld");

    LoggingEvent eventWithBundle = Slf4jCaptureAppender.lastEvent();
    assertThat(eventWithBundle.getMessage()).isEqualTo("bar bundle hello [BundleWorld]");
    assertThat(eventWithBundle.getLevel()).isEqualTo(ch.qos.logback.classic.Level.WARN);
  }

  @Test
  void loggerWithBundle() {
    ResourceBundle barBundle = ResourceBundle.getBundle("io.foo.bar");
    System.Logger log = AppLog.get("io.withBundle", barBundle);

    log.log(Level.INFO, barBundle, "key0", "LoggerWithBundleWorld");

    LoggingEvent eventWithBundle = Slf4jCaptureAppender.lastEvent();
    assertThat(eventWithBundle.getMessage()).isEqualTo("bar bundle hello [LoggerWithBundleWorld]");
    assertThat(eventWithBundle.getLoggerName()).isEqualTo("io.withBundle");

    ResourceBundle bazzBundle = ResourceBundle.getBundle("io.foo.bazz");
    log.log(Level.INFO, bazzBundle, "key0", "BazzWorld");

    LoggingEvent eventWithBazzBundle = Slf4jCaptureAppender.lastEvent();
    assertThat(eventWithBazzBundle.getMessage()).isEqualTo("bazz bundle hello [BazzWorld]");
  }
}
