package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.Entry;
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
public class SimpleEntry<K, V> implements Entry<K, V> {
  K key;
  @Nullable V value;
}
