package javasabr.rlib.common.util.array;

import static javasabr.rlib.common.util.ClassUtils.unsafeNNCast;

import javasabr.rlib.common.util.ArrayUtils;
import javasabr.rlib.common.util.array.impl.ConcurrentStampedLockArray;
import javasabr.rlib.common.util.array.impl.CopyOnModifyArray;
import javasabr.rlib.common.util.array.impl.FastArray;
import javasabr.rlib.common.util.array.impl.ReadOnlyFastArray;
import org.jspecify.annotations.NullMarked;

/**
 * The factory to create different kinds of arrays.
 *
 * @author JavaSaBr
 */
@NullMarked
public class ArrayFactory {

  public static final Array<?> EMPTY_ARRAY = newReadOnlyArray(ArrayUtils.EMPTY_OBJECT_ARRAY);

  @SafeVarargs
  public static <E> Array<E> asArray(E... args) {
    return new FastArray<>(args);
  }

  public static <E> Array<E> newArray(Class<? super E> type) {
    return newUnsafeArray(type);
  }

  public static <E> Array<E> newArray(Class<? super E> type, int capacity) {
    return newUnsafeArray(type, capacity);
  }

  public static <E> Array<E> newCopyOnModifyArray(Class<? super E> type) {
    return new CopyOnModifyArray<>(type, 0);
  }

  public static <E> UnsafeArray<E> newUnsafeArray(Class<? super E> type) {
    return new FastArray<>(type);
  }

  public static <E> UnsafeArray<E> newUnsafeArray(Class<? super E> type, int capacity) {
    return new FastArray<>(type, capacity);
  }

  public static <E> ReadOnlyArray<E> newReadOnlyArray(E[] elements) {
    return new ReadOnlyFastArray<>(elements);
  }

  public static <E> ConcurrentArray<E> newConcurrentStampedLockArray(Class<? super E> type) {
    return new ConcurrentStampedLockArray<>(type);
  }

  public static <E> ConcurrentArray<E> newConcurrentStampedLockArray(E [] array) {

    Class<? super E> type = unsafeNNCast(array
        .getClass()
        .getComponentType());

    var result = new ConcurrentStampedLockArray<E>(type, array.length);
    result.addAll(array);

    return result;
  }

  public static float[] toFloatArray(float... elements) {
    return elements;
  }

  public static int[] toIntArray(int... elements) {
    return elements;
  }

  public static long[] toLongArray(long... elements) {
    return elements;
  }

  public static boolean[] toBooleanArray(boolean... elements) {
    return elements;
  }

  @SafeVarargs
  public static <T, K extends T> T[] toArray(K... elements) {
    return elements;
  }
}
