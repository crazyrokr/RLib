package javasabr.rlib.collections.dictionary;

public interface IntToRefEntry<V> extends RefEntry<V> {

  int key();
  void key(int key);
}
