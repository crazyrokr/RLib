package javasabr.rlib.collections.array;

import javasabr.rlib.collections.operation.LockableOperations;
import javasabr.rlib.collections.operation.LockableSource;
import javasabr.rlib.common.ThreadSafe;

public interface LockableArray<E> extends MutableArray<E>, LockableSource, ThreadSafe {

  LockableOperations<LockableArray<E>> operations();
}
