package javasabr.rlib.collections.array;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyTakeValues(Array<String> array) {

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

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldFindElementIndex(Array<String> array) {
    // when/then:
    Assertions.assertEquals(0, array.indexOf("First"));
    Assertions.assertEquals(3, array.indexOf("  "));
    Assertions.assertEquals(-1, array.indexOf("notexist"));
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldFindElementIndexWithFunction(Array<String> array) {
    // when/then:
    Assertions.assertEquals(0, array.indexOf(s -> s + s, "FirstFirst"));
    Assertions.assertEquals(3, array.indexOf(s -> s + s, "    "));
    Assertions.assertEquals(-1, array.indexOf("notexist"));
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyTransformToNativeArray(Array<String> array) {
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

  private static Stream<Arguments> generateArrays() {
    Array<String> array = Array.of("First", "Second", "Third", "  ");
    MutableArray<String> mutableArray = ArrayFactory.mutableArray(String.class);
    mutableArray.addAll(array);
    MutableArray<String> copyOnModifyArray = ArrayFactory.copyOnModifyArray(String.class);
    copyOnModifyArray.addAll(array);
    LockableArray<String> stampedLockBasedArray = ArrayFactory.stampedLockBasedArray(String.class);
    stampedLockBasedArray.addAll(array);
    return Stream.of(
        Arguments.of(array),
        Arguments.of(mutableArray),
        Arguments.of(copyOnModifyArray),
        Arguments.of(stampedLockBasedArray));
  }
}
