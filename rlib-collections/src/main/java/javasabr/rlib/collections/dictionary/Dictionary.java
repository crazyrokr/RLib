package javasabr.rlib.collections.dictionary;

import java.util.Optional;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.collections.array.MutableArray;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
public interface Dictionary<K, V> extends Iterable<V> {

  boolean containsKey(K key);

  boolean containsValue(V value);

  boolean isEmpty();

  int size();

  @Nullable
  V get(K key);

  Optional<V> getOptional(K key);

  @Nullable
  V getOrDefault(K key, V def);

  MutableArray<K> keys(MutableArray<K> container);

  Array<K> keys(Class<K> type);

  MutableArray<V> values(MutableArray<V> container);

  Array<V> values(Class<V> type);
}
