package javasabr.rlib.collections.dictionary;

public interface RefToRefEntry<K, V> extends RefEntry<V> {
  K key();
  void key(K key);
}
