package javasabr.rlib.collections.array;

public interface UnsafeIntArray extends IntArray {
  int[] wrapped();
  int unsafeGet(int index);
}
