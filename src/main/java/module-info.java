module io.avaje.applog.slf4j {

  exports io.avaje.applog.slf4j;

  requires transitive io.avaje.applog;
  requires transitive org.slf4j;

  provides io.avaje.applog.AppLog.Provider with io.avaje.applog.slf4j.Slf4jProvider;
}
