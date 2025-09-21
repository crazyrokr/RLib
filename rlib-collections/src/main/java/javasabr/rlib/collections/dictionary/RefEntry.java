package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface RefEntry<V> {
  @Nullable V value();
  void value(@Nullable V value);
}
