package io.avaje.applog.slf4j;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

public class Slf4jCaptureAppender<E> extends ConsoleAppender<E> {

  static LoggingEvent lastEvent;

  @Override
  protected void append(E eventObject) {
    lastEvent = (LoggingEvent) eventObject;
    super.append(eventObject);
  }

  static LoggingEvent lastEvent() {
    var temp = lastEvent;
    lastEvent = null;
    return temp;
  }
}
