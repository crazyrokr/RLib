package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.LinkedHashEntry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@Accessors(fluent = true, chain = false)
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
}
