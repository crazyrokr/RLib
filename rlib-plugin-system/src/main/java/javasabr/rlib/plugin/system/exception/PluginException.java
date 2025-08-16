package javasabr.rlib.plugin.system.exception;

/**
 * The base implementation of a plugin exception.
 *
 * @author JavaSaBr
 */
public class PluginException extends RuntimeException {

  public PluginException(String message) {
    super(message);
  }

  public PluginException(String message, Throwable cause) {
    super(message, cause);
  }
}
