package javasabr.rlib.reusable.pool.impl;

import javasabr.rlib.collections.array.ArrayFactory;
import javasabr.rlib.collections.array.MutableArray;
import javasabr.rlib.reusable.pool.Pool;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArrayBasedPool<E> implements Pool<E> {

  MutableArray<E> pool;

  public ArrayBasedPool(Class<? super E> type) {
    this.pool = ArrayFactory.mutableArray(type);
  }

  @Override
  public void put(E object) {
    pool.add(object);
  }

  @Nullable
  @Override
  public E take() {

    if (pool.isEmpty()) {
      return null;
    }

    return pool.remove(pool.size() - 1);
  }

  @Override
  public String toString() {
    return pool.toString();
  }
}
