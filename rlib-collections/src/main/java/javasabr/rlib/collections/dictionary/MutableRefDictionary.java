package javasabr.rlib.collections.dictionary;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public interface MutableRefDictionary<K, V> extends RefDictionary<K, V> {

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
