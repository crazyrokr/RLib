package javasabr.rlib.collections.array;

public interface MutableLongArray extends LongArray {

  boolean add(long value);

  boolean addAll(LongArray array);

  boolean addAll(long[] array);

  /**
   * @return the element previously at the specified position
   */
  long removeByInex(int index);

  boolean remove(long value);

  void replace(int index, long value);

  void clear();

  @Override
  UnsafeMutableLongArray asUnsafe();
}
