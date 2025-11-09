package javasabr.rlib.collections.array;

public interface MutableIntArray extends IntArray {

  boolean add(int value);

  boolean addAll(IntArray array);

  boolean addAll(int[] array);

  /**
   * @return the element previously at the specified position
   */
  int removeByIndex(int index);

  boolean remove(int value);

  boolean removeAll(IntArray array);

  boolean removeAll(int[] array);

  void replace(int index, int value);

  void clear();

  @Override
  UnsafeMutableIntArray asUnsafe();
}
