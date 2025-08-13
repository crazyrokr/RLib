package javasabr.rlib.collections.array;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public interface MutableArray<E> extends Array<E>, Collection<E> {

  boolean addAll(Array<? extends E> array);

  /**
   * @return the element previously at the specified position
   */
  E remove(int index);

  void replace(int index, E element);

  @Override
  Stream<E> stream();

  @Override
  default <T> T[] toArray(IntFunction<T[]> generator) {
    return Collection.super.toArray(generator);
  }

  @Override
  default Iterator<E> iterator() {
    return Array.super.iterator();
  }

  @Override
  default void forEach(Consumer<? super E> action) {
    Array.super.forEach(action);
  }
}
