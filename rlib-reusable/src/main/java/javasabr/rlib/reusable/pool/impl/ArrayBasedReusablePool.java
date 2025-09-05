package javasabr.rlib.reusable.pool.impl;

import javasabr.rlib.reusable.Reusable;
import javasabr.rlib.reusable.pool.ReusablePool;

/**
 * @author JavaSaBr
 */
public class ArrayBasedReusablePool<E extends Reusable> extends ArrayBasedPool<E>
    implements ReusablePool<E> {

  public ArrayBasedReusablePool(Class<? super E> type) {
    super(type);
  }

  public void put(E object) {
    object.cleanup();
    super.put(object);
  }
}
