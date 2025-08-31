package javasabr.rlib.collections.array.impl;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javasabr.rlib.collections.array.ArrayIterationFunctions;
import javasabr.rlib.collections.array.UnsafeArray;
import org.jspecify.annotations.Nullable;

public record DefaultArrayIterationFunctions<E>(UnsafeArray<E> array) implements ArrayIterationFunctions<E> {

  @Override
  public <T> @Nullable E findAny(T arg1, BiPredicate<? super E, T> filter) {

    @Nullable E[] wrapped = array.wrapped();
    int size = array.size();

    for (int i = 0; i < size; i++) {
      E element = wrapped[i];
      if (filter.test(element, arg1)) {
        return element;
      }
    }
    return null;
  }

  @Override
  public <T> ArrayIterationFunctions<E> forEach(T arg1, BiConsumer<? super E, T> consumer) {
    @Nullable E[] wrapped = array.wrapped();
    int size = array.size();
    for (int i = 0; i < size; i++) {
      consumer.accept(wrapped[i], arg1);
    }
    return this;
  }

  @Override
  public <T> boolean anyMatch(T arg1, BiPredicate<? super E, T> filter) {
    return findAny(arg1, filter) != null;
  }
}
