package javasabr.rlib.plugin.system;

import java.util.Optional;
import javasabr.rlib.common.util.array.Array;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
public interface PluginSystem {

  Array<PluginContainer> pluginContainers();

  @Nullable
  PluginContainer getPluginContainer(String id);

  default Optional<PluginContainer> getPluginContainerOptional(String id) {
    return Optional.ofNullable(getPluginContainer(id));
  }

  Array<Plugin> plugins();

  @Nullable
  Plugin getPlugin(String id);

  default Optional<Plugin> getPluginOptional(String id) {
    return Optional.ofNullable(getPlugin(id));
  }
}
