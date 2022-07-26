package io.avaje.applog.slf4j;

import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

/**
 * Adapts SLF4J 1.7 Logger to Java System.Logger.
 */
final class Slf4jLogger implements System.Logger {

  private final Logger slf4jLogger;

  Slf4jLogger(Logger logger) {
    this.slf4jLogger = requireNonNull(logger);
  }

  @Override
  public String getName() {
    return slf4jLogger.getName();
  }

  @Override
  public boolean isLoggable(Level platformLevel) {
    if (platformLevel == Level.ALL) {
      return true;
    }
    if (platformLevel == Level.OFF) {
      return false;
    }
    return isEnabledForLevel(toSLF4JLevel(platformLevel));
  }

  /**
   * Returns whether this Logger is enabled for a given {@link Level}.
   */
  private boolean isEnabledForLevel(org.slf4j.event.Level level) {
    switch (level) {
      case TRACE:
        return slf4jLogger.isTraceEnabled();
      case DEBUG:
        return slf4jLogger.isDebugEnabled();
      case INFO:
        return slf4jLogger.isInfoEnabled();
      case WARN:
        return slf4jLogger.isWarnEnabled();
      case ERROR:
        return slf4jLogger.isErrorEnabled();
      default:
        throw new IllegalArgumentException("Level [" + level + "] not recognized.");
    }
  }

  /**
   * Transform a {@link Level} to {@link org.slf4j.event.Level}.
   * <p>
   * This method assumes that Level.ALL or Level.OFF never reach this method.
   */
  private org.slf4j.event.Level toSLF4JLevel(Level platformLevel) {
    switch (platformLevel) {
      case TRACE:
        return org.slf4j.event.Level.TRACE;
      case DEBUG:
        return org.slf4j.event.Level.DEBUG;
      case INFO:
        return org.slf4j.event.Level.INFO;
      case WARNING:
        return org.slf4j.event.Level.WARN;
      case ERROR:
        return org.slf4j.event.Level.ERROR;
      default:
        reportUnknownLevel(platformLevel);
        return org.slf4j.event.Level.TRACE;
    }
  }

  @Override
  public void log(Level platformLevel, ResourceBundle bundle, String msg, Throwable thrown) {
    log(platformLevel, bundle, msg, thrown, (Object[]) null);
  }

  @Override
  public void log(Level platformLevel, ResourceBundle bundle, String format, Object... params) {
    log(platformLevel, bundle, format, null, params);
  }

  /**
   * Single point of processing taking all possible parameters.
   */
  private void log(Level platformLevel, ResourceBundle bundle, String msg, Throwable thrown, Object... params) {
    if (platformLevel == Level.OFF) {
      return;
    }
    if (platformLevel == Level.ALL) {
      performLog(org.slf4j.event.Level.TRACE, bundle, msg, thrown, params);
      return;
    }

    performLog(toSLF4JLevel(platformLevel), bundle, msg, thrown, params);
  }

  private void performLog(org.slf4j.event.Level slf4jLevel, ResourceBundle bundle, String msg, Throwable thrown, Object... params) {
    switch (slf4jLevel) {
      case ERROR: {
        if (slf4jLogger.isErrorEnabled()) {
          slf4jLogger.error(message(bundle, msg, params), thrown);
        }
        break;
      }
      case WARN: {
        if (slf4jLogger.isWarnEnabled()) {
          slf4jLogger.warn(message(bundle, msg, params), thrown);
        }
        break;
      }
      case INFO: {
        if (slf4jLogger.isInfoEnabled()) {
          slf4jLogger.info(message(bundle, msg, params), thrown);
        }
        break;
      }
      case DEBUG: {
        if (slf4jLogger.isDebugEnabled()) {
          slf4jLogger.debug(message(bundle, msg, params), thrown);
        }
        break;
      }
      case TRACE: {
        if (slf4jLogger.isTraceEnabled()) {
          slf4jLogger.trace(message(bundle, msg, params), thrown);
        }
        break;
      }
    }
  }

  private String message(ResourceBundle bundle, String msg, Object[] params) {
    String message = resourceStringOrMessage(bundle, msg);
    if (params != null && params.length > 0) {
      message = MessageFormat.format(message, params);
    }
    return message;
  }

  private void reportUnknownLevel(Level platformLevel) {
    IllegalArgumentException iae = new IllegalArgumentException("Unknown log level [" + platformLevel + "]");
    org.slf4j.helpers.Util.report("Unsupported log level", iae);
  }

  private static String resourceStringOrMessage(ResourceBundle bundle, String msg) {
    if (bundle == null || msg == null) {
      return msg;
    }
    try {
      return bundle.getString(msg);
    } catch (Throwable ex) {
      // handle all errors to avoid log-related exceptions from crashing the JVM.
      return msg;
    }
  }

}
