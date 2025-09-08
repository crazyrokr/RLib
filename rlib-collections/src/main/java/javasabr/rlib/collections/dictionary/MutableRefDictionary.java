package javasabr.rlib.collections.dictionary;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javasabr.rlib.collections.dictionary.impl.DefaultMutableHashBasedRefDictionary;
import org.jspecify.annotations.Nullable;

public interface MutableRefDictionary<K, V> extends RefDictionary<K, V> {

  static <K, V> MutableRefDictionary<K, V> ofTypes(
      Class<K> keyType,
      Class<V> valueType) {
    return new DefaultMutableHashBasedRefDictionary<>();
  }

  @Nullable
  V getOrCompute(K key, Supplier<V> factory);

  @Nullable
  V getOrCompute(K key, Function<K, V> factory);

  @Nullable
  <T> V getOrCompute(K key, T arg1, Function<T, V> factory);

  /**
   * @return the previous value for the key or null.
   */
  @Nullable
  V put(K key, @Nullable V value);

  void putAll(RefDictionary<? extends K, ? extends V> dictionary);

  MutableRefDictionary<K, V> append(RefDictionary<? extends K, ? extends V> dictionary);

  /**
   * @return the optional value of the previous value for the key.
   */
  Optional<V> putOptional(K key, @Nullable V value);

  /**
   * @return the previous value for the key or null.
   */
  @Nullable
  V remove(K key);

  /**
   * @return the optional value of the previous value for the key.
   */
  Optional<V> removeOptional(K key);

  RefDictionary<K, V> toReadOnly();
}
