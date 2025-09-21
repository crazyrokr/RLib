package javasabr.rlib.collections.dictionary;

import java.util.Optional;
import javasabr.rlib.collections.array.LongArray;
import javasabr.rlib.collections.array.MutableLongArray;
import javasabr.rlib.collections.dictionary.impl.ImmutableHashBasedLongToRefDictionary;
import javasabr.rlib.collections.dictionary.impl.SimpleLongToRefEntry;
import javasabr.rlib.functions.LongObjConsumer;
import org.jspecify.annotations.Nullable;

public interface LongToRefDictionary<V> extends Dictionary<Long, V> {

  static <V> LongToRefEntry<V> entry(long key, @Nullable V value) {
    return new SimpleLongToRefEntry<>(key, value);
  }

  static <K, V> LongToRefDictionary<V> empty() {
    return ImmutableHashBasedLongToRefDictionary.empty();
  }

  static <K, V> LongToRefDictionary<V> of(long key, @Nullable V value) {
    return ofEntries(entry(key, value));
  }

  static <K, V> LongToRefDictionary<V> of(long k1, @Nullable V v1, long k2, @Nullable V v2) {
    return ofEntries(entry(k1, v1), entry(k2, v2));
  }

  @SafeVarargs
  static <K, V> LongToRefDictionary<V> ofEntries(LongToRefEntry<V>... entries) {
    MutableLongToRefDictionary<V> mutable = DictionaryFactory.mutableLongToRefDictionary();
    for (var entry : entries) {
      mutable.put(entry.key(), entry.value());
    }
    return mutable.toReadOnly();
  }

  boolean containsKey(long key);

  @Nullable
  V get(long key);

  Optional<V> getOptional(long key);

  @Nullable
  V getOrDefault(long key, V def);

  MutableLongArray keys(MutableLongArray container);

  LongArray keys();

  void forEach(LongObjConsumer<V> consumer);
}
