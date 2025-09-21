package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.LinkedHashLongToRefEntry;
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
public class DefaultLinkedHashLongToRefEntry<V> implements
    LinkedHashLongToRefEntry<V, DefaultLinkedHashLongToRefEntry<V>> {

  @Nullable
  DefaultLinkedHashLongToRefEntry<V> next;

  long key;
  @Nullable
  V value;

  int hash;

  public DefaultLinkedHashLongToRefEntry(
      @Nullable DefaultLinkedHashLongToRefEntry<V> next,
      long key,
      @Nullable V value,
      int hash) {
    this.next = next;
    this.key = key;
    this.value = value;
    this.hash = hash;
  }
}
