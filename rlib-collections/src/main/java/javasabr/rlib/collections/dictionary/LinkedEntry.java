package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface LinkedEntry<N>  {
  @Nullable
  N next();
  void next(@Nullable N next);
}
