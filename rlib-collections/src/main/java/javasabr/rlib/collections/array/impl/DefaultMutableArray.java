package javasabr.rlib.collections.array.impl;

import java.util.Arrays;
import javasabr.rlib.collections.array.UnsafeMutableArray;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

@Getter
@Accessors(fluent = true)
public class DefaultMutableArray<E> extends AbstractMutableArray<E> {

  /*
    It's initialized during object construction by setter #wrapped
   */
  @SuppressWarnings("NotNullFieldNotInitialized")
  @Nullable
  E[] wrapped;
  int size;

  public DefaultMutableArray(Class<? super E> type) {
    super(type);
  }

  public DefaultMutableArray(Class<? super E> type, int capacity) {
    super(type, capacity);
  }

  @Override
  protected void size(int size) {
    this.size = size;
  }

  @Override
  public boolean isEmpty() {
    return size < 1;
  }

  @Override
  protected void wrapped(@Nullable E[] wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public UnsafeMutableArray<E> unsafeAdd(E element) {
    wrapped[size++] = element;
    return this;
  }

  @Override
  public UnsafeMutableArray<E> unsafeSet(int index, E element) {
    wrapped[index] = element;
    return this;
  }

  @Override
  public E unsafeRemove(int index) {

    int numMoved = size - index - 1;
    E element = wrapped[index];

    if (numMoved > 0) {
      System.arraycopy(wrapped, index + 1, wrapped, index, numMoved);
    }

    size -= 1;
    wrapped[size] = null;

    //noinspection DataFlowIssue
    return element;
  }

  @Override
  public E unsafeGet(int index) {
    //noinspection DataFlowIssue
    return wrapped[index];
  }

  @Override
  public UnsafeMutableArray<E> prepareForSize(int expectedSize) {
    if (expectedSize > wrapped.length) {
      int newLength = Math.max((wrapped.length * 3) / 2, expectedSize);
      wrapped = Arrays.copyOf(wrapped, newLength);
    }
    return this;
  }

  @Override
  public UnsafeMutableArray<E> trimToSize() {
    if (size == wrapped.length) {
      return this;
    }
    wrapped = Arrays.copyOfRange(wrapped, 0, size);
    return this;
  }
}
