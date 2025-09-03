package javasabr.rlib.plugin.system.exception;

import java.nio.file.Path;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.NullMarked;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true)
public class PreloadPluginException extends PluginException {

  private final Path path;

  public PreloadPluginException(String message, Path path) {
    super(message);
    this.path = path;
  }
}
