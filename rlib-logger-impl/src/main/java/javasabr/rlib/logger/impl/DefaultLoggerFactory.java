package javasabr.rlib.logger.impl;

import static javasabr.rlib.common.util.ObjectUtils.notNull;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.common.util.array.ArrayFactory;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerFactory;
import javasabr.rlib.logger.api.LoggerLevel;
import javasabr.rlib.logger.api.LoggerListener;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * The class for managing loggers.
 *
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class DefaultLoggerFactory implements LoggerFactory {

  ConcurrentMap<String, Logger> loggers;
  Array<LoggerListener> listeners;
  Array<Writer> writers;

  Logger logger;
  DateTimeFormatter timeFormatter;

  public DefaultLoggerFactory() {
    this.loggers = new ConcurrentHashMap<>();
    this.logger = new DefaultLogger("", this);
    this.timeFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy HH:mm:ss:SSS");
    this.listeners = ArrayFactory.newCopyOnModifyArray(LoggerListener.class);
    this.writers = ArrayFactory.newCopyOnModifyArray(Writer.class);
  }

  @Override
  public void addListener(LoggerListener listener) {
    listeners.add(listener);
  }

  @Override
  public void addWriter(Writer writer) {
    writers.add(writer);
  }

  @Override
  public Logger getDefault() {
    return logger;
  }

  @Override
  public Logger make(Class<?> type) {
    String simpleName = type.getSimpleName();
    Logger logger = loggers
        .computeIfAbsent(simpleName, name -> new DefaultLogger(name, this));
    return notNull(logger);
  }

  @Override
  public Logger make(String name) {
    Logger logger = loggers
        .computeIfAbsent(name, str -> new DefaultLogger(str, this));
    return notNull(logger);
  }

  @Override
  public void removeListener(LoggerListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void removeWriter(Writer writer) {
    writers.remove(writer);
  }

  void write(LoggerLevel level, String name, String message) {

    var timeStamp = timeFormatter.format(LocalDateTime.now());
    var result = level.title() + level.offset() + ' ' + timeStamp + ' ' + name + ": " + message;

    write(level, result);
  }

  private void write(LoggerLevel level, String resultMessage) {

    listeners.forEachR(resultMessage, LoggerListener::println);
    writers.forEachR(resultMessage, DefaultLoggerFactory::append);

    switch (level) {
      case INFO, DEBUG -> System.out.println(resultMessage);
      case ERROR, WARNING -> System.err.println(resultMessage);
    }

    if (!level.forceFlush()) {
      return;
    }

    listeners.forEach(LoggerListener::flush);
    writers.forEach(DefaultLoggerFactory::flush);
  }

  private static void append(Writer writer, String toWrite) {
    try {
      writer.append(toWrite);
      writer.append('\n');
    } catch (IOException exception) {
      //noinspection CallToPrintStackTrace
      exception.printStackTrace();
    }
  }

  private static void flush(Writer writer) {
    try {
      writer.flush();
    } catch (IOException exception) {
      //noinspection CallToPrintStackTrace
      exception.printStackTrace();
    }
  }
}
