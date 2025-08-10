package javasabr.rlib.logger.api;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.ServiceLoader;
import javasabr.rlib.logger.api.impl.NullLoggerFactory;

/**
 * @author JavaSaBr
 */
public class LoggerManager {

  private static final LoggerFactory LOGGER_FACTORY;

  static {

    String className = System.getProperty("com.ss.rlib.logger.factory", "");
    Class<? extends LoggerFactory> implementation = null;

    if (!className.isEmpty()) {
      try {
        implementation = (Class<? extends LoggerFactory>) Class.forName(className);
      } catch (ClassNotFoundException exception) {
        exception.printStackTrace();
      }
    }

    if (implementation == null) {
      Iterator<LoggerFactory> impls = ServiceLoader
          .load(LoggerFactory.class)
          .iterator();
      if (impls.hasNext()) {
        implementation = impls.next().getClass();
      }
    }

    if (implementation == null) {
      System.err.printf("ERROR: No any exist implementation of [%s], will be used null logger%n", LoggerFactory.class);
      LOGGER_FACTORY = new NullLoggerFactory();
    } else {
      try {
        LOGGER_FACTORY = implementation
            .getDeclaredConstructor()
            .newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static Logger getDefaultLogger() {
    return LOGGER_FACTORY.getDefault();
  }

  public static Logger getLogger(Class<?> cs) {
    return LOGGER_FACTORY.make(cs);
  }

  public static Logger getLogger(String id) {
    return LOGGER_FACTORY.make(id);
  }

  public static void addListener(LoggerListener listener) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.addListener(listener);
    }
  }

  public static void removeListener(LoggerListener listener) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.removeListener(listener);
    }
  }

  public static void addWriter(Writer writer) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.addWriter(writer);
    }
  }

  public static void removeWriter(Writer writer) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.removeWriter(writer);
    }
  }

  public static void configureDefault(LoggerLevel level, boolean def) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.configureDefault(level, def);
    }
  }

  public static void removeDefault(LoggerLevel level) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.removeDefault(level);
    }
  }

  public static void enable(Class<?> cs, LoggerLevel level) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.enable(cs, level);
    }
  }

  public static void disable(Class<?> cs, LoggerLevel level) {
    if (LOGGER_FACTORY instanceof LoggerService ls) {
      ls.disable(cs, level);
    }
  }
}
