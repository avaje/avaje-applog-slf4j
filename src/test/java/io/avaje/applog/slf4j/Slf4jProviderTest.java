package io.avaje.applog.slf4j;

import ch.qos.logback.classic.spi.LoggingEvent;
import io.avaje.applog.AppLog;
import org.junit.jupiter.api.Test;

import java.lang.System.Logger.Level;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Slf4jProviderTest {

  @Test
  void logger() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.INFO, "Hello {0}", "world");

    assertThat(log.getName()).isEqualTo(Slf4jProvider.class.getName());

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
    System.Logger log = AppLog.getLogger("io.withBundle", barBundle);

    assertThat(log.getName()).isEqualTo("io.withBundle");
    assertTrue(log.isLoggable(Level.INFO));

    log.log(Level.INFO, barBundle, "key0", "LoggerWithBundleWorld");

    LoggingEvent eventWithBundle = Slf4jCaptureAppender.lastEvent();
    assertThat(eventWithBundle.getMessage()).isEqualTo("bar bundle hello [LoggerWithBundleWorld]");
    assertThat(eventWithBundle.getLoggerName()).isEqualTo("io.withBundle");

    ResourceBundle bazzBundle = ResourceBundle.getBundle("io.foo.bazz");
    log.log(Level.INFO, bazzBundle, "key0", "BazzWorld");

    LoggingEvent eventWithBazzBundle = Slf4jCaptureAppender.lastEvent();
    assertThat(eventWithBazzBundle.getMessage()).isEqualTo("bazz bundle hello [BazzWorld]");
  }

  @Test
  void loggerWithBundle_log() {
    ResourceBundle barBundle = ResourceBundle.getBundle("io.foo.bar");
    System.Logger log = AppLog.getLogger("io.withBundle", barBundle);

    log.log(Level.INFO, "key0");
    assertThat(lastMessage()).isEqualTo("bar bundle hello [{0}]");

    log.log(Level.INFO, () -> "key0");
    assertThat(lastMessage()).isEqualTo("key0");

    log.log(Level.INFO, "key0", "LoggerWithBundleWorld");
    assertThat(lastMessage()).isEqualTo("bar bundle hello [LoggerWithBundleWorld]");

    log.log(Level.INFO, "key0", "LoggerWithBundleWorld");
    assertThat(lastMessage()).isEqualTo("bar bundle hello [LoggerWithBundleWorld]");
  }

  @Test
  void loggerWithBundle_log_object() {
    ResourceBundle barBundle = ResourceBundle.getBundle("io.foo.bar");
    System.Logger log = AppLog.getLogger("io.withBundle", barBundle);

    log.log(Level.INFO, new SomeObject("key0"));
    assertThat(lastMessage()).isEqualTo("bar bundle hello [{0}]");

    log.log(Level.INFO, new SomeObject("hello-world"));
    assertThat(lastMessage()).isEqualTo("hello-world");
  }

  static class SomeObject {
    final String key;

    SomeObject(String key) {
      this.key = key;
    }

    @Override public String toString() {
      return key;
    }
  }
  @Test
  void loggerWithBundle_log_throwable() {
    ResourceBundle barBundle = ResourceBundle.getBundle("io.foo.bar");
    System.Logger log = AppLog.getLogger("io.withBundle", barBundle);
    log.log(Level.DEBUG, "msg", new RuntimeException("I like to throw"));

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getMessage()).isEqualTo("msg");
    assertThat(event.getThrowableProxy().getMessage()).isEqualTo("I like to throw");
  }

  @Test
  void loggerWithBundle_log_rbThrowable() {
    ResourceBundle barBundle = ResourceBundle.getBundle("io.foo.bar");
    System.Logger log = AppLog.getLogger("io.withBundle", barBundle);

    ResourceBundle bazzBundle = ResourceBundle.getBundle("io.foo.bazz");

    log.log(Level.TRACE, bazzBundle,  "msg", new RuntimeException("I like to throw"));

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getMessage()).isEqualTo("msg");
    assertThat(event.getThrowableProxy().getMessage()).isEqualTo("I like to throw");

    log.log(Level.TRACE, bazzBundle,  "key0", new RuntimeException("I like to throw"));

    event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getMessage()).isEqualTo("bazz bundle hello [{0}]");
    assertThat(event.getThrowableProxy().getMessage()).isEqualTo("I like to throw");
  }

  private String lastMessage() {
    return Slf4jCaptureAppender.lastEvent().getMessage();
  }

  @Test
  void loggerWithBundle_missingMessage() {
    ResourceBundle barBundle = ResourceBundle.getBundle("io.foo.bar");
    System.Logger log = AppLog.getLogger("io.withBundle", barBundle);

    log.log(Level.INFO, barBundle, "keyNotFound", "LoggerWithBundleWorld");

    LoggingEvent eventWithBundle = Slf4jCaptureAppender.lastEvent();
    assertThat(eventWithBundle.getMessage()).isEqualTo("keyNotFound");
  }

  @Test
  void withError() {
    System.Logger logger = AppLog.getLogger("my.foo");
    logger.log(Level.INFO, "Name Hello {0}", "world");
    try {
      methodThatThrows();
    } catch (Throwable e) {
      logger.log(Level.ERROR, MessageFormat.format("This error {0}", "MyParam"), e);

      LoggingEvent eventWithBazzBundle = Slf4jCaptureAppender.lastEvent();
      assertThat(eventWithBazzBundle.getMessage()).isEqualTo("This error MyParam");
      assertThat(eventWithBazzBundle.getThrowableProxy().getMessage()).isEqualTo("I like to throw");
    }
  }

  private void methodThatThrows() {
    throw new RuntimeException("I like to throw");
  }

  @Test
  void trace() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.TRACE, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.TRACE);

    assertTrue(log.isLoggable(Level.TRACE));
  }

  @Test
  void debug() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.DEBUG, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.DEBUG);

    assertTrue(log.isLoggable(Level.DEBUG));
  }

  @Test
  void info() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.INFO, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.INFO);

    assertTrue(log.isLoggable(Level.INFO));
  }

  @Test
  void warn() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.WARNING, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.WARN);

    assertTrue(log.isLoggable(Level.WARNING));
  }

  @Test
  void error() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.ERROR, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.ERROR);

    assertTrue(log.isLoggable(Level.ERROR));
  }

  @Test
  void all_expectLevelTrace() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.ALL, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.TRACE);

    assertTrue(log.isLoggable(Level.ALL));
  }

  @Test
  void off_expectNoLogMessage() {
    System.Logger log = AppLog.getLogger(Slf4jProvider.class);
    log.log(Level.OFF, "Hello {0}", "world");

    LoggingEvent event = Slf4jCaptureAppender.lastEvent();
    assertThat(event).isNull();

    assertFalse(log.isLoggable(Level.OFF));
  }
}
