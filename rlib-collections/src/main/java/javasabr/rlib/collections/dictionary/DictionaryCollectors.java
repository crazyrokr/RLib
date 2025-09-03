package javasabr.rlib.collections.dictionary;

import static java.util.Collections.unmodifiableSet;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import javasabr.rlib.common.util.ObjectUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DictionaryCollectors {

  static final Set<Characteristics> CH_ID = unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
  static class CollectorImpl<T, A, R> implements Collector<T, A, R> {

    Supplier<A> supplier;
    BiConsumer<A, T> accumulator;
    BinaryOperator<A> combiner;
    Function<A, R> finisher;
    Set<Characteristics> characteristics;

    CollectorImpl(
        Supplier<A> supplier,
        BiConsumer<A, T> accumulator,
        BinaryOperator<A> combiner,
        Set<Characteristics> characteristics) {
      this(supplier, accumulator, combiner, a -> (R) a, characteristics);
    }
  }

  public static <T, K, U> Collector<T, MutableRefDictionary<K, T>, RefDictionary<K, T>> toRefDictionary(
      Function<? super T, ? extends K> keyMapper) {
    return new CollectorImpl<>(
        DictionaryFactory::mutableRefDictionary,
        uniqKeysAccumulator(keyMapper, Function.identity()),
        MutableRefDictionary::append,
        MutableRefDictionary::toReadOnly,
        CH_ID);
  }

  public static <T, K, U> Collector<T, MutableRefDictionary<K, U>, RefDictionary<K, U>> toRefDictionary(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper) {
    return new CollectorImpl<>(
        DictionaryFactory::mutableRefDictionary,
        uniqKeysAccumulator(keyMapper, valueMapper),
        MutableRefDictionary::append,
        MutableRefDictionary::toReadOnly,
        CH_ID);
  }

  private static <T, K, V> BiConsumer<MutableRefDictionary<K, V>, T> uniqKeysAccumulator(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends V> valueMapper) {
    return (map, element) -> {

      K key = keyMapper.apply(element);
      V value = ObjectUtils.notNull(valueMapper.apply(element));
      V prev = map.put(key, value);

      if (prev != null) {
        throw duplicateKeyException(key, prev, value);
      }
    };
  }

  private static IllegalStateException duplicateKeyException(Object key, Object left, Object right) {
    return new IllegalStateException(
        String.format("Duplicate key %s (attempted merging values %s and %s)", key, left, right));
  }
}
