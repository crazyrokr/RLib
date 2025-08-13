package javasabr.rlib.collections.array.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javasabr.rlib.collections.array.Array;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultArrayIterator<E> implements Iterator<E> {

  final @Nullable E[] wrapped;
  final int size;

  private int position;

  public DefaultArrayIterator(Array<E> array) {
    this.wrapped = array.asUnsafe().wrapped();
    this.size = array.size();
  }

  @Override
  public boolean hasNext() {
    return position < size;
  }

  @Override
  public E next() {
    if (position >= size) {
      throw new NoSuchElementException();
    }
    //noinspection DataFlowIssue
    return wrapped[position++];
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
