package javasabr.rlib.collections.array;

import org.jspecify.annotations.Nullable;

public interface UnsafeMutableArray<E> extends UnsafeArray<E>, MutableArray<E> {

  UnsafeMutableArray<E> prepareForSize(int expectedSize);

  UnsafeMutableArray<E> unsafeAdd(E object);

  E unsafeRemove(int index);

  UnsafeMutableArray<E> unsafeSet(int index, E element);

  UnsafeMutableArray<E> trimToSize();
}
