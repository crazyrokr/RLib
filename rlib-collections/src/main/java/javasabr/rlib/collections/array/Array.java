package javasabr.rlib.collections.array;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.function.Function;
import java.util.stream.Stream;
import javasabr.rlib.collections.array.impl.DefaultArrayIterator;
import javasabr.rlib.collections.array.impl.ImmutableArray;
import javasabr.rlib.common.util.ClassUtils;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
public interface Array<E> extends Iterable<E>, Serializable, Cloneable, RandomAccess {

  static <E> Array<E> empty(Class<? super E> type) {
    return new ImmutableArray<>(ClassUtils.unsafeCast(type));
  }

  static <E> Array<E> of(E e1) {
    return new ImmutableArray<>(ClassUtils.unsafeCast(e1.getClass()), e1);
  }

  static <E> Array<E> of(E e1, E e2) {
    return new ImmutableArray<>(ClassUtils.unsafeCast(e1.getClass()), e1, e2);
  }

  static <E> Array<E> of(E e1, E e2, E e3) {
    return new ImmutableArray<>(ClassUtils.unsafeCast(e1.getClass()), e1, e2, e3);
  }

  static <E> Array<E> of(E e1, E e2, E e3, E e4) {
    return new ImmutableArray<>(ClassUtils.unsafeCast(e1.getClass()), e1, e2, e3, e4);
  }

  @SafeVarargs
  static <E> Array<E> of(Class<? super E> type, E... elements) {
    return new ImmutableArray<>(type, elements);
  }

  @SafeVarargs
  static <E> Array<E> optionals(Class<? super E> type, Optional<E>... elements) {
    return Stream
        .of(elements)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(ArrayCollectors.toArray(type));
  }

  static <E> Array<E> copyOf(MutableArray<E> array) {
    return new ImmutableArray<>(array.type(), array.toArray());
  }

  Class<E> type();

  /**
   * Returns the number of elements in this array.  If this array
   * contains more than {@code Integer.MAX_VALUE} elements, returns
   * {@code Integer.MAX_VALUE}.
   *
   * @return the number of elements in this array
   */
  int size();

  /**
   * Returns {@code true} if this array contains the specified element.
   * More formally, returns {@code true} if and only if this array
   * contains at least one element {@code e} such that
   * {@code Objects.equals(object, e)}.
   *
   * @param object element whose presence in this array is to be tested
   * @return {@code true} if this array contains the specified
   *         element
   * @throws ClassCastException if the type of the specified element
   *         is incompatible with this array
   *         ({@linkplain Collection##optional-restrictions optional})
   * @throws NullPointerException if the specified element is null and this
   *         array does not permit null elements
   *         ({@linkplain Collection##optional-restrictions optional})
   */
  boolean contains(Object object);

  /**
   * Returns {@code true} if this array contains all of the elements
   * in the specified array.
   *
   * @param  array array to be checked for containment in this collection
   * @return {@code true} if this array contains all of the elements
   *         in the specified array
   * @throws ClassCastException if the types of one or more elements
   *         in the specified array are incompatible with this
   *         array
   *         ({@linkplain Collection##optional-restrictions optional})
   * @throws NullPointerException if the specified array contains one
   *         or more null elements and this array does not permit null
   *         elements
   *         ({@linkplain Collection##optional-restrictions optional})
   *         or if the specified collection is null.
   * @see    #contains(Object)
   */
  boolean containsAll(Array<?> array);

  /**
   * Returns {@code true} if this array contains all of the elements
   * in the specified collection.
   *
   * @param  collection collection to be checked for containment in this array
   * @return {@code true} if this array contains all of the elements
   *         in the specified collection
   * @throws ClassCastException if the types of one or more elements
   *         in the specified collection are incompatible with this
   *         collection
   *         ({@linkplain Collection##optional-restrictions optional})
   * @throws NullPointerException if the specified collection contains one
   *         or more null elements and this array does not permit null
   *         elements
   *         ({@linkplain Collection##optional-restrictions optional})
   *         or if the specified collection is null.
   * @see    #contains(Object)
   */
  boolean containsAll(Collection<?> collection);

  /**
   * Returns {@code true} if this array contains all of the elements
   * in the specified array.
   *
   * @param  array array to be checked for containment in this collection
   * @return {@code true} if this array contains all of the elements
   *         in the specified array
   * @throws ClassCastException if the types of one or more elements
   *         in the specified array are incompatible with this
   *         array
   *         ({@linkplain Collection##optional-restrictions optional})
   * @throws NullPointerException if the specified array contains one
   *         or more null elements and this array does not permit null
   *         elements
   *         ({@linkplain Collection##optional-restrictions optional})
   *         or if the specified collection is null.
   * @see    #contains(Object)
   */
  boolean containsAll(Object[] array);

  /**
   * Gets the first element of this array.

   * @return the retrieved element or null
   */
  @Nullable E first();

  E get(int index);

  /**
   * Gets the last element of this array.

   * @return the retrieved element or null
   */
  @Nullable E last();

  @Override
  default Iterator<E> iterator() {
    return new DefaultArrayIterator<>(this);
  }

  /**
   * @return the index of the object or -1.
   */
  int indexOf(Object object);

  int lastIndexOf(Object object);

  <T > T[] toArray(T[] newArray);

  /**
   * Copy this array to the new array.
   *
   * @param <T> the type parameter
   * @param componentType the type of the new array.
   * @return the copied array.
   */
  <T> T[] toArray(Class<T> componentType);

  boolean isEmpty();

  E[] toArray();

  String toString(Function<E, String> toString);

  /**
   * Returns a sequential {@code Stream} with this array as its source.
   *
   * @return a sequential {@code Stream} over the elements in this array
   */
  Stream<E> stream();

  ReversedArrayIterationFunctions<E> reversedIterations();

  ArrayIterationFunctions<E> iterations();

  UnsafeArray<E> asUnsafe();
}
