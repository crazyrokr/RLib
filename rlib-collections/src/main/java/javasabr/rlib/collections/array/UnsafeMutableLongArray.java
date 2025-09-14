package javasabr.rlib.collections.array;

public interface UnsafeMutableLongArray extends UnsafeLongArray, MutableLongArray {

  UnsafeMutableLongArray prepareForSize(int expectedSize);

  UnsafeMutableLongArray unsafeAdd(long value);

  long unsafeRemoveByInex(int index);

  UnsafeMutableLongArray unsafeSet(int index, long value);

  UnsafeMutableLongArray trimToSize();
}
