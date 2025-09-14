package javasabr.rlib.collections.array;

public interface UnsafeLongArray extends LongArray {
  long[] wrapped();
  long unsafeGet(int index);
}
