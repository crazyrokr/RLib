package javasabr.rlib.concurrent.lock.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ReentrantAtomicLock implements Lock {

  final AtomicReference<@Nullable Thread> status;
  final AtomicInteger level;

  int sink;

  public ReentrantAtomicLock() {
    this.status = new AtomicReference<>();
    this.level = new AtomicInteger();
    this.sink = 1;
  }

  @Override
  public void lock() {
    Thread thread = Thread.currentThread();
    try {
      if (status.get() == thread) {
        return;
      }
      while (!status.compareAndSet(null, thread)) {
        consumeCPU();
      }
    } finally {
      level.incrementAndGet();
    }
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

    Thread currentThread = Thread.currentThread();

    if (status.get() == currentThread) {
      level.incrementAndGet();
      return true;
    }

    if (status.compareAndSet(null, currentThread)) {
      level.incrementAndGet();
      return true;
    }

    return false;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unlock() {

    Thread thread = Thread.currentThread();
    if (status.get() != thread) {
      return;
    }

    if (level.decrementAndGet() == 0) {
      status.set(null);
    }
  }
}
