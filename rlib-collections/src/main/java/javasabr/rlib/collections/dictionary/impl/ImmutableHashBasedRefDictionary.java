package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.LinkedHashEntry;
import javasabr.rlib.collections.dictionary.RefDictionary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImmutableHashBasedRefDictionary<K, V, E extends LinkedHashEntry<K, V, E>>
    extends AbstractHashBasedRefDictionary<K, V, E> {

  private static final RefDictionary<?, ?> EMPTY =
      new ImmutableHashBasedRefDictionary<>(new DefaultLinkedHashEntry[0], 0);

  public static <K, V> RefDictionary<K, V> empty() {
    //noinspection unchecked
    return (RefDictionary<K, V>) EMPTY;
  }

  @Nullable E[] entries;
  int size;

  @Override
  public boolean isEmpty() {
    return size < 1;
  }
}
