package javasabr.rlib.common.classpath;

import javasabr.rlib.common.classpath.impl.ClassPathScannerImpl;
import javasabr.rlib.common.classpath.impl.ManifestClassPathScannerImpl;

/**
 * @author JavaSaBr
 */
public final class ClassPathScannerFactory {

  public static ClassPathScanner newDefaultScanner() {
    return new ClassPathScannerImpl(ClassPathScannerFactory.class.getClassLoader());
  }

  public static ClassPathScanner newDefaultScanner(ClassLoader classLoader) {
    return new ClassPathScannerImpl(classLoader);
  }

  public static ClassPathScanner newDefaultScanner(
      ClassLoader classLoader,
      String[] additionalPaths) {
    var scanner = new ClassPathScannerImpl(classLoader);
    scanner.addAdditionalPaths(additionalPaths);
    return scanner;
  }

  public static ClassPathScanner newManifestScanner(Class<?> rootClass) {
    return new ManifestClassPathScannerImpl(
        ClassPathScannerFactory.class.getClassLoader(),
        rootClass,
        "Class-Path");
  }

  public static ClassPathScanner newManifestScanner(Class<?> rootClass, String classPathKey) {
    return new ManifestClassPathScannerImpl(
        ClassPathScannerFactory.class.getClassLoader(),
        rootClass,
        classPathKey);
  }

  private ClassPathScannerFactory() {
    throw new RuntimeException();
  }
}
