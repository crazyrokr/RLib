package javasabr.rlib.collections.array;

import java.io.Serializable;
import java.util.RandomAccess;
import java.util.stream.LongStream;
import javasabr.rlib.collections.array.impl.ImmutableLongArray;

/**
 * @author JavaSaBr
 */
public interface LongArray extends Iterable<Long>, Serializable, Cloneable, RandomAccess {

  ImmutableLongArray EMPTY = new ImmutableLongArray();

  static LongArray empty() {
    return EMPTY;
  }

  static LongArray of(long e1) {
    return new ImmutableLongArray(e1);
  }

  static LongArray of(long e1, long e2) {
    return new ImmutableLongArray(e1, e2);
  }

  static LongArray of(long e1, long e2, long e3) {
    return new ImmutableLongArray(e1, e2, e3);
  }

  static LongArray of(long e1, long e2, long e3, long e4) {
    return new ImmutableLongArray(e1, e2, e3, e4);
  }

  static LongArray of(long... elements) {
    return new ImmutableLongArray(elements);
  }

  static LongArray copyOf(LongArray intArray) {
    return new ImmutableLongArray(intArray.toArray());
  }

  int size();

  boolean contains(long value);

  boolean containsAll(LongArray array);

  /**
   * @return the first element or {@link java.util.NoSuchElementException}
   */
  long first();

  long get(int index);

  /**
   * @return the last element or {@link java.util.NoSuchElementException}
   */
  long last();

  /**
   * @return the index of the value or -1.
   */
  int indexOf(long value);

  int lastIndexOf(long value);

  boolean isEmpty();

  long[] toArray();

  LongStream stream();

  UnsafeLongArray asUnsafe();
}
