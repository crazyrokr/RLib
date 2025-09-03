package javasabr.rlib.collections.array.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javasabr.rlib.collections.array.UnsafeMutableArray;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultMutableArrayIterator<E> implements Iterator<E> {

  final UnsafeMutableArray<E> mutableArray;

  int size;
  int position;

  public DefaultMutableArrayIterator(UnsafeMutableArray<E> array) {
    this.mutableArray = array;
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
    return mutableArray.unsafeGet(position++);
  }

  @Override
  public void remove() {
    mutableArray.remove(--position);
    size--;
  }
}
