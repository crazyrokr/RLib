package javasabr.rlib.logger.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum LoggerLevel {
  INFO("INFO", "   ", false, true),
  DEBUG("DEBUG", "  ", false, false),
  WARNING("WARNING", "", true, true),
  ERROR("ERROR", "  ", true, true);

  public static final int LENGTH = values().length;

  String title;
  String offset;

  boolean enabled;
  boolean forceFlush;

  @Override
  public String toString() {
    return title;
  }
}
