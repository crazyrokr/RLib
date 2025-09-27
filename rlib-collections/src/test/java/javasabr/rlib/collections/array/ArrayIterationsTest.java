package javasabr.rlib.collections.array;

import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ArrayIterationsTest {

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldFindAnyCorrectly(Array<String> array) {
    // when/then:
    Assertions.assertNull(array
        .iterations()
        .findAny("notexist", Objects::equals));
    Assertions.assertNotNull(array
        .iterations()
        .findAny("Second", Objects::equals));
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldFindAny2Correctly(Array<String> array) {
    // when/then:
    Assertions.assertNull(array
        .iterations()
        .findAny(10, (element, intArg) -> String.valueOf(intArg).equals(element)));
    Assertions.assertNotNull(array
        .iterations()
        .findAny(5, (element, intArg) -> String.valueOf(intArg).equals(element)));
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldDoForEachCorrectly(Array<String> array) {
    // given:
    var result = MutableArray.ofType(String.class);
    var expected = Array.typed(String.class,
        "First_postfix",
        "Second_postfix",
        "Third_postfix",
        "  _postfix",
        "5_postfix");

    // when:
    array.iterations()
        .forEach("_postfix", (element, arg1) -> result.add(element + arg1));

    // then:
    Assertions.assertEquals(expected, result);
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldDoForEach2Correctly(Array<String> array) {
    // given:
    var result = MutableArray.ofType(String.class);
    var expected = Array.typed(String.class,
        "prefix_First_postfix",
        "prefix_Second_postfix",
        "prefix_Third_postfix",
        "prefix_  _postfix",
        "prefix_5_postfix");

    // when:
    array.iterations()
        .forEach("prefix_", "_postfix", (element, arg1, arg2) -> result.add(arg1 + element + arg2));

    // then:
    Assertions.assertEquals(expected, result);
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldDoForEach3Correctly(Array<String> array) {
    // given:
    var result = MutableArray.ofType(String.class);
    var expected = Array.typed(String.class,
        "prefix_First55",
        "prefix_Second55",
        "prefix_Third55",
        "prefix_  55",
        "prefix_555");

    // when:
    array.iterations()
        .forEach("prefix_", 55L, (element, arg1, arg2) -> result.add(arg1 + element + arg2));

    // then:
    Assertions.assertEquals(expected, result);
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldAnyMatchCorrectly(Array<String> array) {
    // when/then:
    Assertions.assertFalse(array
        .iterations()
        .anyMatch(10, (element, intArg) -> String.valueOf(intArg).equals(element)));
    Assertions.assertTrue(array
        .iterations()
        .anyMatch(5, (element, intArg) -> String.valueOf(intArg).equals(element)));
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
