package javasabr.rlib.collections.deque.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class LinkedListIterator<E> implements Iterator<E> {

  public static final int NEXT = 1;
  public static final int PREV = 2;

  final AbstractLinkedListBasedDeque<E> linkedList;
  final Function<LinkedNode<E>, @Nullable LinkedNode<E>> nextFunction;

  @Nullable
  LinkedNode<E> next;
  @Nullable
  LinkedNode<E> current;

  public LinkedListIterator(AbstractLinkedListBasedDeque<E> linkedList, int mode) {
    if (mode == NEXT) {
      nextFunction = LinkedNode::next;
      next = linkedList.firstNode();
    } else if (mode == PREV) {
      nextFunction = LinkedNode::prev;
      next = linkedList.lastNode();
    } else {
      throw new IllegalArgumentException("Unknown mode:" + mode);
    }
    this.linkedList = linkedList;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public E next() {
    if (next == null) {
      throw new NoSuchElementException();
    }
    E item = next.item;
    current = next;
    next = nextFunction.apply(next);
    return item;
  }

  @Override
  public void remove() {
    if (current == null) {
      throw new NoSuchElementException();
    }
    linkedList.unlink(current);
  }
}
