package javasabr.rlib.collections.deque.impl;

import java.util.Collection;
import java.util.Deque;

public abstract class AbstractDeque<E> implements Deque<E> {

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    int changed = 0;
    for (E object : collection) {
      if (add(object)) {
        changed++;
      }
    }
    return changed > 0;
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    for (Object object : collection) {
      if (!contains(object)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public E element() {
    return getFirst();
  }

  @Override
  public boolean isEmpty() {
    return size() < 1;
  }

  @Override
  public E pop() {
    return removeFirst();
  }

  @Override
  public E remove() {
    return removeFirst();
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder("[");

    if (!isEmpty()) {

      for (E element : this) {
        builder
            .append(element)
            .append(", ");
      }

      if (builder.indexOf(",") != -1) {
        builder.delete(builder.length() - 2, builder.length());
      }
    }

    builder.append("]");

    return builder.toString();
  }
}
