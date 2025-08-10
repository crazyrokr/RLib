package javasabr.rlib.plugin.system.extension;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author JavaSaBr
 */
public class ExtensionPointTests {

  @Test
  void registerExtensionTest() {

    var point = new ExtensionPoint<String>();
    point.register("a", "b");
    point.register("c");

    Assertions.assertIterableEquals(Arrays.asList("a", "b", "c"), point.extensions());
  }

  @Test
  void registerExtensionPointTest() {

    var manager = new ExtensionPointManager();
    manager.addExtension("Test1", 5);
    manager.addExtension("Test1", 6, 7);

    var test2 = manager.<Integer>create("Test2");
    test2.register(1, 2);
    test2.register(3);

    var forTest1 = manager.<Integer>getOrCreateExtensionPoint("Test1");
    var forTest2 = manager.<Integer>getOrCreateExtensionPoint("Test2");

    Assertions.assertIterableEquals(Arrays.asList(5, 6, 7), forTest1.extensions());
    Assertions.assertIterableEquals(Arrays.asList(1, 2, 3), forTest2.extensions());
  }
}
