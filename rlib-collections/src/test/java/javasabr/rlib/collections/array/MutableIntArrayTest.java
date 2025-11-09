package javasabr.rlib.collections.array;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MutableIntArrayTest {

  @ParameterizedTest
  @MethodSource("generateMutableArrays")
  @DisplayName("should correctly add elements")
  void shouldCorrectlyAddElements(MutableIntArray mutableArray) {
    // when:
    mutableArray.add(10);

    // then:
    Assertions.assertEquals(1, mutableArray.size());
    Assertions.assertEquals(10, mutableArray.get(0));

    // when:
    mutableArray.add(20);

    // then:
    Assertions.assertEquals(2, mutableArray.size());
    Assertions.assertEquals(20, mutableArray.get(1));

    // when:
    mutableArray.add(30);

    // then:
    Assertions.assertEquals(3, mutableArray.size());
    Assertions.assertEquals(30, mutableArray.get(2));
    Assertions.assertEquals(IntArray.of(10, 20, 30), mutableArray);

    // when:
    for(int i = 0; i < 100; i++) {
      mutableArray.add(100 + i);
    }

    // then:
    Assertions.assertEquals(103, mutableArray.size());
    Assertions.assertEquals(199, mutableArray.get(102));
  }

  @ParameterizedTest
  @MethodSource("generateMutableArrays")
  @DisplayName("should correctly remove elements")
  void shouldCorrectlyRemoveElements(MutableIntArray mutableArray) {
    // given:
    for(int i = 0; i < 101; i++) {
      mutableArray.add(100 + i);
    }

    // when:
    mutableArray.remove(100);

    // then:
    Assertions.assertEquals(100, mutableArray.size());
    Assertions.assertEquals(101, mutableArray.get(0));

    // when:
    mutableArray.remove(110);

    // then:
    Assertions.assertEquals(99, mutableArray.size());
    Assertions.assertEquals(101, mutableArray.get(0));
    Assertions.assertEquals(109, mutableArray.get(8));
    Assertions.assertEquals(111, mutableArray.get(9));

    // when:
    mutableArray.remove(200);

    // then:
    Assertions.assertEquals(98, mutableArray.size());
    Assertions.assertEquals(199, mutableArray.get(97));
    Assertions.assertEquals(198, mutableArray.get(96));
  }

  @ParameterizedTest
  @MethodSource("generateMutableArrays")
  @DisplayName("should correctly replace elements")
  void shouldCorrectlyReplaceElements(MutableIntArray mutableArray) {
    // given:
    for(int i = 0; i < 101; i++) {
      mutableArray.add(100 + i);
    }

    // when:
    mutableArray.replace(0, 200);

    // then:
    Assertions.assertEquals(101, mutableArray.size());
    Assertions.assertEquals(200, mutableArray.get(0));

    // when:
    mutableArray.replace(11, 211);

    // then:
    Assertions.assertEquals(101, mutableArray.size());
    Assertions.assertEquals(211, mutableArray.get(11));
  }

  @ParameterizedTest
  @MethodSource("generateMutableArrays")
  @DisplayName("should correctly add batch elements")
  void shouldCorrectlyAddBatchElements(MutableIntArray mutableArray) {
    // given:
    for(int i = 0; i < 21; i++) {
      mutableArray.add(100 + i);
    }

    var anotherArray = IntArray.of(31, 32, 33, 34, 35, 36);
    var anotherNativeArray = new int[] {41, 42, 43, 44, 45, 46};

    // when:
    mutableArray.addAll(anotherArray);

    // then:
    Assertions.assertEquals(27, mutableArray.size());
    Assertions.assertEquals(100, mutableArray.get(0));
    Assertions.assertEquals(120, mutableArray.get(20));
    Assertions.assertEquals(31, mutableArray.get(21));
    Assertions.assertEquals(36, mutableArray.get(26));

    // when:
    mutableArray.addAll(anotherNativeArray);

    // then:
    Assertions.assertEquals(33, mutableArray.size());
    Assertions.assertEquals(100, mutableArray.get(0));
    Assertions.assertEquals(120, mutableArray.get(20));
    Assertions.assertEquals(31, mutableArray.get(21));
    Assertions.assertEquals(36, mutableArray.get(26));
    Assertions.assertEquals(41, mutableArray.get(27));
    Assertions.assertEquals(46, mutableArray.get(32));
  }

  @ParameterizedTest
  @MethodSource("generateMutableArrays")
  @DisplayName("should correctly remove batch elements")
  void shouldCorrectlyRemoveBatchElements(MutableIntArray mutableArray) {
    // given:
    for(int i = 0; i < 21; i++) {
      mutableArray.add(100 + i);
    }

    var anotherArray = IntArray.of(31, 32, 33, 34, 35, 36);
    var anotherNativeArray = new int[] {41, 42, 43, 44, 45, 46};

    mutableArray.addAll(anotherArray);
    mutableArray.addAll(anotherNativeArray);

    // when:
    mutableArray.removeAll(anotherArray);

    // then:
    Assertions.assertEquals(27, mutableArray.size());
    Assertions.assertEquals(100, mutableArray.get(0));
    Assertions.assertEquals(120, mutableArray.get(20));
    Assertions.assertEquals(41, mutableArray.get(21));
    Assertions.assertEquals(46, mutableArray.get(26));
  }

  @ParameterizedTest
  @MethodSource("generateMutableArrays")
  @DisplayName("should clear array")
  void shouldClearArray(MutableIntArray mutableArray) {
    // when:
    for(int i = 0; i < 21; i++) {
      mutableArray.add(100 + i);
    }

    // then:
    Assertions.assertEquals(21, mutableArray.size());
    Assertions.assertEquals(100, mutableArray.get(0));
    Assertions.assertEquals(120, mutableArray.get(20));

    // when:
    mutableArray.clear();

    // then:
    Assertions.assertEquals(0, mutableArray.size());
    Assertions.assertArrayEquals(new int[0], mutableArray.toArray());
  }

  private static Stream<Arguments> generateMutableArrays() {
    return Stream.of(
        Arguments.of(ArrayFactory.mutableIntArray()));
  }
}
