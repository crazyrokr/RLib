package javasabr.rlib.classpath;

import java.util.Collection;
import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.common.util.array.impl.AbstractArray;
import javasabr.rlib.logger.api.LoggerLevel;
import javasabr.rlib.logger.api.LoggerManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author JavaSaBr
 */
public class ClasspathScannerTests {

  static {
    LoggerManager.configureDefault(LoggerLevel.DEBUG, true);
  }

  @Test
  void testSystemClasspathScanner() {

    ClassPathScanner scanner = ClassPathScannerFactory.newDefaultScanner();
    scanner.useSystemClassPath(true);
    scanner.scan();

    Array<Class<Collection>> implementations = scanner.findImplementations(Collection.class);

    Assertions.assertFalse(implementations.isEmpty());

    Array<Class<AbstractArray>> inherited = scanner.findInherited(AbstractArray.class);

    Assertions.assertFalse(inherited.isEmpty());
  }
}
