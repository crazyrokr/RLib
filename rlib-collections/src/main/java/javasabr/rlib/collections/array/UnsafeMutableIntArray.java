package javasabr.rlib.collections.array;

public interface UnsafeMutableIntArray extends UnsafeIntArray, MutableIntArray {

  UnsafeMutableIntArray prepareForSize(int expectedSize);

  UnsafeMutableIntArray unsafeAdd(int value);

  int unsafeRemoveByInex(int index);

  UnsafeMutableIntArray unsafeSet(int index, int value);

  UnsafeMutableIntArray trimToSize();
}
