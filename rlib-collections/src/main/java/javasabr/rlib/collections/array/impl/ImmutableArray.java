package javasabr.rlib.collections.array.impl;

import java.util.stream.Stream;
import javasabr.rlib.collections.array.UnsafeArray;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ImmutableArray<E> extends AbstractArray<E> implements UnsafeArray<E> {

  E[] wrapped;

  @SafeVarargs
  public ImmutableArray(Class<? super E> type, E... elements) {
    super(type);
    this.wrapped = elements;
  }

  @Override
  public E unsafeGet(int index) {
    return wrapped[index];
  }

  @Override
  public Stream<E> stream() {
    return Stream.of(wrapped);
  }

  @Override
  public int size() {
    return wrapped.length;
  }

  @Override
  public boolean isEmpty() {
    return wrapped.length < 1;
  }

  @Override
  public E get(int index) {

    if (index < 0 || index >= size()) {
      throw new IndexOutOfBoundsException();
    }

    return wrapped[index];
  }

  @Override
  public @Nullable E[] wrapped() {
    return wrapped;
  }
}
