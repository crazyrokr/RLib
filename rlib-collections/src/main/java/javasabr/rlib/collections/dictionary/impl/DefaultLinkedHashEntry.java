package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.LinkedHashEntry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultLinkedHashEntry<K, V> implements
    LinkedHashEntry<K, V, DefaultLinkedHashEntry<K, V>> {

  @Nullable
  DefaultLinkedHashEntry<K, V> next;

  K key;
  @Nullable
  V value;

  int hash;

  public DefaultLinkedHashEntry(
      @Nullable DefaultLinkedHashEntry<K, V> next,
      K key,
      @Nullable V value,
      int hash) {
    this.next = next;
    this.key = key;
    this.value = value;
    this.hash = hash;
  }

  @Override
  public void next(@Nullable DefaultLinkedHashEntry<K, V> next) {
    this.next = next;
  }

  @Override
  public void key(K key) {
    this.key = key;
  }

  @Override
  public void value(@Nullable V value) {
    this.value = value;
  }
}
