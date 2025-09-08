package javasabr.rlib.collections.operation;

public interface LockableSource {

  long readLock();
  void readUnlock(long stamp);
  long tryOptimisticRead();

  boolean validateLock(long stamp);

  long writeLock();
  void writeUnlock(long stamp);
}
