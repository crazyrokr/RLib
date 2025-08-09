package javasabr.rlib.plugin.system;

import javasabr.rlib.plugin.system.annotation.PluginDescription;

/**
 * @author JavaSaBr
 */
public interface Plugin {

  ClassLoader classLoader();

  default String id() {
    return getClass()
        .getAnnotation(PluginDescription.class)
        .id();
  }

  default Version version() {
    return new Version(getClass()
        .getAnnotation(PluginDescription.class)
        .version());
  }

  default String name() {
    return getClass()
        .getAnnotation(PluginDescription.class)
        .name();
  }

  default String description() {
    return getClass()
        .getAnnotation(PluginDescription.class)
        .description();
  }

  boolean isEmbedded();

  void initialize(PluginSystem pluginSystem);
}
