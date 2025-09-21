package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface UnsafeRefToRefDictionary<K, V, E extends RefToRefEntry<K, V>>
    extends RefToRefDictionary<K, V> {

  @Nullable E[] entries();
}
