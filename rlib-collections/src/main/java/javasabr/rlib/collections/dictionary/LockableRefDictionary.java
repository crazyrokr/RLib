package javasabr.rlib.collections.dictionary;

import javasabr.rlib.collections.operation.LockableOperations;
import javasabr.rlib.collections.operation.LockableSource;
import javasabr.rlib.common.util.ThreadSafe;

public interface LockableRefDictionary<K, V> extends MutableRefDictionary<K, V>, LockableSource, ThreadSafe {

  LockableOperations<LockableRefDictionary<K, V>> operations();
}
