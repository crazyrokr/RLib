package javasabr.rlib.collections.array;

import javasabr.rlib.common.util.ThreadSafe;

public interface LockableMutableArray<E> extends MutableArray<E>, ThreadSafe {

  long readLock();
  void readUnlock(long stamp);
  long tryOptimisticRead();

  boolean validateLock(long stamp);

  long writeLock();
  void writeUnlock(long stamp);
}
