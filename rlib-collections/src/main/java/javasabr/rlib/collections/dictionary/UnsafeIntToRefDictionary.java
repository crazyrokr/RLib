package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface UnsafeIntToRefDictionary<V, E extends IntToRefEntry<V>> extends IntToRefDictionary<V> {

  @Nullable E[] entries();
}
