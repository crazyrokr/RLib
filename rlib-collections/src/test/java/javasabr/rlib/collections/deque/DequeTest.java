package javasabr.rlib.collections.deque;

import static javasabr.rlib.common.util.ArrayUtils.array;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import javasabr.rlib.collections.array.MutableArray;
import javasabr.rlib.common.util.ReflectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DequeTest {

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldAddFirst(Deque<String> deque) {

    // when:
    deque.addFirst("val1");
    deque.addFirst("val2");

    // then:
    assertArrayEquals(deque.toArray(), array("val2", "val1"));

    // when:
    deque.addFirst("val3");
    deque.addFirst("val4");

    // then:
    assertArrayEquals(deque.toArray(), array("val4", "val3", "val2", "val1"));

    // when:
    deque.addFirst("val5");
    deque.addFirst("val6");
    deque.addFirst("val7");
    deque.addFirst("val8");
    deque.addFirst("val9");
    deque.addFirst("val10");
    deque.addFirst("val11");
    deque.addFirst("val12");

    // then:
    assertArrayEquals(deque.toArray(), array("val12", "val11", "val10", "val9", "val8",
        "val7", "val6", "val5", "val4", "val3", "val2", "val1"));
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldAddLast(Deque<String> deque) {

    // when:
    deque.addLast("val1");
    deque.addLast("val2");

    // then:
    assertArrayEquals(deque.toArray(), array("val1", "val2"));

    // when:
    deque.addLast("val3");
    deque.addLast("val4");

    // then:
    assertArrayEquals(deque.toArray(), array("val1", "val2", "val3", "val4"));

    // when:
    deque.addLast("val5");
    deque.addLast("val6");
    deque.addLast("val7");
    deque.addLast("val8");
    deque.addLast("val9");
    deque.addLast("val10");
    deque.addLast("val11");
    deque.addLast("val12");

    // then:
    assertArrayEquals(deque.toArray(), array("val1", "val2", "val3", "val4", "val5",
        "val6", "val7", "val8", "val9", "val10", "val11", "val12"));
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldAddFirstAndAddLastInMixMode(Deque<String> deque) {

    // when:
    deque.addFirst("val1");
    deque.addFirst("val2");

    // then:
    assertArrayEquals(deque.toArray(), array("val2", "val1"));

    // when:
    deque.addLast("val3");
    deque.addLast("val4");

    // then:
    assertArrayEquals(deque.toArray(), array("val2", "val1", "val3", "val4"));

    // when:
    deque.addFirst("val5");
    deque.addFirst("val6");
    deque.addFirst("val7");
    deque.addLast("val8");
    deque.addLast("val9");
    deque.addLast("val10");
    deque.addFirst("val11");
    deque.addLast("val12");

    // then:
    assertArrayEquals(deque.toArray(), array("val11", "val7", "val6", "val5", "val2",
        "val1", "val3", "val4", "val8", "val9", "val10", "val12"));
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldRemoveFirst(Deque<String> deque) {

    // given:
    deque.addAll(List.of("val1", "val2", "val3", "val4", "val5"));

    // when:
    var removed1 = deque.removeFirst();
    var removed2 = deque.removeFirst();

    // then:
    assertEquals("val1", removed1);
    assertEquals("val2", removed2);
    assertArrayEquals(deque.toArray(), array("val3", "val4", "val5"));

    // when:
    var removed3 = deque.removeFirst();
    var removed4 = deque.removeFirst();

    // then:
    assertEquals("val3", removed3);
    assertEquals("val4", removed4);
    assertArrayEquals(deque.toArray(), array("val5"));

    // when:
    var removed5 = deque.removeFirst();

    // then:
    assertEquals("val5", removed5);
    assertArrayEquals(deque.toArray(), array());

    // when/then:
    Assertions.assertThrows(NoSuchElementException.class, deque::removeFirst);
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldRemoveLast(Deque<String> deque) {

    // given:
    deque.addAll(List.of("val1", "val2", "val3", "val4", "val5"));

    // when:
    var removed1 = deque.removeLast();
    var removed2 = deque.removeLast();

    // then:
    assertEquals("val5", removed1);
    assertEquals("val4", removed2);
    assertArrayEquals(deque.toArray(), array("val1", "val2", "val3"));

    // when:
    var removed3 = deque.removeLast();
    var removed4 = deque.removeLast();

    // then:
    assertEquals("val3", removed3);
    assertEquals("val2", removed4);
    assertArrayEquals(deque.toArray(), array("val1"));

    // when:
    var removed5 = deque.removeLast();

    // then:
    assertEquals("val1", removed5);
    assertArrayEquals(deque.toArray(), array());

    // when/then:
    Assertions.assertThrows(NoSuchElementException.class, deque::removeLast);
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldRemoveFirstAndLast(Deque<String> deque) {

    // given:
    deque.addAll(List.of("val1", "val2", "val3", "val4", "val5", "val6", "val7", "val8"));

    // when:
    var removed1 = deque.removeFirst();
    var removed2 = deque.removeLast();

    // then:
    assertEquals("val1", removed1);
    assertEquals("val8", removed2);
    assertArrayEquals(deque.toArray(), array("val2", "val3", "val4", "val5", "val6", "val7"));

    // when:
    var removed3 = deque.removeFirst();
    var removed4 = deque.removeLast();

    // then:
    assertEquals("val2", removed3);
    assertEquals("val7", removed4);
    assertArrayEquals(deque.toArray(), array("val3", "val4", "val5", "val6"));

    // when:
    var removed5 = deque.removeFirst();
    var removed6 = deque.removeFirst();
    var removed7 = deque.removeLast();
    var removed8 = deque.removeLast();

    // then:
    assertEquals("val3", removed5);
    assertEquals("val4", removed6);
    assertEquals("val6", removed7);
    assertEquals("val5", removed8);
    assertArrayEquals(deque.toArray(), array());

    // when/then:
    Assertions.assertThrows(NoSuchElementException.class, deque::removeFirst);
    Assertions.assertThrows(NoSuchElementException.class, deque::removeLast);
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldRemoveElement(Deque<String> deque) {

    // given:
    deque.addAll(List.of("val1", "val2", "val3", "val4", "val5", "val6", "val7", "val8", "val9", "val10"));

    // when:
    deque.remove("val9");

    // then:
    assertArrayEquals(deque.toArray(), array("val1", "val2", "val3", "val4", "val5", "val6", "val7", "val8", "val10"));

    // when:
    deque.remove("val2");

    // then:
    assertArrayEquals(deque.toArray(), array("val1", "val3", "val4", "val5", "val6", "val7", "val8", "val10"));

    // when:
    deque.remove("val5");

    // then:
    assertArrayEquals(deque.toArray(), array("val1", "val3", "val4", "val6", "val7", "val8", "val10"));

    // when:
    deque.remove("val6");

    // then:
    assertArrayEquals(deque.toArray(), array("val1", "val3", "val4", "val7", "val8", "val10"));

    // when:
    deque.remove("val1");

    // then:
    assertArrayEquals(deque.toArray(), array("val3", "val4", "val7", "val8", "val10"));

    // when:
    deque.remove("val10");

    // then:
    assertArrayEquals(deque.toArray(), array("val3", "val4", "val7", "val8"));
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldCheckContains(Deque<String> deque) {

    // given:
    deque.addAll(List.of("val1", "val2", "val3", "val4", "val5", "val6", "val7", "val8", "val9", "val10"));

    // when/then:
    assertTrue(deque.contains("val1"));
    assertTrue(deque.contains("val10"));
    assertTrue(deque.contains("val3"));
    assertTrue(deque.contains("val8"));
    assertFalse(deque.contains("val55"));
  }

  @Test
  void shouldRebalanceIndexesAddLastRemoveFirst() {

    // given:
    Deque<String> deque = DequeFactory.arrayBasedBased(String.class, 15);
    Field head = ReflectionUtils.getUnsafeField(deque, "head");
    Field tail = ReflectionUtils.getUnsafeField(deque, "tail");

    // when:
    for (int i = 1; i < 12; i++) {
      deque.addLast("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeFirst();
    }

    int headValue = ReflectionUtils.getFieldValue(deque, head);
    int tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(18, headValue);
    assertEquals(18, tailValue);
    assertEquals(0, deque.size());

    // when:
    for (int i = 12; i < 24; i++) {
      deque.addLast("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeFirst();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(29, headValue);
    assertEquals(29, tailValue);
    assertEquals(1, deque.size());

    // when:
    for (int i = 24; i < 36; i++) {
      deque.addLast("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeFirst();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(40, headValue);
    assertEquals(41, tailValue);
    assertEquals(2, deque.size());

    // when:
    for (int i = 36; i < 48; i++) {
      deque.addLast("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeFirst();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(51, headValue);
    assertEquals(53, tailValue);
    assertEquals(3, deque.size());

    // when:
    for (int i = 48; i < 60; i++) {
      deque.addLast("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeFirst();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(40, headValue);
    assertEquals(43, tailValue);
    assertEquals(4, deque.size());

    // when:
    for (int i = 60; i < 72; i++) {
      deque.addLast("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeFirst();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(51, headValue);
    assertEquals(55, tailValue);
    assertEquals(5, deque.size());

    // when:
    for (int i = 60; i < 72; i++) {
      deque.addLast("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeFirst();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(39, headValue);
    assertEquals(44, tailValue);
    assertEquals(6, deque.size());
  }

  @Test
  void shouldRebalanceIndexesAddFirstRemoveLast() {

    // given:
    Deque<String> deque = DequeFactory.arrayBasedBased(String.class, 15);
    Field head = ReflectionUtils.getUnsafeField(deque, "head");
    Field tail = ReflectionUtils.getUnsafeField(deque, "tail");

    // when:
    for (int i = 1; i < 12; i++) {
      deque.addFirst("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeLast();
    }

    int headValue = ReflectionUtils.getFieldValue(deque, head);
    int tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(6, headValue);
    assertEquals(6, tailValue);
    assertEquals(0, deque.size());

    // when:
    for (int i = 12; i < 24; i++) {
      deque.addFirst("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeLast();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(5, headValue);
    assertEquals(5, tailValue);
    assertEquals(1, deque.size());

    // when:
    for (int i = 24; i < 36; i++) {
      deque.addFirst("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeLast();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(3, headValue);
    assertEquals(4, tailValue);
    assertEquals(2, deque.size());

    // when:
    for (int i = 36; i < 48; i++) {
      deque.addFirst("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeLast();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(1, headValue);
    assertEquals(3, tailValue);
    assertEquals(3, deque.size());

    // when:
    for (int i = 48; i < 60; i++) {
      deque.addFirst("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeLast();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(31, headValue);
    assertEquals(34, tailValue);
    assertEquals(4, deque.size());

    // when:
    for (int i = 60; i < 72; i++) {
      deque.addFirst("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeLast();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(19, headValue);
    assertEquals(23, tailValue);
    assertEquals(5, deque.size());

    // when:
    for (int i = 60; i < 72; i++) {
      deque.addFirst("val%s".formatted(i));
    }
    for (int i = 1; i < 12; i++) {
      deque.removeLast();
    }

    headValue = ReflectionUtils.getFieldValue(deque, head);
    tailValue = ReflectionUtils.getFieldValue(deque, tail);

    // then:
    assertEquals(30, headValue);
    assertEquals(35, tailValue);
    assertEquals(6, deque.size());
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldCorrectlyIterateDeque(Deque<String> deque) {

    // given:
    deque.addAll(List.of("val1", "val2", "val3", "val4", "val5", "val6"));
    var container = MutableArray.ofType(String.class);

    // when:
    for (Iterator<String> iterator = deque.iterator(); iterator.hasNext(); ) {
      String value = iterator.next();
      container.add(value);
      if (value.equals("val2") || value.equals("val4")) {
        iterator.remove();
      }
    }

    // then:
    assertArrayEquals(container.toArray(), array("val1", "val2", "val3", "val4", "val5", "val6"));
    assertArrayEquals(deque.toArray(), array("val1", "val3", "val5", "val6"));
  }

  @ParameterizedTest
  @MethodSource("generateDeque")
  void shouldCorrectlyIterate2Deque(Deque<String> deque) {

    // given:
    deque.addAll(List.of("val1", "val2", "val3", "val4", "val5", "val6"));
    var container = MutableArray.ofType(String.class);

    // when:
    for (Iterator<String> iterator = deque.descendingIterator(); iterator.hasNext(); ) {
      String value = iterator.next();
      container.add(value);
      if (value.equals("val2") || value.equals("val4")) {
        iterator.remove();
      }
    }

    // then:
    assertArrayEquals(container.toArray(), array("val6", "val5", "val4", "val3", "val2", "val1"));
    assertArrayEquals(deque.toArray(), array("val1", "val3", "val5", "val6"));
  }

  private static Stream<Arguments> generateDeque() {
    return Stream.of(
        //Arguments.of(DequeFactory.linkedListBased()),
        Arguments.of(DequeFactory.arrayBasedBased(String.class, 15)));
  }
}
