package javasabr.rlib.plugin.system.impl;

import javasabr.rlib.plugin.system.Plugin;
import javasabr.rlib.plugin.system.PluginContainer;
import javasabr.rlib.plugin.system.PluginSystem;
import javasabr.rlib.plugin.system.Version;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BasePlugin implements Plugin {

  PluginContainer container;

  @Override
  public ClassLoader classLoader() {
    return container.classLoader();
  }

  @Override
  public boolean isEmbedded() {
    return container.embedded();
  }

  @Override
  public String id() {
    return container.id();
  }

  @Override
  public Version version() {
    return container.version();
  }

  @Override
  public String description() {
    return container.description();
  }

  @Override
  public String name() {
    return container.name();
  }

  protected PluginContainer container() {
    return container;
  }

  @Override
  public void initialize(PluginSystem pluginSystem) {}
}
