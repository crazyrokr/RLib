package javasabr.rlib.compiler.impl;

import javasabr.rlib.compiler.CompiledClassData;
import javasabr.rlib.common.util.Utils;
import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CompiledClassesClassLoader extends ClassLoader {

  private static final Logger LOGGER = LoggerManager.getLogger(CompiledClassesClassLoader.class);
  
  Array<CompiledClassData> compiledClassData;

  public CompiledClassesClassLoader() {
    this.compiledClassData = Array.ofType(CompiledClassData.class);
  }

  /**
   * Add a new compiled class.
   */
  public synchronized void registerClassData(CompiledClassData classData) {
    LOGGER.debug(classData.name(), "Register compiled class data:[%s]"::formatted);
    compiledClassData.add(classData);
  }

  @Override
  protected synchronized @Nullable Class<?> findClass(String className) {

    if (compiledClassData.isEmpty()) {
      return null;
    }

    LOGGER.debug(className, "Try to find compiled class data for class:[%s]"::formatted);

    for (CompiledClassData classData : this.compiledClassData) {
      if (className.equals(classData.name())) {
        LOGGER.debug(classData.size(), className, "Loading bytecode:[%s] for class:[%s]..."::formatted);
        byte[] content = classData.byteCode();
        return Utils.uncheckedGet(() -> defineClass(className, content, 0, content.length));
      }
    }

    return null;
  }
}
