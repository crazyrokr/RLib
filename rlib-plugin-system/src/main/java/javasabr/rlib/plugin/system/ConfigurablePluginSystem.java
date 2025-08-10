package javasabr.rlib.plugin.system;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
public interface ConfigurablePluginSystem extends PluginSystem {

  void configureInstallationPluginsPath(Path installationPluginsPath);

  void configureEmbeddedPluginPath(Path embeddedPluginPath);

  void configureAppVersion(@Nullable Version version);

  CompletionStage<ConfigurablePluginSystem> preLoad();

  CompletionStage<ConfigurablePluginSystem> preLoad(Executor executor);

  CompletionStage<ConfigurablePluginSystem> initialize();

  CompletionStage<ConfigurablePluginSystem> initialize(Executor executor);

  @Nullable
  Plugin installPlugin(Path file, boolean needInitialize);

  /**
   * @return true if the plugin was removed
   */
  boolean removePlugin(Plugin plugin);
}
