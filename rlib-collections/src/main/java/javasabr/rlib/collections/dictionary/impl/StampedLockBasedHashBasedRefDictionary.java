package javasabr.rlib.collections.dictionary.impl;

import java.util.concurrent.locks.StampedLock;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class StampedLockBasedHashBasedRefDictionary<K, V> extends AbstractLockableHashBasedRefDictionary<K, V> {

  StampedLock stampedLock;

  public StampedLockBasedHashBasedRefDictionary() {
    this.stampedLock = new StampedLock();
  }

  public StampedLockBasedHashBasedRefDictionary(int initCapacity, float loadFactor) {
    super(initCapacity, loadFactor);
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
