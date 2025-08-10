package javasabr.rlib.logger.api;

import java.io.Writer;

public interface LoggerService {

  int NOT_CONFIGURE = -1;
  int DISABLED = 0;
  int ENABLED = 1;

  void addListener(LoggerListener listener);

  void removeListener(LoggerListener listener);

  void addWriter(Writer writer);

  void removeWriter(Writer writer);

  void enable(Class<?> cs, LoggerLevel level);

  void disable(Class<?> cs, LoggerLevel level);

  void configureDefault(LoggerLevel level, boolean def);

  void removeDefault(LoggerLevel level);

  /**
   * @return {@link #NOT_CONFIGURE} or {@link #ENABLED} or {@link #DISABLED}
   */
  int enabled(LoggerLevel level);

  void write(LoggerLevel level, String loggerName, String logMessage);
}
