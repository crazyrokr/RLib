package javasabr.rlib.common.classpath;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Predicate;
import javasabr.rlib.common.classpath.impl.ClassPathScannerImpl;
import javasabr.rlib.common.util.array.Array;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
public interface ClassPathScanner {

  String JAR_EXTENSION = ".jar";
  String SOURCE_EXTENSION = ".java";
  String CLASS_EXTENSION = ".class";

  @Nullable ClassPathScanner NULL_SCANNER = null;

  ClassPathScanner EMPTY_SCANNER = new ClassPathScannerImpl(ClassPathScanner.class.getClassLoader()) {

    @Override
    public void addClasses(Array<Class<?>> classes) {}

    @Override
    public void addAdditionalPath(String path) {}

    @Override
    public void addAdditionalPaths(String[] paths) {}

    @Override
    public void addResources(Array<String> resources) {}

    @Override
    public void scan(Predicate<String> filter) {}
  };

  URLClassLoader EMPTY_CLASS_LOADER = new URLClassLoader(new URL[0], ClassPathScanner.class.getClassLoader());

  @Nullable URLClassLoader NULL_CLASS_LOADER = null;

  void addClasses(Array<Class<?>> classes);

  void addResources(Array<String> resources);

  void useSystemClassPath(boolean useSystemClasspath);

  default <T> Array<Class<T>> findImplementations(Class<T> interfaceClass) {
    Array<Class<T>> result = Array.ofType(Class.class);
    findImplementationsTo(result, interfaceClass);
    return result;
  }

  <T> void findImplementationsTo(Array<Class<T>> container, Class<T> interfaceClass);

  default <T> Array<Class<T>> findInherited(Class<T> parentClass) {
    Array<Class<T>> result = Array.ofType(Class.class);
    findInheritedTo(result, parentClass);
    return result;
  }

  <T> void findInheritedTo(Array<Class<T>> container, Class<T> parentClass);

  default Array<Class<?>> findAnnotated(Class<? extends Annotation> annotationClass) {
    Array<Class<?>> result = Array.ofType(Class.class);
    findAnnotatedTo(result, annotationClass);
    return result;
  }

  void findAnnotatedTo(Array<Class<?>> container, Class<? extends Annotation> annotationClass);

  void foundClassesTo(Array<Class<?>> container);

  void foundResourcesTo(Array<String> container);

  Array<Class<?>> foundClasses();

  Array<String> foundResources();

  default void scan() {
    scan(null);
  }

  void scan(@Nullable Predicate<String> filter);

  void addAdditionalPath(String path);

  void addAdditionalPaths(String[] paths);
}
