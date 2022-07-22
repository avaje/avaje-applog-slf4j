/**
 * Add avaje-applog-slf4j as a dependency and it will adapt AppLog System.Logger
 * to use slf4j-api.
 * <p>
 * Adding this as a dependency allows {@link io.avaje.applog.slf4j.Slf4jProvider} to
 * be registered with AppLog via ServiceLoader. This means the System.Logger implementations
 * returned by AppLog will use slf4j-api.
 */
package io.avaje.applog.slf4j;
