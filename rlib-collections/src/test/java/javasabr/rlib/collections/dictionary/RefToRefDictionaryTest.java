package javasabr.rlib.collections.dictionary;

import static javasabr.rlib.collections.dictionary.RefToRefDictionary.entry;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.collections.array.MutableArray;
import javasabr.rlib.common.tuple.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RefToRefDictionaryTest {

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldFindValuesByKeys(RefToRefDictionary<String, String> dictionary) {
    // then:
    Assertions.assertEquals("val1", dictionary.get("key1"));
    Assertions.assertEquals("val3", dictionary.get("key3"));
    Assertions.assertEquals("val4", dictionary.get("key4"));
    Assertions.assertNull(dictionary.get("key10"));
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldCheckContainsKeys(RefToRefDictionary<String, String> dictionary) {
    // then:
    Assertions.assertTrue(dictionary.containsKey("key1"));
    Assertions.assertTrue(dictionary.containsKey("key2"));
    Assertions.assertTrue(dictionary.containsKey("key5"));
    Assertions.assertFalse(dictionary.containsKey("key10"));
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldCheckContainsValues(RefToRefDictionary<String, String> dictionary) {
    // then:
    Assertions.assertTrue(dictionary.containsValue("val1"));
    Assertions.assertTrue(dictionary.containsValue("val3"));
    Assertions.assertTrue(dictionary.containsValue("val4"));
    Assertions.assertFalse(dictionary.containsValue("val10"));
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldHaveExpectedKeys(RefToRefDictionary<String, String> dictionary) {

    // given:
    var expectedArray = Array.typed(String.class, "key4", "key3", "key5", "key2", "key1");
    var expectedSet = Set.copyOf(expectedArray.toList());

    // when:
    Array<String> keys = dictionary.keys(String.class);

    // then:
    Assertions.assertEquals(expectedArray, keys);

    // when:
    Array<String> keys2 = dictionary.keys(MutableArray.ofType(String.class));

    // then:
    Assertions.assertEquals(expectedArray, keys2);

    // when:
    Set<String> keys3 = dictionary.keys(new HashSet<>());

    // then:
    Assertions.assertEquals(expectedSet, keys3);
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldHaveExpectedValues(RefToRefDictionary<String, String> dictionary) {

    // given:
    var expectedArray = Array.typed(String.class, "val4", "val3", "val5", "val2", "val1");
    var expectedSet = Set.copyOf(expectedArray.toList());

    // when:
    Array<String> values = dictionary.values(String.class);

    // then:
    Assertions.assertEquals(expectedArray, values);

    // when:
    Array<String> values2 = dictionary.values(MutableArray.ofType(String.class));

    // then:
    Assertions.assertEquals(expectedArray, values2);

    // when:
    Set<String> keys3 = dictionary.values(new HashSet<>());

    // then:
    Assertions.assertEquals(expectedSet, keys3);
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldHaveExpectedResultFromForEach(RefToRefDictionary<String, String> dictionary) {

    // given:
    var expectedArray = Array.typed(String.class, "val4", "val3", "val5", "val2", "val1");
    var expectedPairs = Array.typed(Tuple.class,
        new Tuple<>("key4", "val4"),
        new Tuple<>("key3", "val3"),
        new Tuple<>("key5", "val5"),
        new Tuple<>("key2", "val2"),
        new Tuple<>("key1", "val1"));

    // when:
    MutableArray<String> values = MutableArray.ofType(String.class);
    dictionary.forEach(values::add);

    // then:
    Assertions.assertEquals(expectedArray, values);

    // when:
    MutableArray<Tuple> pairs = MutableArray.ofType(Tuple.class);
    dictionary.forEach((key, value) -> pairs.add(new Tuple<>(key, value)));

    // then:
    Assertions.assertEquals(expectedPairs, pairs);
  }

  private static Stream<Arguments> generateDictionaries() {

    RefToRefDictionary<String, String> source = RefToRefDictionary.ofEntries(
        entry("key1", "val1"),
        entry("key2", "val2"),
        entry("key3", "val3"),
        entry("key4", "val4"),
        entry("key5", "val5"));

    return Stream.of(
        Arguments.of(source),
        Arguments.of(DictionaryFactory.mutableRefToRefDictionary().append(source)),
        Arguments.of(DictionaryFactory.stampedLockBasedRefToRefDictionary().append(source)));
  }
}
