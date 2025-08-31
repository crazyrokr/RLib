package javasabr.rlib.collections.array.impl;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javasabr.rlib.collections.array.ReversedArrayIterationFunctions;
import javasabr.rlib.collections.array.UnsafeArray;
import org.jspecify.annotations.Nullable;

public record DefaultReversedArrayIterationFunctions<E>(UnsafeArray<E> array) implements
    ReversedArrayIterationFunctions<E> {

  @Override
  public <T> @Nullable E findAny(T arg1, BiPredicate<T, ? super E> filter) {

    @Nullable E[] wrapped = array.wrapped();
    int size = array.size();

    for (int i = 0; i < size; i++) {
      E element = wrapped[i];
      if (filter.test(arg1, element)) {
        return element;
      }
    }
    return null;
  }

  @Override
  public <T> ReversedArrayIterationFunctions<E> forEach(T arg1, BiConsumer<T, ? super E> consumer) {
    @Nullable E[] wrapped = array.wrapped();
    int size = array.size();
    for (int i = 0; i < size; i++) {
      consumer.accept(arg1, wrapped[i]);
    }
    return this;
  }

  @Override
  public <T> boolean anyMatch(T arg1, BiPredicate<T, ? super E> filter) {
    return findAny(arg1, filter) != null;
  }
}
