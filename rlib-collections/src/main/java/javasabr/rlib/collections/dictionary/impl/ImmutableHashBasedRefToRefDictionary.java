package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.LinkedHashEntry;
import javasabr.rlib.collections.dictionary.RefToRefDictionary;
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
public class ImmutableHashBasedRefToRefDictionary<K, V, E extends LinkedHashEntry<K, V, E>>
    extends AbstractHashBasedRefToRefDictionary<K, V, E> {

  private static final RefToRefDictionary<?, ?> EMPTY =
      new ImmutableHashBasedRefToRefDictionary<>(new DefaultLinkedHashEntry[0], 0);

  public static <K, V> RefToRefDictionary<K, V> empty() {
    //noinspection unchecked
    return (RefToRefDictionary<K, V>) EMPTY;
  }

  @Nullable E[] entries;
  int size;

  @Override
  public boolean isEmpty() {
    return size < 1;
  }
}
