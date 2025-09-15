package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.LinkedHashLongToRefEntry;
import javasabr.rlib.collections.dictionary.LongToRefDictionary;
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
public class ImmutableHashBasedLongToRefDictionary<V, E extends LinkedHashLongToRefEntry<V, E>>
    extends AbstractHashBasedLongToRefDictionary<V, E> {

  private static final LongToRefDictionary<?> EMPTY =
      new ImmutableHashBasedLongToRefDictionary<>(new DefaultLinkedHashLongToRefEntry[0], 0);

  public static <V> LongToRefDictionary<V> empty() {
    //noinspection unchecked
    return (LongToRefDictionary<V>) EMPTY;
  }

  @Nullable E[] entries;
  int size;

  @Override
  public boolean isEmpty() {
    return size < 1;
  }
}
