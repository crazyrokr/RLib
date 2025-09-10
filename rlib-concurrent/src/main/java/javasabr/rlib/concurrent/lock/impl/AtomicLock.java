package javasabr.rlib.concurrent.lock.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.NullMarked;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AtomicLock implements Lock {

  private static final int STATUS_LOCKED = 1;
  private static final int STATUS_UNLOCKED = 0;

  final AtomicInteger status;

  int sink;

  public AtomicLock() {
    this.status = new AtomicInteger();
    this.sink = 1;
  }

  @Override
  public void lock() {
    while (!tryLock())
      consumeCPU();
  }

  /**
   * Consume cpu.
   */
  protected void consumeCPU() {

    final int value = sink;
    int newValue = value * value;
    newValue += value >>> 1;
    newValue += value & newValue;
    newValue += value ^ newValue;
    newValue += newValue << value;
    newValue += newValue | value;
    newValue += value & newValue;
    newValue += value ^ newValue;
    newValue += newValue << value;
    newValue += newValue | value;

    sink = newValue;
  }

  @Override
  public void lockInterruptibly() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Condition newCondition() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean tryLock() {
    return status.compareAndSet(STATUS_UNLOCKED, STATUS_LOCKED);
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unlock() {
    status.set(STATUS_UNLOCKED);
  }

  @Override
  public String toString() {
    return "AtomicLock{" + "status=" + status + '}';
  }
}
