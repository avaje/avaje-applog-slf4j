
package io.avaje.applog.slf4j;

import java.util.ResourceBundle;

/**
 * Wraps Slf4jLogger with a provided resource bundle.
 */
final class Slf4jWrapperLogger implements System.Logger {

  private final System.Logger wrapped;
  private final ResourceBundle bundle;

  Slf4jWrapperLogger(System.Logger wrapped, ResourceBundle bundle) {
    this.wrapped = wrapped;
    this.bundle = bundle;
  }

  @Override
  public String getName() {
    return wrapped.getName();
  }

  @Override
  public boolean isLoggable(Level platformLevel) {
    return wrapped.isLoggable(platformLevel);
  }

  @Override
  public void log(Level level, String msg) {
    wrapped.log(level, bundle, msg);
  }

  @Override
  public void log(Level level, Object obj) {
    wrapped.log(level, bundle, obj.toString(), (Object[]) null);
  }

  @Override
  public void log(Level level, String msg, Throwable thrown) {
    wrapped.log(level, bundle, msg, thrown);
  }

  @Override
  public void log(Level level, String format, Object... params) {
    wrapped.log(level, bundle, format, params);
  }

  @Override
  public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
    wrapped.log(level, bundle, msg, thrown);
  }

  @Override
  public void log(Level level, ResourceBundle bundle, String format, Object... params) {
    wrapped.log(level, bundle, format, params);
  }
}
