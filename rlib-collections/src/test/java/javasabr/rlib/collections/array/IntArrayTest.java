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
public class IntArrayTest {

  @Test
  void arrayOfTest() {
    // when:
    IntArray array1 = IntArray.of(5);
    IntArray array2 = IntArray.of(5, 8);
    IntArray array3 = IntArray.of(5, 8, 13);
    IntArray array4 = IntArray.of(5, 8, 13, 25);
    IntArray array5 = IntArray.of(5, 8, 13, 25, 33);

    // then:
    assertThat(array1.toArray())
        .isEqualTo(new int[]{5});
    assertThat(array2.toArray())
        .isEqualTo(new int[]{5, 8});
    assertThat(array3.toArray())
        .isEqualTo(new int[]{5, 8, 13});
    assertThat(array4.toArray())
        .isEqualTo(new int[]{5, 8, 13, 25});
    assertThat(array5.toArray())
        .isEqualTo(new int[]{5, 8, 13, 25, 33});
  }

  @Test
  void arrayRepeatedTest() {
    // when:
    IntArray repeated = IntArray.repeated(20, 5);

    // then:
    assertThat(repeated.toArray())
        .isEqualTo(new int[]{20, 20, 20, 20, 20});
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyTakeValues(IntArray array) {

    // then:
    Assertions.assertEquals(4, array.size());
    Assertions.assertEquals(5, array.get(0));
    Assertions.assertEquals(8, array.get(1));
    Assertions.assertEquals(13, array.get(2));
    Assertions.assertEquals(25, array.get(3));
    Assertions.assertEquals(5, array.first());
    Assertions.assertEquals(25, array.last());

    // then:
    Assertions.assertArrayEquals(new int[]{5, 8, 13, 25}, array.stream().toArray());

    // then:
    Assertions.assertTrue(array.contains(8));
    Assertions.assertFalse(array.contains(99));
  }

  @ParameterizedTest
  @MethodSource("generateArraysWithDuplicates")
  void shouldFindElementIndex(IntArray array) {
    // when/then:
    Assertions.assertEquals(0, array.indexOf(5));
    Assertions.assertEquals(3, array.indexOf(25));
    Assertions.assertEquals(-1, array.indexOf(99));
  }

  @ParameterizedTest
  @MethodSource("generateArraysWithDuplicates")
  void shouldFindLastElementIndex(IntArray array) {
    // when/then:
    Assertions.assertEquals(4, array.lastIndexOf(5));
    Assertions.assertEquals(6, array.lastIndexOf(13));
    Assertions.assertEquals(-1, array.lastIndexOf(99));
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyTransformToNativeArray(IntArray array) {
    // when/then:
    Assertions.assertArrayEquals(new int[]{5, 8, 13, 25}, array.stream().toArray());
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyCheckOnContains(IntArray array) {
    // when/then:
    assertThat(array.contains(5)).isTrue();
    assertThat(array.contains(25)).isTrue();
    assertThat(array.contains(99)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyCheckOnContainsAllByArray(IntArray array) {
    // given:
    IntArray check1 = IntArray.of(5);
    IntArray check2 = IntArray.of(8, 13);
    IntArray check3 = IntArray.of(8, 13, 99);
    // when/then:
    assertThat(array.containsAll(check1)).isTrue();
    assertThat(array.containsAll(check2)).isTrue();
    assertThat(array.containsAll(check3)).isFalse();
  }

  @ParameterizedTest
  @MethodSource("generateArrays")
  void shouldCorrectlyCheckOnContainsAllByNativeArray(IntArray array) {
    // given:
    int[] check1 = new int[]{5};
    int[] check2 = new int[]{8, 13};
    int[] check3 = new int[]{8, 13, 99};
    // when/then:
    assertThat(array.containsAll(check1)).isTrue();
    assertThat(array.containsAll(check2)).isTrue();
    assertThat(array.containsAll(check3)).isFalse();
  }

  private static Stream<Arguments> generateArrays() {
    IntArray array = IntArray.of(5, 8, 13, 25);
    MutableIntArray mutableArray = ArrayFactory.mutableIntArray();
    mutableArray.addAll(array);
    return Stream.of(
        Arguments.of(array),
        Arguments.of(mutableArray));
  }

  private static Stream<Arguments> generateArraysWithDuplicates() {
    IntArray array = IntArray.of(5, 8, 13, 25, 5, 8, 13);
    MutableIntArray mutableArray = ArrayFactory.mutableIntArray();
    mutableArray.addAll(array);
    return Stream.of(
        Arguments.of(array),
        Arguments.of(mutableArray));
  }
}
