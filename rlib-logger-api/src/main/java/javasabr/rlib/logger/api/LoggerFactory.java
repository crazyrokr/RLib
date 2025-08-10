package javasabr.rlib.logger.api;

import java.io.Writer;

public interface LoggerFactory {

  Logger make(String name);

  Logger make(Class<?> type);

  Logger getDefault();

  void addListener(LoggerListener listener);

  void removeListener(LoggerListener listener);

  void addWriter(Writer writer);

  void removeWriter(Writer writer);
}
