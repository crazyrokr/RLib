package javasabr.rlib.concurrent.lock.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import javasabr.rlib.concurrent.lock.AsyncReadSyncWriteLock;
import javasabr.rlib.concurrent.lock.LockFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public final class ReentrantARSWLock implements AsyncReadSyncWriteLock {

  Lock readLock;
  Lock writeLock;

  public ReentrantARSWLock() {
    ReadWriteLock readWriteLock = LockFactory.reentrantRWLock();
    readLock = readWriteLock.readLock();
    writeLock = readWriteLock.writeLock();
  }

  @Override
  public void asyncLock() {
    readLock.lock();
  }

  @Override
  public void asyncUnlock() {
    readLock.unlock();
  }

  @Override
  public void syncLock() {
    writeLock.lock();
  }

  @Override
  public void syncUnlock() {
    writeLock.unlock();
  }
}
