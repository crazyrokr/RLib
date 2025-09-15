package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.LinkedHashIntToRefEntry;
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
public class DefaultLinkedHashIntToRefEntry<V> implements
    LinkedHashIntToRefEntry<V, DefaultLinkedHashIntToRefEntry<V>> {

  @Nullable
  DefaultLinkedHashIntToRefEntry<V> next;

  int key;
  @Nullable
  V value;

  int hash;

  public DefaultLinkedHashIntToRefEntry(
      @Nullable DefaultLinkedHashIntToRefEntry<V> next,
      int key,
      @Nullable V value,
      int hash) {
    this.next = next;
    this.key = key;
    this.value = value;
    this.hash = hash;
  }
}
