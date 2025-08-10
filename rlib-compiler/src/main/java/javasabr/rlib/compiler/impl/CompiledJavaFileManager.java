package javasabr.rlib.compiler.impl;

import javasabr.rlib.common.util.array.Array;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CompiledJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

  Array<String> classNames;
  CompiledClassesClassLoader classLoader;

  public CompiledJavaFileManager(
      StandardJavaFileManager fileManager,
      CompiledClassesClassLoader classLoader) {
    super(fileManager);
    this.classLoader = classLoader;
    this.classNames = Array.ofType(String.class);
  }

  /**
   * Clear the list of names of loaded classes.
   */
  public void clear() {
    classNames.clear();
  }

  public String[] classNames() {
    return classNames.toArray();
  }

  @Override
  public JavaFileObject getJavaFileForOutput(
      Location location,
      String name,
      Kind kind,
      FileObject sibling) {

    SimpleCompiledClassData byteCode = new SimpleCompiledClassData(name);

    classLoader.registerClassData(byteCode);
    classNames.add(name);

    return byteCode;
  }
}
