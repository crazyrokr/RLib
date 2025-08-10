package javasabr.rlib.logger.impl;

import java.util.Objects;
import javasabr.rlib.common.util.StringUtils;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerLevel;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public final class DefaultLogger implements Logger {

  private static final LoggerLevel[] VALUES = LoggerLevel.values();

  @Nullable Boolean[] override;
  String name;
  DefaultLoggerFactory loggerFactory;

  public DefaultLogger(String name, DefaultLoggerFactory loggerFactory) {
    this.name = name;
    this.loggerFactory = loggerFactory;
    this.override = new Boolean[VALUES.length];
  }

  @Override
  public boolean enabled(LoggerLevel level) {
    var value = override[level.ordinal()];
    return Objects.requireNonNullElse(value, level.enabled());
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
