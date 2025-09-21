package javasabr.rlib.collections.dictionary.impl;

import javasabr.rlib.collections.dictionary.IntToRefDictionary;
import javasabr.rlib.collections.dictionary.LinkedHashIntToRefEntry;
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
public class ImmutableHashBasedIntToRefDictionary<V, E extends LinkedHashIntToRefEntry<V, E>>
    extends AbstractHashBasedIntToRefDictionary<V, E> {

  private static final IntToRefDictionary<?> EMPTY =
      new ImmutableHashBasedIntToRefDictionary<>(new DefaultLinkedHashIntToRefEntry[0], 0);

  public static <V> IntToRefDictionary<V> empty() {
    //noinspection unchecked
    return (IntToRefDictionary<V>) EMPTY;
  }

  @Nullable E[] entries;
  int size;

  @Override
  public boolean isEmpty() {
    return size < 1;
  }
}
