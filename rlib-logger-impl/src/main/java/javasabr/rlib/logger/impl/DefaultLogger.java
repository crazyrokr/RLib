package javasabr.rlib.logger.impl;

import java.util.Objects;
import javasabr.rlib.common.util.StringUtils;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerLevel;
import org.jspecify.annotations.Nullable;

/**
 * The base implementation of the logger.
 *
 * @author JavaSaBr
 */
public final class DefaultLogger implements Logger {

  private static final LoggerLevel[] VALUES = LoggerLevel.values();

  /**
   * The table of override enabled statuses.
   */
  private final @Nullable Boolean[] override;

  /**
   * The logger name.
   */
  private final String name;

  /**
   * The default logger factory.
   */
  private final DefaultLoggerFactory loggerFactory;

  public DefaultLogger(String name, DefaultLoggerFactory loggerFactory) {
    this.name = name;
    this.loggerFactory = loggerFactory;
    this.override = new Boolean[VALUES.length];
  }

  @Override
  public boolean enabled(LoggerLevel level) {
    var value = override[level.ordinal()];
    return Objects.requireNonNullElse(value, level.isEnabled());
  }

  @Override
  public boolean overrideEnabled(LoggerLevel level, boolean enabled) {
    override[level.ordinal()] = enabled;
    return true;
  }

  @Override
  public boolean resetToDefault(LoggerLevel level) {
    override[level.ordinal()] = null;
    return true;
  }

  @Override
  public void print(LoggerLevel level, String message) {
    if (enabled(level)) {
      loggerFactory.write(level, name, message);
    }
  }

  @Override
  public void print(LoggerLevel level, Throwable exception) {
    if (enabled(level)) {
      loggerFactory.write(level, name, StringUtils.toString(exception));
    }
  }
}
