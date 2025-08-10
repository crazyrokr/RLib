package javasabr.rlib.common.classpath.impl;

import static java.lang.reflect.Modifier.isAbstract;
import static javasabr.rlib.common.util.ClassUtils.unsafeNNCast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.jar.JarInputStream;
import java.util.zip.ZipException;
import javasabr.rlib.common.classpath.ClassPathScanner;
import javasabr.rlib.common.io.impl.ReuseBytesInputStream;
import javasabr.rlib.common.io.impl.ReuseBytesOutputStream;
import javasabr.rlib.common.util.ArrayUtils;
import javasabr.rlib.common.util.IOUtils;
import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.common.util.array.ArrayFactory;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@Accessors(fluent = true)
@Getter(AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ClassPathScannerImpl implements ClassPathScanner {

  protected static final Logger LOGGER = LoggerManager.getLogger(ClassPathScanner.class);

  private static final String CLASS_PATH = System.getProperty("java.class.path");
  private static final String PATH_SEPARATOR = File.pathSeparator;
  private static final String CLASS_EXTENSION = ".class";
  private static final String MODULE_INFO_CLASS = "module-info.class";
  private static final String META_INF_PREFIX = "META-INF";

  final Array<String> additionalPaths;
  final ClassLoader loader;

  Class<?>[] classes;
  String[] resources;

  @Getter
  boolean useSystemClassPath;

  public ClassPathScannerImpl(ClassLoader classLoader) {
    this.additionalPaths = ArrayFactory.newArray(String.class);
    this.loader = classLoader;
    this.classes = new Class[0];
    this.resources = new String[0];
  }

  @Override
  public void useSystemClassPath(boolean useSystemClassPath) {
    this.useSystemClassPath = useSystemClassPath;
  }

  @Override
  public void addClasses(Array<Class<?>> classes) {
    this.classes = ArrayUtils.combine(
        this.classes,
        classes.toArray(ArrayUtils.EMPTY_CLASS_ARRAY),
        Class.class);
  }

  @Override
  public void addResources(Array<String> resources) {
    this.resources = ArrayUtils.combine(
        this.resources,
        resources.toArray(ArrayUtils.EMPTY_STRING_ARRAY),
        String.class);
  }

  @Override
  public <T> void findImplementationsTo(Array<Class<T>> container, Class<T> interfaceClass) {

    if (!interfaceClass.isInterface()) {
      throw new IllegalArgumentException("Class " + interfaceClass + " is not interface.");
    }

    for (Class<?> klass : classes) {
      if (klass.isInterface() ||
          !interfaceClass.isAssignableFrom(klass) ||
          isAbstract(klass.getModifiers())) {
        continue;
      }
      container.add(unsafeNNCast(klass));
    }
  }

  @Override
  public <T> void findInheritedTo(Array<Class<T>> container, Class<T> parentClass) {

    if (Modifier.isFinal(parentClass.getModifiers())) {
      throw new IllegalArgumentException("Class " + parentClass + " is final class.");
    }

    for (Class<?> klass : classes) {
      if (klass.isInterface() ||
          klass == parentClass ||
          !parentClass.isAssignableFrom(klass) ||
          isAbstract(klass.getModifiers())) {
        continue;
      }
      container.add(unsafeNNCast(klass));
    }
  }

  @Override
  public void findAnnotatedTo(Array<Class<?>> container, Class<? extends Annotation> annotationClass) {
    for (Class<?> klass : classes) {
      if (klass.isInterface() ||
          isAbstract(klass.getModifiers()) ||
          klass.isAnnotation() ||
          !klass.isAnnotationPresent(annotationClass)) {
        continue;
      }
      container.add(klass);
    }
  }

  @Override
  public void foundClassesTo(Array<Class<?>> container) {
    container.addAll(classes);
  }

  @Override
  public void foundResourcesTo(Array<String> container) {
    container.addAll(resources);
  }

  @Override
  public Array<Class<?>> foundClasses() {
    return Array.of(classes);
  }

  @Override
  public Array<String> foundResources() {
    return Array.of(resources);
  }

  protected String[] calculatePathsToScan() {

    var systemClasspath = useSystemClassPath() ? classpathPaths() : ArrayUtils.EMPTY_STRING_ARRAY;
    var capacity = additionalPaths.size() + systemClasspath.length;

    var result = Array.ofType(String.class, capacity);
    result.addAll(systemClasspath);
    result.addAll(additionalPaths);

    return result.toArray(String.class);
  }

  protected String[] classpathPaths() {
    return CLASS_PATH.split(PATH_SEPARATOR);
  }

  private void loadClass(
      @Nullable Path rootPath,
      @Nullable Path file,
      String name,
      Array<Class<?>> container) {

    if (!name.endsWith(CLASS_EXTENSION)) {
      return;
    } else if(MODULE_INFO_CLASS.equals(name)) {
      LOGGER.debug("Skip loading module-info...");
      return;
    } else if(name.startsWith(META_INF_PREFIX)) {
      LOGGER.debug("Skip loading META-INF class...");
      return;
    }

    String className;
    try {

      StringBuilder result = new StringBuilder(name.length() - CLASS_EXTENSION.length());

      for (int i = 0, length = name.length() - CLASS_EXTENSION.length(); i < length; i++) {
        char ch = name.charAt(i);
        if (ch == '/' || ch == '\\') {
          ch = '.';
        }
        result.append(ch);
      }

      className = result.toString();

    } catch (Exception e) {
      LOGGER.warning(name, File.separator, "Incorrect replaced class name:[%s] to java path, used separator:[%s]"::formatted);
      return;
    }

    LOGGER.debug(className, "Try to load class:[%s]"::formatted);
    try {
      container.add(loader().loadClass(className));
    } catch (NoClassDefFoundError error) {
      LOGGER.warning(className, name, rootPath, file,
          "Can't load class:[%s] with original name:[%s], root folder:[%s] and class file:[%s]"::formatted);
      LOGGER.warning(error);
    } catch (Throwable e) {
      LOGGER.warning(e);
    }
  }

  private void scanDirectory(
      Path rootPath,
      Array<Class<?>> classes,
      Array<String> resources,
      Path directory) {
    LOGGER.debug(directory, "Scanning directory:[%s]"::formatted);
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path file : stream) {

        if (Files.isDirectory(file)) {
          scanDirectory(rootPath, classes, resources, file);
          continue;
        }

        String filename = file
            .getFileName()
            .toString();

        if (filename.endsWith(JAR_EXTENSION)) {
          scanJar(classes, resources, file);
        } else if (filename.endsWith(CLASS_EXTENSION)) {

          String path = file
              .subpath(rootPath.getNameCount(), file.getNameCount())
              .toString();

          if (path.startsWith(File.separator)) {
            path = path.substring(1);
          }

          loadClass(rootPath, file, path, classes);
        } else if (!filename.endsWith(SOURCE_EXTENSION)) {

          String path = file
              .subpath(rootPath.getNameCount(), file.getNameCount())
              .toString();

          if (path.startsWith(File.separator)) {
            path = path.substring(1);
          }

          resources.add(path);
        }
      }

    } catch (IOException ex) {
      LOGGER.warning(ex);
    }
  }

  private void scanJar(Array<Class<?>> classes, Array<String> resources, Path jarFile) {
    LOGGER.debug(jarFile, "Scanning jar:[%s]"::formatted);

    if (!Files.exists(jarFile)) {
      LOGGER.warning(jarFile, "Jar file:[%s] does not exists"::formatted);
      return;
    }

    var rout = new ReuseBytesOutputStream();
    var rin = new ReuseBytesInputStream();
    var buffer = new byte[128];

    try (var jin = new JarInputStream(Files.newInputStream(jarFile))) {
      scanJarInputStream(classes, resources, rout, rin, buffer, jin);
    } catch (ZipException e) {
      LOGGER.warning(jarFile, "Can't open zip file:[%s]"::formatted);
      LOGGER.warning(e);
    } catch (IOException e) {
      LOGGER.warning(e);
    }
  }

  private void scanJarInputStream(
      Array<Class<?>> classes,
      Array<String> resources,
      ReuseBytesOutputStream rout,
      ReuseBytesInputStream rin,
      byte[] buffer,
      JarInputStream jin) throws IOException {

    for (var entry = jin.getNextJarEntry(); entry != null; entry = jin.getNextJarEntry()) {

      if (entry.isDirectory()) {
        continue;
      }

      String name = entry.getName();

      if (name.endsWith(JAR_EXTENSION)) {
        rout.reset();
        IOUtils.copy(jin, rout, buffer, false);
        rin.initFor(rout.getData(), 0, rout.size());
        scanJar(classes, resources, rin);
      } else if (name.endsWith(CLASS_EXTENSION)) {
        loadClass(null, null, name, classes);
      } else if (!name.endsWith(SOURCE_EXTENSION)) {
        resources.add(name);
      }
    }
  }

  private void scanJar(Array<Class<?>> classes, Array<String> resources, InputStream jarFile) {
    LOGGER.debug(jarFile, "Scanning jar:[%s]"::formatted);

    var rout = new ReuseBytesOutputStream();
    var rin = new ReuseBytesInputStream();
    var buffer = new byte[128];

    try (var jin = new JarInputStream(jarFile)) {
      scanJarInputStream(classes, resources, rout, rin, buffer, jin);
    } catch (final ZipException e) {
      LOGGER.warning(jarFile, arg -> "Can't open zip file " + arg);
      LOGGER.warning(e);
    } catch (final IOException e) {
      LOGGER.warning(e);
    }
  }

  @Override
  public void scan(@Nullable Predicate<String> filter) {

    var paths = calculatePathsToScan();

    Array<Class<?>> classes = Array.ofType(Class.class);
    Array<String> resources = Array.ofType(String.class);

    for (String path : paths) {
      var file = Path.of(path);
      if (!Files.exists(file) || (filter != null && !filter.test(path))) {
        continue;
      }

      LOGGER.debug(file, "Scanning file:[%s]"::formatted);

      var filename = file
          .getFileName()
          .toString();

      if (Files.isDirectory(file)) {
        scanDirectory(file, classes, resources, file);
      } else if (filename.endsWith(JAR_EXTENSION)) {
        scanJar(classes, resources, file);
      }
    }

    this.classes = classes.toArray(ArrayUtils.EMPTY_CLASS_ARRAY);
    this.resources = resources.toArray(ArrayUtils.EMPTY_STRING_ARRAY);

    LOGGER.debug(
        classes().length,
        resources().length,
        "Scanned [%s] classes and [%s] resources"::formatted);
  }

  @Override
  public void addAdditionalPath(String path) {
    this.additionalPaths.add(path);
  }

  @Override
  public void addAdditionalPaths(String[] paths) {
    this.additionalPaths.addAll(paths);
  }
}
