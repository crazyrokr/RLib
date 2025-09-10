package javasabr.rlib.reusable.pool.impl;

import javasabr.rlib.collections.array.ArrayFactory;
import javasabr.rlib.collections.array.LockableArray;
import javasabr.rlib.reusable.pool.Pool;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class LockableArrayBasePool<E> implements Pool<E> {

  LockableArray<E> pool;

  public LockableArrayBasePool(Class<? super E> type) {
    this.pool = ArrayFactory.stampedLockBasedArray(type);
  }

  @Override
  public void put(E object) {
    long stamp = pool.writeLock();
    try {
      pool.add(object);
    } finally {
      pool.writeUnlock(stamp);
    }
  }

  @Nullable
  @Override
  public E take() {

    if (pool.isEmpty()) {
      return null;
    }

    long stamp = pool.writeLock();
    try {

      if (pool.isEmpty()) {
        return null;
      }

      return pool.remove(pool.size() - 1);
    } finally {
      pool.writeUnlock(stamp);
    }
  }
}
