package javasabr.rlib.collections.dictionary;

import static javasabr.rlib.collections.dictionary.RefToRefDictionary.entry;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MutableRefToRefDictionaryTest {

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldPutNewPairs(MutableRefToRefDictionary<String, String> dictionary) {
    // when:
    dictionary.put("key1", "val1");
    dictionary.put("key4", "val4");
    dictionary.put("key7", "val7");
    dictionary.put("key55", "val55");

    // then:
    Assertions.assertEquals("val1", dictionary.get("key1"));
    Assertions.assertEquals("val4", dictionary.get("key4"));
    Assertions.assertEquals("val55", dictionary.get("key55"));
    Assertions.assertTrue(dictionary.containsKey("key1"));
    Assertions.assertTrue(dictionary.containsKey("key4"));
    Assertions.assertTrue(dictionary.containsKey("key55"));
    Assertions.assertNull(dictionary.get("key10"));
    Assertions.assertEquals(4, dictionary.size());
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldRemoveByKeys(MutableRefToRefDictionary<String, String> dictionary) {
    // given:
    dictionary.put("key1", "val1");
    dictionary.put("key4", "val4");
    dictionary.put("key7", "val7");
    dictionary.put("key55", "val55");

    // when:
    String removed1 = dictionary.remove("key1");
    String removed2 = dictionary.remove("key55");

    // then:
    Assertions.assertEquals("val1", removed1);
    Assertions.assertEquals("val55", removed2);
    Assertions.assertEquals("val4", dictionary.get("key4"));
    Assertions.assertEquals("val7", dictionary.get("key7"));
    Assertions.assertNull(dictionary.get("key1"));
    Assertions.assertNull(dictionary.get("key55"));
    Assertions.assertFalse(dictionary.containsKey("key1"));
    Assertions.assertFalse(dictionary.containsKey("key55"));
    Assertions.assertEquals(2, dictionary.size());
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldCorrectlyComputeOnGet(MutableRefToRefDictionary<String, String> dictionary) {
    // when:
    var computed1 = dictionary.getOrCompute("key1", () -> "val1");
    var computed2 = dictionary.getOrCompute("key4", key -> "val4");
    var computed3 = dictionary.getOrCompute("key10", 5, arg1 -> "val10");

    // then:
    Assertions.assertEquals("val1", computed1);
    Assertions.assertEquals("val4", computed2);
    Assertions.assertEquals("val10", computed3);
    Assertions.assertEquals("val1", dictionary.get("key1"));
    Assertions.assertEquals("val4", dictionary.get("key4"));
    Assertions.assertEquals("val10", dictionary.get("key10"));
    Assertions.assertTrue(dictionary.containsKey("key1"));
    Assertions.assertTrue(dictionary.containsKey("key4"));
    Assertions.assertTrue(dictionary.containsKey("key10"));
    Assertions.assertEquals(3, dictionary.size());
  }

  @ParameterizedTest
  @MethodSource("generateDictionaries")
  void shouldAppendDictionary(MutableRefToRefDictionary<String, String> dictionary) {
    // given:
    RefToRefDictionary<String, String> source = RefToRefDictionary.ofEntries(
        entry("key1", "val1"),
        entry("key2", "val2"),
        entry("key3", "val3"),
        entry("key4", "val4"),
        entry("key5", "val5"));

    // when:
    dictionary.append(source);

    // then:
    Assertions.assertEquals("val1", dictionary.get("key1"));
    Assertions.assertEquals("val4", dictionary.get("key4"));
    Assertions.assertEquals("val5", dictionary.get("key5"));
    Assertions.assertTrue(dictionary.containsKey("key3"));
    Assertions.assertFalse(dictionary.containsKey("key55"));
    Assertions.assertNull(dictionary.get("key10"));
    Assertions.assertEquals(5, dictionary.size());
  }

  private static Stream<Arguments> generateDictionaries() {
    return Stream.of(
        Arguments.of(DictionaryFactory.mutableRefToRefDictionary()),
        Arguments.of(DictionaryFactory.stampedLockBasedRefToRefDictionary()));
  }
}
