package javasabr.rlib.collections.array;

import java.io.Serializable;
import java.util.RandomAccess;
import java.util.stream.IntStream;
import javasabr.rlib.collections.array.impl.ImmutableIntArray;

/**
 * @author JavaSaBr
 */
public interface IntArray extends Iterable<Integer>, Serializable, Cloneable, RandomAccess {

  ImmutableIntArray EMPTY = new ImmutableIntArray();

  static IntArray empty() {
    return EMPTY;
  }

  static IntArray of(int e1) {
    return new ImmutableIntArray(e1);
  }

  static IntArray of(int e1, int e2) {
    return new ImmutableIntArray(e1, e2);
  }

  static IntArray of(int e1, int e2, int e3) {
    return new ImmutableIntArray(e1, e2, e3);
  }

  static IntArray of(int e1, int e2, int e3, int e4) {
    return new ImmutableIntArray(e1, e2, e3, e4);
  }

  static IntArray of(int... elements) {
    return new ImmutableIntArray(elements);
  }

  static IntArray copyOf(IntArray intArray) {
    return new ImmutableIntArray(intArray.toArray());
  }

  int size();

  boolean contains(int value);

  boolean containsAll(IntArray array);

  /**
   * @return the first element or {@link java.util.NoSuchElementException}
   */
  int first();

  int get(int index);

  /**
   * @return the last element or {@link java.util.NoSuchElementException}
   */
  int last();

  /**
   * @return the index of the value or -1.
   */
  int indexOf(int value);

  int lastIndexOf(int value);

  boolean isEmpty();

  int[] toArray();

  IntStream stream();

  UnsafeIntArray asUnsafe();
}
