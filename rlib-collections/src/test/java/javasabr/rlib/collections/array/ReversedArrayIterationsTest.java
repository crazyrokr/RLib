package javasabr.rlib.collections.array;

import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReversedArrayIterationsTest {

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldFindAnyCorrectly(Array<String> array) {
    // when/then:
    Assertions.assertNull(array
        .reversedIterations()
        .findAny("notexist", Objects::equals));
    Assertions.assertNotNull(array
        .iterations()
        .findAny("Second", Objects::equals));
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldDoForEachCorrectly(Array<String> array) {
    // given:
    var result = MutableArray.ofType(String.class);
    var expected = Array.typed(String.class,
        "prefix_First",
        "prefix_Second",
        "prefix_Third",
        "prefix_  ",
        "prefix_5");

    // when:
    array.reversedIterations()
        .forEach("prefix_", (arg1, element) -> result.add(arg1 + element));

    // then:
    Assertions.assertEquals(expected, result);
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldDoForEach2Correctly(Array<String> array) {
    // given:
    var result = MutableArray.ofType(String.class);
    var expected = Array.typed(String.class,
        "prefix__middle_First",
        "prefix__middle_Second",
        "prefix__middle_Third",
        "prefix__middle_  ",
        "prefix__middle_5");

    // when:
    array.reversedIterations()
        .forEach("prefix_", "_middle_", (arg1, arg2, element) -> result.add(arg1 + arg2 + element));

    // then:
    Assertions.assertEquals(expected, result);
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldAnyMatchCorrectly(Array<String> array) {
    // when/then:
    Assertions.assertFalse(array
        .reversedIterations()
        .anyMatch("10", String::equals));
    Assertions.assertTrue(array
        .reversedIterations()
        .anyMatch("5", String::equals));
  }

  private static Stream<Arguments> generateArrays() {
    Array<String> array = Array.typed(String.class, "First", "Second", "Third", "  ", "5");
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
