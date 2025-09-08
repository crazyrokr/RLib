package javasabr.rlib.collections.dictionary;

import javasabr.rlib.collections.operation.LockableOperations;
import javasabr.rlib.collections.operation.LockableSource;

public interface LockableMutableRefDictionary<K, V> extends MutableRefDictionary<K, V>, LockableSource {

  LockableOperations<LockableMutableRefDictionary<K, V>> operations();
}
