package javasabr.rlib.collections.deque.impl;

import java.util.Iterator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class ArrayBasedDequeDescendingIterator<E> implements Iterator<E> {

  final AbstractArrayBasedDeque<E> deque;
  int iterated;

  public ArrayBasedDequeDescendingIterator(AbstractArrayBasedDeque<E> deque) {
    this.deque = deque;
  }

  @Override
  public boolean hasNext() {
    return iterated < deque.size();
  }

  @Override
  public E next() {
    int index = deque.tail() - iterated++;
    return deque.items()[index];
  }

  @Override
  public void remove() {
    int index = deque.tail() - --iterated;
    deque.remove(deque.items()[index]);
  }
}

