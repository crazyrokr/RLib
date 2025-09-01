package javasabr.rlib.collections.array.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.collections.array.UnsafeArray;
import javasabr.rlib.collections.array.UnsafeMutableArray;
import javasabr.rlib.common.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractMutableArray<E> extends AbstractArray<E> implements UnsafeMutableArray<E> {

  protected AbstractMutableArray(Class<? super E> type) {
    super(type);
  }

  @Override
  public boolean add(E element) {
    Objects.requireNonNull(element);
    prepareForSize(size() + 1);
    unsafeAdd(element);
    return true;
  }

  @Override
  public boolean addAll(Array<? extends E> array) {
    if (array.isEmpty()) {
      return false;
    }
    int size = size();
    int elementsToAdd = array.size();
    prepareForSize(size + elementsToAdd);
    processAdd(array, size, elementsToAdd);
    return true;
  }

  @Override
  public boolean addAll(E[] array) {
    if (array.length < 1) {
      return false;
    }
    int size = size();
    int elementsToAdd = array.length;
    prepareForSize(size + elementsToAdd);
    processAdd(array, size, elementsToAdd);
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends E> collection) {
    if (collection.isEmpty()) {
      return false;
    }
    int size = size();
    int elementsToAdd = collection.size();
    prepareForSize(size + elementsToAdd);
    for (E element : collection) {
      Objects.requireNonNull(element);
      unsafeAdd(element);
    }
    return true;
  }

  @Override
  public void replace(int index, E element) {
    checkIndex(index);
    unsafeSet(index, element);
  }

  @Override
  public E remove(int index) {
    checkIndex(index);
    return unsafeRemove(index);
  }

  @Override
  public boolean remove(Object element) {
    int index = indexOf(element);
    if (index < 0) {
      return false;
    }
    remove(index);
    return true;
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    if (collection.isEmpty()) {
      return false;
    }
    int removed = 0;
    for (Object element : collection) {
      if (remove(element)) {
        removed++;
      }
    }
    return removed > 0;
  }

  @Override
  public boolean retainAll(Collection<?> collection) {

    if (collection.isEmpty()) {
      int size = size();
      clear();
      return size > 0;
    }

    int removed = 0;
    @Nullable E[] wrapped = wrapped();

    for (int i = 0, length = size(); i < length; i++) {
      if (!collection.contains(wrapped[i])) {
        unsafeRemove(i);
        length--;
        removed++;
      }
    }

    return removed > 0;
  }

  @Override
  public void clear() {
    if (isEmpty()) {
      return;
    }
    Arrays.fill(wrapped(), 0, size(), null);
    size(0);
  }

  @Override
  public Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(wrapped(), 0, size(), Spliterator.NONNULL);
  }

  protected void processAdd(Array<? extends E> array, int currentSize, int elementsToAdd) {
    System.arraycopy(array.asUnsafe().wrapped(), 0, wrapped(), currentSize, elementsToAdd);
    size(currentSize + elementsToAdd);
  }

  protected void processAdd(E[] array, int currentSize, int elementsToAdd) {
    System.arraycopy(array, 0, wrapped(), currentSize, elementsToAdd);
    size(currentSize + elementsToAdd);
  }

  protected abstract void size(int size);

  protected abstract void wrapped(@Nullable E[] wrapped);

  @Override
  public UnsafeMutableArray<E> asUnsafe() {
    return this;
  }
}
