package javasabr.rlib.collections.array.impl;

import java.util.stream.LongStream;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ImmutableLongArray extends AbstractLongArray {

  long[] wrapped;

  public ImmutableLongArray(long... elements) {
    this.wrapped = elements;
  }

  @Override
  public long unsafeGet(int index) {
    return wrapped[index];
  }

  @Override
  public LongStream stream() {
    return LongStream.of(wrapped);
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
  public long get(int index) {

    if (index < 0 || index >= size()) {
      throw new IndexOutOfBoundsException();
    }

    return wrapped[index];
  }

  @Override
  public long[] wrapped() {
    return wrapped;
  }
}
