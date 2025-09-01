package javasabr.rlib.collections.array.impl;

import static javasabr.rlib.common.util.ClassUtils.unsafeCast;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.collections.array.ArrayIterationFunctions;
import javasabr.rlib.collections.array.ReversedArrayIterationFunctions;
import javasabr.rlib.collections.array.UnsafeArray;
import javasabr.rlib.common.util.ArrayUtils;
import javasabr.rlib.common.util.ClassUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractArray<E> implements UnsafeArray<E> {

  @Getter
  final Class<E> type;

  protected AbstractArray(Class<? super E> type) {
    this.type = ClassUtils.unsafeCast(type);
  }

  @Nullable
  @Override
  public E first() {
    return isEmpty() ? null : get(0);
  }

  @Nullable
  @Override
  public E last() {
    int size = size();
    if (size < 1) {
      return null;
    }
    return get(size - 1);
  }

  @Override
  public E get(int index) {
    checkIndex(index);
    return unsafeGet(index);
  }

  protected void checkIndex(int index) {
    if (index < 0 || index >= size()) {
      throw new ArrayIndexOutOfBoundsException();
    }
  }

  @Override
  public boolean contains(@Nullable Object object) {
    if (object == null) {
      return false;
    }
    @Nullable E[] wrapped = wrapped();
    for (int i = 0, length = size(); i < length; i++) {
      if (object.equals(wrapped[i])) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsAll(Array<?> array) {
    if (array.isEmpty()) {
      return false;
    }

    @Nullable Object[] wrapped = array
        .asUnsafe()
        .wrapped();

    for (int i = 0, length = array.size(); i < length; i++) {
      if (!contains(wrapped[i])) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    if (collection.isEmpty()) {
      return false;
    }
    for (var element : collection) {
      if (!contains(element)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean containsAll(Object[] array) {
    if (array.length < 1) {
      return false;
    }
    for (Object element : array) {
      if (!contains(element)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int indexOf(@Nullable Object object) {
    if (object == null) {
      return -1;
    }
    @Nullable E[] wrapped = wrapped();
    for (int i = 0, length = size(); i < length; i++) {
      if (object.equals(wrapped[i])) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int lastIndexOf(@Nullable Object object) {
    if (object == null) {
      return -1;
    }
    @Nullable E[] wrapped = wrapped();
    for (int i = size() - 1; i >= 0; i--) {
      if (object.equals(wrapped[i])) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public void forEach(Consumer<? super E> action) {
    @Nullable E[] wrapped = wrapped();
    for (int i = 0, limit = size(); i < limit; i++) {
      action.accept(wrapped[i]);
    }
  }

  @Override
  public ReversedArrayIterationFunctions<E> reversedIterations() {
    return new DefaultReversedArrayIterationFunctions<>(this);
  }

  @Override
  public ArrayIterationFunctions<E> iterations() {
    return new DefaultArrayIterationFunctions<>(this);
  }

  @Override
  public <T> T[] toArray(T[] newArray) {

    @Nullable E[] array = wrapped();

    if (newArray.length >= size()) {
      for (int i = 0, j = 0, length = size(), newLength = newArray.length; i < length && j < newLength; i++) {
        newArray[j++] = (T) array[i];
      }
      return newArray;
    }

    Class<T[]> arrayClass = unsafeCast(newArray.getClass());
    Class<T> componentType = unsafeCast(arrayClass.getComponentType());

    return toArray(componentType);
  }

  @Override
  public <T> T[] toArray(Class<T> componentType) {
    T[] newArray = ArrayUtils.create(componentType, size());
    @Nullable E[] array = wrapped();
    System.arraycopy(array, 0, newArray, 0, size());
    return newArray;
  }

  @Override
  public E[] toArray() {
    @Nullable E[] wrapped = wrapped();
    return Arrays.copyOf(wrapped, size(), (Class<E[]>) wrapped.getClass());
  }

  @Override
  public String toString(Function<E, String> toString) {
    return ArrayUtils.toString(wrapped(), size(), toString);
  }

  @Override
  public UnsafeArray<E> asUnsafe() {
    return this;
  }
}
