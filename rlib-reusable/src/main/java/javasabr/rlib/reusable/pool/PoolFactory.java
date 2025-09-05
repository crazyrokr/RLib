package javasabr.rlib.reusable.pool;

import javasabr.rlib.reusable.Reusable;
import javasabr.rlib.reusable.pool.impl.ArrayBasedPool;
import javasabr.rlib.reusable.pool.impl.ArrayBasedReusablePool;
import javasabr.rlib.reusable.pool.impl.LockableArrayBasePool;

public class PoolFactory {

  public static <T> Pool<T> newPool(Class<? super T> type) {
    return new ArrayBasedPool<>(type);
  }

  public static <T extends Reusable> ReusablePool<T> newReusablePool(Class<? super T> type) {
    return new ArrayBasedReusablePool<>(type);
  }

  public static <T> Pool<T> newLockBasePool(Class<? super T> type) {
    return new LockableArrayBasePool<>(type);
  }
}
