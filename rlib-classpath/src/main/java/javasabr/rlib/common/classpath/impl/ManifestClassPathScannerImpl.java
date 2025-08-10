package javasabr.rlib.common.classpath.impl;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javasabr.rlib.common.util.Utils;
import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.common.util.array.ArrayFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ManifestClassPathScannerImpl extends ClassPathScannerImpl {

  Class<?> rootClass;
  String classPathKey;

  public ManifestClassPathScannerImpl(
      ClassLoader classLoader,
      Class<?> rootClass,
      String classPathKey) {
    super(classLoader);
    this.rootClass = rootClass;
    this.classPathKey = classPathKey;
  }

  protected String[] calculateManifestClassPath() {

    var result = Array.ofType(String.class);
    var currentThread = Thread.currentThread();

    Path root = Utils.getRootFolderFromClass(rootClass);
    ClassLoader loader = currentThread.getContextClassLoader();
    Enumeration<URL> urls = Utils.uncheckedGet(loader, arg -> arg.getResources(JarFile.MANIFEST_NAME));

    while (urls.hasMoreElements()) {

      try {

        URL url = urls.nextElement();
        InputStream is = url.openStream();
        if (is == null) {
          LOGGER.warning(url, arg -> "not found input stream for the url " + arg);
          continue;
        }

        var manifest = new Manifest(is);

        Attributes attributes = manifest.getMainAttributes();
        String value = attributes.getValue(classPathKey);
        if (value == null) {
          continue;
        }

        String[] classpath = value.split(" ");

        for (String path : classpath) {
          Path file = root.resolve(path);
          if (Files.exists(file)) {
            result.add(file.toString());
          }
        }

      } catch (Exception exc) {
        LOGGER.warning(exc);
      }
    }

    return result.toArray(String.class);
  }

  @Override
  protected String[] calculatePathsToScan() {

    var result = ArrayFactory.newArraySet(String.class);
    result.addAll(super.calculatePathsToScan());
    result.addAll(calculateManifestClassPath());

    return result.toArray(String.class);
  }
}
