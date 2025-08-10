package javasabr.rlib.logger.slf4j;

import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Slf4jLogger implements Logger {

  private final org.slf4j.Logger logger;

  @Override
  public boolean enabled(LoggerLevel level) {
    return switch (level) {
      case INFO -> logger.isInfoEnabled();
      case DEBUG -> logger.isDebugEnabled();
      case ERROR -> logger.isErrorEnabled();
      case WARNING -> logger.isWarnEnabled();
    };
  }

  @Override
  public void print(LoggerLevel level, String message) {
    switch (level) {
      case INFO -> logger.info(message);
      case DEBUG -> logger.debug(message);
      case ERROR -> logger.error(message);
      case WARNING -> logger.warn(message);
    }
  }

  @Override
  public void print(LoggerLevel level, Throwable exception) {
    switch (level) {
      case INFO -> logger.info(exception.getMessage(), exception);
      case DEBUG -> logger.debug(exception.getMessage(), exception);
      case ERROR -> logger.error(exception.getMessage(), exception);
      case WARNING -> logger.warn(exception.getMessage(), exception);
    }
  }
}
