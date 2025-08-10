package javasabr.rlib.plugin.system;

import java.net.URLClassLoader;
import java.nio.file.Path;
import javasabr.rlib.common.classpath.ClassPathScanner;
import javasabr.rlib.plugin.system.annotation.PluginDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level =  AccessLevel.PRIVATE, makeFinal = true)
public class PluginContainer {

  Class<Plugin> pluginClass;
  URLClassLoader classLoader;
  ClassPathScanner scanner;
  Path path;
  String id;
  String name;
  String description;
  Version version;
  boolean embedded;

  public PluginContainer(
      Class<Plugin> pluginClass,
      URLClassLoader classLoader,
      ClassPathScanner scanner,
      Path path,
      boolean embedded) {
    PluginDescription description = pluginClass.getAnnotation(PluginDescription.class);
    this.pluginClass = pluginClass;
    this.classLoader = classLoader;
    this.scanner = scanner;
    this.path = path;
    this.embedded = embedded;
    this.id = description.id();
    this.name = description.name();
    this.version = new Version(description.version());
    this.description = description.description();
  }

  @Override
  public String toString() {
    return "PluginContainer{" + "pluginClass=" + pluginClass + ", path=" + path + '}';
  }
}
