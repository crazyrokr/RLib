package javasabr.rlib.collections.dictionary;

public interface HashEntry<K, V> extends Entry<K, V>  {
  int hash();
}
