package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface UnsafeLongToRefDictionary<V, E extends LongToRefEntry<V>> extends LongToRefDictionary<V> {

  @Nullable E[] entries();
}
