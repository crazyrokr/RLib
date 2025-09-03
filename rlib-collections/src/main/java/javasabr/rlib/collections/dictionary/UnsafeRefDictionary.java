package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface UnsafeRefDictionary<K, V, E extends Entry<K, V>> extends RefDictionary<K, V> {

  @Nullable E[] entries();
}
