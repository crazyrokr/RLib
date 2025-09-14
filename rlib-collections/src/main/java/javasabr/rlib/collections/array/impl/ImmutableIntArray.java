package javasabr.rlib.collections.array.impl;

import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ImmutableIntArray extends AbstractIntArray {

  int[] wrapped;

  public ImmutableIntArray(int... elements) {
    this.wrapped = elements;
  }

  @Override
  public int unsafeGet(int index) {
    return wrapped[index];
  }

  @Override
  public IntStream stream() {
    return IntStream.of(wrapped);
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
  public int get(int index) {

    if (index < 0 || index >= size()) {
      throw new IndexOutOfBoundsException();
    }

    return wrapped[index];
  }

  @Override
  public int[] wrapped() {
    return wrapped;
  }
}
