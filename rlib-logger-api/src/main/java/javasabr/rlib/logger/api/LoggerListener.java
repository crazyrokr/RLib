package javasabr.rlib.logger.api;

/**
 * @author JavaSaBr
 */
public interface LoggerListener {

  void println(String text);

  default void flush() {}
}
