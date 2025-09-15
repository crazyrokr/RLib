package javasabr.rlib.collections.dictionary;

import javasabr.rlib.collections.operation.LockableOperations;
import javasabr.rlib.collections.operation.LockableSource;
import javasabr.rlib.common.util.ThreadSafe;

public interface LockableRefToRefDictionary<K, V> extends MutableRefToRefDictionary<K, V>,
    LockableSource, ThreadSafe {

  LockableOperations<LockableRefToRefDictionary<K, V>> operations();
}
