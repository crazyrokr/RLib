package javasabr.rlib.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import javasabr.rlib.concurrent.lock.impl.AtomicLock;
import javasabr.rlib.concurrent.lock.impl.AtomicAsyncReadSyncWriteLock;
import javasabr.rlib.concurrent.lock.impl.ReentrantARSWLock;
import javasabr.rlib.concurrent.lock.impl.ReentrantAtomicLock;

/**
 * @author JavaSaBr
 */
public class LockFactory {

  public static AsyncReadSyncWriteLock reentrantARSWLock() {
    return new ReentrantARSWLock();
  }

  public static StampedLock stampedLock() {
    return new StampedLock();
  }

  public static Lock reentrantLock() {
    return new ReentrantLock();
  }

  public static AsyncReadSyncWriteLock atomicARSWLock() {
    return new AtomicAsyncReadSyncWriteLock();
  }

  public static Lock atomicLock() {
    return new AtomicLock();
  }

  public static ReadWriteLock reentrantRWLock() {
    return new ReentrantReadWriteLock();
  }

  public static Lock reentrantAtomicLock() {
    return new ReentrantAtomicLock();
  }
}
