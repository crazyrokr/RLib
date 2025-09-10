package javasabr.rlib.plugin.system.exception;

import java.nio.file.Path;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true)
public class InitializePluginException extends PluginException {

  private final Path path;

  public InitializePluginException(String message, Path path) {
    super(message);
    this.path = path;
  }

  public InitializePluginException(String message, Path path, Throwable e) {
    super(message, e);
    this.path = path;
  }
}
