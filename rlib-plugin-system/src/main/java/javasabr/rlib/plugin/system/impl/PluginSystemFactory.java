package javasabr.rlib.plugin.system.impl;

import javasabr.rlib.plugin.system.ConfigurablePluginSystem;

/**
 * @author JavaSaBr
 */
public class PluginSystemFactory {

  public static ConfigurablePluginSystem newBasePluginSystem() {
    return new BasePluginSystem();
  }

  public static ConfigurablePluginSystem newBasePluginSystem(ClassLoader classLoader) {
    return new BasePluginSystem(classLoader);
  }
}
