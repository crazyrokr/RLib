package javasabr.rlib.collections.array;

import javasabr.rlib.common.util.array.ArrayFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author JavaSaBr
 */
public class ArrayTest  {

  @Test
  void arrayOfTest() {

    // when:
    Array<String> array = Array.of("First", "Second", "Third", "  ");

    // then:
    Assertions.assertEquals(4, array.size());
    Assertions.assertEquals("First", array.get(0));
    Assertions.assertEquals("Second", array.get(1));
    Assertions.assertEquals("Third", array.get(2));
    Assertions.assertEquals("  ", array.get(3));
    Assertions.assertEquals("First", array.first());
    Assertions.assertEquals("  ", array.last());

    // then:
    Assertions.assertArrayEquals(
        new String[]{
            "First",
            "Second",
            "Third",
            "  "
        }, array.stream().toArray());

    // then:
    Assertions.assertTrue(array.contains("Second"));
    Assertions.assertFalse(array.contains("test"));
  }

  @Test
  void toArrayTest() {

    // when:
    Array<String> array = Array.of("First", "Second", "Third", "  ");

    // then:
    Assertions.assertArrayEquals(
        new String[]{
            "First",
            "Second",
            "Third",
            "  "
        }, array.toArray());
    Assertions.assertArrayEquals(
        new String[]{
            "First",
            "Second",
            "Third",
            "  "
        }, array.toArray(new String[4]));
    Assertions.assertArrayEquals(
        new String[]{
            "First",
            "Second",
            "Third",
            "  "
        }, array.toArray(String.class));
  }
}
