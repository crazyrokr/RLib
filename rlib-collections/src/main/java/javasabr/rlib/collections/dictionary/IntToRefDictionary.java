package javasabr.rlib.collections.dictionary;

import java.util.Optional;
import javasabr.rlib.collections.array.IntArray;
import javasabr.rlib.collections.array.MutableIntArray;
import javasabr.rlib.collections.dictionary.impl.ImmutableHashBasedIntToRefDictionary;
import javasabr.rlib.collections.dictionary.impl.SimpleIntToRefEntry;
import javasabr.rlib.functions.IntObjConsumer;
import org.jspecify.annotations.Nullable;

public interface IntToRefDictionary<V> extends Dictionary<Integer, V> {

  static <V> IntToRefEntry<V> entry(int key, @Nullable V value) {
    return new SimpleIntToRefEntry<>(key, value);
  }

  static <V> IntToRefDictionary<V> empty() {
    return ImmutableHashBasedIntToRefDictionary.empty();
  }

  static <K, V> IntToRefDictionary<V> of(int key, @Nullable V value) {
    return ofEntries(entry(key, value));
  }

  static <K, V> IntToRefDictionary<V> of(int k1, @Nullable V v1, int k2, @Nullable V v2) {
    return ofEntries(entry(k1, v1), entry(k2, v2));
  }

  @SafeVarargs
  static <K, V> IntToRefDictionary<V> ofEntries(IntToRefEntry<V>... entries) {
    MutableIntToRefDictionary<V> mutable = DictionaryFactory.mutableIntToRefDictionary();
    for (var entry : entries) {
      mutable.put(entry.key(), entry.value());
    }
    return mutable.toReadOnly();
  }

  boolean containsKey(int key);

  @Nullable
  V get(int key);

  Optional<V> getOptional(int key);

  @Nullable
  V getOrDefault(int key, V def);

  MutableIntArray keys(MutableIntArray container);

  IntArray keys();

  void forEach(IntObjConsumer<V> consumer);
}
