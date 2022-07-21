package io.avaje.applog.slf4j;

import io.avaje.applog.AppLog;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provides SLF4J Logger as AppLog.Provider.
 */
public final class Slf4jProvider implements AppLog.Provider {

  private final ConcurrentMap<String, Slf4jLogger> loggerCache = new ConcurrentHashMap<>();

  private System.Logger logger(String loggerName) {
    return loggerCache.computeIfAbsent(loggerName, s -> new Slf4jLogger(LoggerFactory.getLogger(loggerName)));
  }

  @Override
  public System.Logger getLogger(String name) {
    return logger(name);
  }

  @Override
  public System.Logger getLogger(String name, ResourceBundle bundle) {
    return new Slf4jWrapperLogger(logger(name), bundle);
  }
}
