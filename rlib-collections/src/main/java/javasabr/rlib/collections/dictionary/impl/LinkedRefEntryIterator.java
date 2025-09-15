package javasabr.rlib.collections.dictionary.impl;

import java.util.Iterator;
import javasabr.rlib.collections.dictionary.RefEntry;
import javasabr.rlib.collections.dictionary.LinkedEntry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LinkedRefEntryIterator<V, E extends LinkedEntry<E> & RefEntry<V>> implements Iterator<@Nullable V> {

  final @Nullable E[] entries;

  @Nullable
  E next;

  int index;

  public LinkedRefEntryIterator(@Nullable E[] entries) {
    this.entries = entries;
    findNext();
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public V next() {
    @SuppressWarnings("DataFlowIssue")
    V value = next.value();
    findNext();
    return value;
  }

  private void findNext() {

    E candidate;
    if (next != null && (candidate = next.next()) != null) {
      next = candidate;
      return;
    }

    //noinspection StatementWithEmptyBody
    while (index < entries.length && (next = entries[index++]) == null);
  }
}
