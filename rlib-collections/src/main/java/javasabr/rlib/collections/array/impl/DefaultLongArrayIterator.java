package javasabr.rlib.collections.array.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javasabr.rlib.collections.array.LongArray;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultLongArrayIterator implements Iterator<Long> {

  final long[] wrapped;
  final int size;

  int position;

  public DefaultLongArrayIterator(LongArray array) {
    this.wrapped = array.asUnsafe().wrapped();
    this.size = array.size();
  }

  @Override
  public boolean hasNext() {
    return position < size;
  }

  @Override
  public Long next() {
    if (position >= size) {
      throw new NoSuchElementException();
    }
    return wrapped[position++];
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
