package javasabr.rlib.collections.dictionary;

import java.util.function.BiConsumer;
import javasabr.rlib.collections.dictionary.impl.ImmutableHashBasedRefToRefDictionary;
import javasabr.rlib.collections.dictionary.impl.SimpleRefToRefEntry;
import org.jspecify.annotations.Nullable;

public interface RefToRefDictionary<K, V> extends Dictionary<K, V> {

  static <K, V> RefToRefEntry<K, V> entry(K key, @Nullable V value) {
    return new SimpleRefToRefEntry<>(key, value);
  }

  static <K, V> RefToRefDictionary<K, V> empty() {
    return ImmutableHashBasedRefToRefDictionary.empty();
  }

  static <K, V> RefToRefDictionary<K, V> of(K key, @Nullable V value) {
    return ofEntries(entry(key, value));
  }

  static <K, V> RefToRefDictionary<K, V> of(K k1, @Nullable V v1, K k2, @Nullable V v2) {
    return ofEntries(entry(k1, v1), entry(k2, v2));
  }

  @SafeVarargs
  static <K, V> RefToRefDictionary<K, V> ofEntries(RefToRefEntry<K, V>... entries) {
    MutableRefToRefDictionary<K, V> mutable = DictionaryFactory.mutableRefToRefDictionary();
    for (var entry : entries) {
      mutable.put(entry.key(), entry.value());
    }
    return mutable.toReadOnly();
  }

  void forEach(BiConsumer<K, V> consumer);
}
