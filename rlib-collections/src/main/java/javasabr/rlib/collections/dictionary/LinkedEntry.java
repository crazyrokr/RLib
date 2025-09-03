package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface LinkedEntry<K, V, N> extends Entry<K, V>  {
  @Nullable
  N next();
  void next(@Nullable N next);
}
