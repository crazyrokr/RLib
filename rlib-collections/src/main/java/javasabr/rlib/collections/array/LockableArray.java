package javasabr.rlib.collections.array;

import javasabr.rlib.collections.operation.LockableSource;
import javasabr.rlib.common.ThreadSafe;

public interface LockableArray<E> extends MutableArray<E>, LockableSource, ThreadSafe {
}
