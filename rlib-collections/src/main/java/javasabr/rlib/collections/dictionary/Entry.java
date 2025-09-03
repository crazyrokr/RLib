package javasabr.rlib.collections.dictionary;

import org.jspecify.annotations.Nullable;

public interface Entry<K, V>  {

  K key();
  void key(K key);

  @Nullable V value();
  void value(@Nullable V value);
}
