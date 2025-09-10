package javasabr.rlib.collections.deque.impl;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultLinkedListBasedDeque<E> extends AbstractLinkedListBasedDeque<E> {

  @Nullable
  @Getter(AccessLevel.PROTECTED)
  LinkedNode<E> firstNode;

  @Nullable
  @Getter(AccessLevel.PROTECTED)
  LinkedNode<E> lastNode;

  @Getter
  int size;

  @Nullable
  @Override
  protected E unlink(LinkedNode<E> node) {

    E item = node.item;
    LinkedNode<E> next = node.next;
    LinkedNode<E> prev = node.prev;

    if (prev == null) {
      firstNode = next;
    } else {
      prev.next = next;
      node.prev = null;
    }

    if (next == null) {
      lastNode = prev;
    } else {
      next.prev = prev;
      node.next = null;
    }

    size--;
    return item;
  }

  @Nullable
  @Override
  protected E unlinkFirst(LinkedNode<E> node) {

    E item = node.item;
    LinkedNode<E> next = node.next;

    firstNode = next;
    if (next == null) {
      lastNode = null;
    } else {
      next.prev = null;
    }

    node.next = null;
    node.item = null;

    size--;
    return item;
  }

  @Nullable
  @Override
  protected E unlinkLast(LinkedNode<E> node) {

    E item = node.item;
    LinkedNode<E> prev = node.prev;

    lastNode = prev;
    if (prev == null) {
      firstNode = null;
    } else {
      prev.next = null;
    }

    node.prev = null;
    node.item = null;

    size--;
    return item;
  }

  @Override
  protected void linkFirst(E item) {
    Objects.requireNonNull(item);

    LinkedNode<E> first = firstNode();
    LinkedNode<E> linked = allocateNode(item, null, first);

    this.firstNode = linked;

    if (first == null) {
      this.lastNode = linked;
    } else {
      first.prev = linked;
    }

    size++;
  }

  @Override
  protected void linkLast(E item) {
    Objects.requireNonNull(item);

    LinkedNode<E> last = lastNode();
    LinkedNode<E> linked = allocateNode(item, last, null);

    this.lastNode = linked;

    if (last == null) {
      this.firstNode = linked;
    } else {
      last.next = linked;
    }

    size++;
  }

  protected LinkedNode<E> allocateNode(
      @Nullable E item,
      @Nullable LinkedNode<E> prev,
      @Nullable LinkedNode<E> next) {
    return new LinkedNode<>(item, prev, next);
  }

  @Override
  public void clear() {
    this.firstNode = null;
    this.lastNode = null;
    this.size = 0;
  }
}
