package javasabr.rlib.collections.array;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author JavaSaBr
 */
public class LongArrayTest {

  @Test
  void arrayOfTest() {
    // when:
    LongArray array1 = LongArray.of(5);
    LongArray array2 = LongArray.of(5, 8);
    LongArray array3 = LongArray.of(5, 8, 13);
    LongArray array4 = LongArray.of(5, 8, 13, 25);
    LongArray array5 = LongArray.of(5, 8, 13, 25, 33);

    // then:
    assertThat(array1.toArray())
        .isEqualTo(new long[]{5});
    assertThat(array2.toArray())
        .isEqualTo(new long[]{5, 8});
    assertThat(array3.toArray())
        .isEqualTo(new long[]{5, 8, 13});
    assertThat(array4.toArray())
        .isEqualTo(new long[]{5, 8, 13, 25});
    assertThat(array5.toArray())
        .isEqualTo(new long[]{5, 8, 13, 25, 33});
  }

  @Test
  void arrayRepeatedTest() {
    // when:
    LongArray repeated = LongArray.repeated(20, 5);

    // then:
    assertThat(repeated.toArray())
        .isEqualTo(new long[]{20, 20, 20, 20, 20});
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyTakeValues(LongArray array) {

    // then:
    Assertions.assertEquals(4, array.size());
    Assertions.assertEquals(5, array.get(0));
    Assertions.assertEquals(8, array.get(1));
    Assertions.assertEquals(13, array.get(2));
    Assertions.assertEquals(25, array.get(3));
    Assertions.assertEquals(5, array.first());
    Assertions.assertEquals(25, array.last());

    // then:
    Assertions.assertArrayEquals(new long[]{5, 8, 13, 25}, array.stream().toArray());

    // then:
    Assertions.assertTrue(array.contains(8));
    Assertions.assertFalse(array.contains(99));
  }

  @ParameterizedTest
  @MethodSource("generateArraysWithDuplicates")
  void shouldFindElementIndex(LongArray array) {
    // when/then:
    Assertions.assertEquals(0, array.indexOf(5));
    Assertions.assertEquals(3, array.indexOf(25));
    Assertions.assertEquals(-1, array.indexOf(99));
  }

  @ParameterizedTest
  @MethodSource("generateArraysWithDuplicates")
  void shouldFindLastElementIndex(LongArray array) {
    // when/then:
    Assertions.assertEquals(4, array.lastIndexOf(5));
    Assertions.assertEquals(6, array.lastIndexOf(13));
    Assertions.assertEquals(-1, array.lastIndexOf(99));
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyTransformToNativeArray(LongArray array) {
    // when/then:
    Assertions.assertArrayEquals(new long[]{5, 8, 13, 25}, array.stream().toArray());
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyCheckOnContains(LongArray array) {
    // when/then:
    assertThat(array.contains(5)).isTrue();
    assertThat(array.contains(25)).isTrue();
    assertThat(array.contains(99)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyCheckOnContainsAllByArray(LongArray array) {
    // given:
    LongArray check1 = LongArray.of(5);
    LongArray check2 = LongArray.of(8, 13);
    LongArray check3 = LongArray.of(8, 13, 99);
    // when/then:
    assertThat(array.containsAll(check1)).isTrue();
    assertThat(array.containsAll(check2)).isTrue();
    assertThat(array.containsAll(check3)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyCheckOnContainsAllByNativeArray(LongArray array) {
    // given:
    long[] check1 = new long[]{5};
    long[] check2 = new long[]{8, 13};
    long[] check3 = new long[]{8, 13, 99};
    // when/then:
    assertThat(array.containsAll(check1)).isTrue();
    assertThat(array.containsAll(check2)).isTrue();
    assertThat(array.containsAll(check3)).isFalse();
  }

  private static Stream<Arguments> generateArrays() {
    LongArray array = LongArray.of(5, 8, 13, 25);
    MutableLongArray mutableArray = ArrayFactory.mutableLongArray();
    mutableArray.addAll(array);
    return Stream.of(
        Arguments.of(array),
        Arguments.of(mutableArray));
  }

  private static Stream<Arguments> generateArraysWithDuplicates() {
    LongArray array = LongArray.of(5, 8, 13, 25, 5, 8, 13);
    MutableLongArray mutableArray = ArrayFactory.mutableLongArray();
    mutableArray.addAll(array);
    return Stream.of(
        Arguments.of(array),
        Arguments.of(mutableArray));
  }
}
