package javasabr.rlib.collections.array.impl;

import java.util.concurrent.locks.StampedLock;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class StampedLockBasedArray<E> extends AbstractLockableArray<E> {

  StampedLock stampedLock;

  public StampedLockBasedArray(Class<? super E> type) {
    this(type, DEFAULT_CAPACITY);
  }

  public StampedLockBasedArray(Class<? super E> type, int capacity) {
    super(type, capacity);
    this.stampedLock = new StampedLock();
  }

  @Override
  public long readLock() {
    return stampedLock.readLock();
  }

  @Override
  public void readUnlock(long stamp) {
    stampedLock.unlockRead(stamp);
  }

  @Override
  public long tryOptimisticRead() {
    return stampedLock.tryOptimisticRead();
  }

  @Override
  public boolean validateLock(long stamp) {
    return stampedLock.validate(stamp);
  }

  @Override
  public long writeLock() {
    return stampedLock.writeLock();
  }

  @Override
  public void writeUnlock(long stamp) {
    stampedLock.unlockWrite(stamp);
  }
}
