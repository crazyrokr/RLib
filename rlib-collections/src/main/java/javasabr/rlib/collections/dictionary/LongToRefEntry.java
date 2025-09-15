package javasabr.rlib.collections.dictionary;

public interface LongToRefEntry<V> extends RefEntry<V> {
  long key();
  void key(long key);
}
