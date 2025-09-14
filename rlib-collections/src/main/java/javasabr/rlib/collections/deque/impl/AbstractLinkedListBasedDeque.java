package javasabr.rlib.collections.deque.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import javasabr.rlib.common.util.ArrayUtils;
import org.jspecify.annotations.Nullable;

public abstract class AbstractLinkedListBasedDeque<E> extends AbstractDeque<E> {

  @Nullable
  protected abstract LinkedNode<E> firstNode();

  @Nullable
  protected abstract LinkedNode<E> lastNode();

  protected abstract E unlink(LinkedNode<E> node);

  protected abstract E unlinkFirst(LinkedNode<E> node);

  protected abstract E unlinkLast(LinkedNode<E> node);

  protected abstract void linkFirst(E item);
  protected abstract void linkLast(E item);

  @Override
  public boolean contains(@Nullable Object object) {
    if (object == null) {
      return false;
    }
    for (LinkedNode<E> node = firstNode(); node != null; node = node.next) {
      if (Objects.equals(node.item, object)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public E getFirst() {

    LinkedNode<E> firstNode = firstNode();
    if (firstNode == null) {
      throw new NoSuchElementException();
    }

    //noinspection DataFlowIssue
    return firstNode.item;
  }

  @Override
  public E getLast() {

    LinkedNode<E> linkedNode = lastNode();
    if (linkedNode == null) {
      throw new NoSuchElementException();
    }

    //noinspection DataFlowIssue
    return linkedNode.item;
  }

  @Override
  public boolean offer(E element) {
    return add(element);
  }

  @Override
  public boolean offerFirst(E element) {
    addFirst(element);
    return true;
  }

  @Override
  public boolean offerLast(E element) {
    addLast(element);
    return true;
  }

  @Nullable
  @Override
  public E peek() {
    LinkedNode<E> first = firstNode();
    return first == null ? null : first.item;
  }

  @Nullable
  @Override
  public E peekFirst() {
    LinkedNode<E> first = firstNode();
    return first == null ? null : first.item;
  }

  @Nullable
  @Override
  public E peekLast() {
    LinkedNode<E> last = lastNode();
    return last == null ? null : last.item;
  }

  @Override
  public void push(E element) {
    linkFirst(element);
  }

  @Override
  public boolean add(E element) {
    linkLast(element);
    return true;
  }

  @Override
  public void addFirst(E element) {
    linkFirst(element);
  }

  @Override
  public void addLast(E element) {
    linkLast(element);
  }

  @Nullable
  @Override
  public E poll() {
    LinkedNode<E> first = firstNode();
    return first == null ? null : unlinkFirst(first);
  }

  @Nullable
  @Override
  public E pollFirst() {
    LinkedNode<E> first = firstNode();
    return first == null ? null : unlinkFirst(first);
  }

  @Nullable
  @Override
  public E pollLast() {
    LinkedNode<E> last = lastNode();
    return last == null ? null : unlinkLast(last);
  }

  @Override
  public E removeFirst() {
    LinkedNode<E> first = firstNode();
    if (first == null) {
      throw new NoSuchElementException();
    }
    return unlinkFirst(first);
  }

  @Override
  public E removeLast() {

    LinkedNode<E> last = lastNode();
    if (last == null) {
      throw new NoSuchElementException();
    }

    return unlinkLast(last);
  }

  @Override
  public boolean remove(@Nullable Object object) {
    if (object == null) {
      return false;
    }
    for (LinkedNode<E> node = firstNode(); node != null; node = node.next) {
      if (Objects.equals(object, node.item)) {
        unlink(node);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    for (Object object : collection) {
      if (!remove(object)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean removeFirstOccurrence(@Nullable Object object) {
    if (object == null) {
      return false;
    }
    for (LinkedNode<E> node = firstNode(); node != null; node = node.next) {
      if (Objects.equals(object, node.item)) {
        unlink(node);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean removeLastOccurrence(@Nullable Object object) {
    if (object == null) {
      return false;
    }
    for (LinkedNode<E> node = lastNode(); node != null; node = node.prev) {
      if (Objects.equals(object, node.item)) {
        unlink(node);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    for (LinkedNode<E> node = firstNode(); node != null;) {
      if (collection.contains(node.item)) {
        node = node.next();
        continue;
      }
      var toUnlink = node;
      node = node.next;
      unlink(toUnlink);
    }
    return true;
  }

  @Override
  public Iterator<E> iterator() {
    return new LinkedListIterator<>(this, LinkedListIterator.NEXT);
  }

  @Override
  public Iterator<E> descendingIterator() {
    return new LinkedListIterator<>(this, LinkedListIterator.PREV);
  }

  @Override
  public Object[] toArray() {

    Object[] result = new Object[size()];
    int index = 0;

    for (LinkedNode<E> node = firstNode(); node != null; node = node.next) {
      //noinspection DataFlowIssue
      result[index++] = node.item;
    }

    return result;
  }

  @Override
  public <T> T[] toArray(T[] container) {

    int size = size();

    if (container.length < size) {
      container = ArrayUtils.create(container, size);
    }

    int index = 0;

    for (LinkedNode<E> node = firstNode(); node != null; node = node.next) {
      //noinspection DataFlowIssue,unchecked
      container[index++] = (T) node.item;
    }

    return container;
  }
}
