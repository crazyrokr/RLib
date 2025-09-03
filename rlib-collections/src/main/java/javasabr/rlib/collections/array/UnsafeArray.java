package javasabr.rlib.collections.array;

import org.jspecify.annotations.Nullable;

public interface UnsafeArray<E> extends Array<E> {
  @Nullable E[] wrapped();

  E unsafeGet(int index);
}
