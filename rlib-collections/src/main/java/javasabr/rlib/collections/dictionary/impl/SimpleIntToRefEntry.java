package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.IntToRefEntry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleIntToRefEntry<V> implements IntToRefEntry<V> {
  int key;
  @Nullable V value;
}
