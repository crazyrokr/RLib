package javasabr.rlib.collections.dictionary.impl;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javasabr.rlib.collections.dictionary.LinkedHashEntry;
import javasabr.rlib.collections.dictionary.UnsafeMutableRefDictionary;
import org.jspecify.annotations.Nullable;

public abstract class AbstractMutableHashBasedRefDictionary<K, V, E extends LinkedHashEntry<K, V, E>>
    extends AbstractHashBasedRefDictionary<K, V, E> implements UnsafeMutableRefDictionary<K, V, E> {

  protected static final int DEFAULT_INITIAL_CAPACITY = 16;
  protected static final int DEFAULT_MAXIMUM_CAPACITY = 1 << 30;
  protected static final float DEFAULT_LOAD_FACTOR = 0.75f;

  @Nullable
  @Override
  public V getOrCompute(K key, Function<K, V> factory) {

    E entry = findEntry(key);
    if (entry != null) {
      return entry.value();
    }

    V newValue = factory.apply(key);
    put(key, newValue);
    return newValue;
  }

  @Nullable
  @Override
  public V getOrCompute(K key, Supplier<V> factory) {
    E entry = findEntry(key);
    if (entry != null) {
      return entry.value();
    }

    V newValue = factory.get();
    put(key, newValue);
    return newValue;
  }

  @Nullable
  @Override
  public <T> V getOrCompute(K key, T arg1, Function<T, V> factory) {
    E entry = findEntry(key);
    if (entry != null) {
      return entry.value();
    }

    V newValue = factory.apply(arg1);
    put(key, newValue);
    return newValue;
  }

  @Nullable
  @Override
  public V put(K key, V value) {

    @Nullable E[] entries = entries();
    int hash = hash(key.hashCode());
    int entryIndex = indexFor(hash, entries.length);

    for (E entry = entries[entryIndex]; entry != null; entry = entry.next()) {
      if (entry.hash() == hash && key.equals(entry.key())) {
        V prev = entry.value();
        entry.value(value);
        return prev;
      }
    }

    addEntry(hash, key, value, entryIndex);
    return null;
  }

  @Override
  public Optional<V> putOptional(K key, V value) {
    return Optional.ofNullable(put(key, value));
  }

  @Nullable
  @Override
  public V remove(K key) {
    E removed = removeEntryForKey(key);
    if (removed == null) {
      return null;
    }
    return removed.value();
  }

  @Override
  public Optional<V> removeOptional(K key) {
    return Optional.ofNullable(remove(key));
  }

  @Nullable
  protected E removeEntryForKey(K key) {

    @Nullable E[] entries = entries();
    int hash = hash(key.hashCode());
    int entryIndex = indexFor(hash, entries.length);

    E previosEntry = entries[entryIndex];
    E entry = previosEntry;

    while (entry != null) {
      E nextEntry = entry.next();
      if (entry.hash() == hash && key.equals(entry.key())) {
        decrementSize();
        if (previosEntry == entry) {
          entries[entryIndex] = nextEntry;
        } else {
          previosEntry.next(nextEntry);
        }
        return entry;
      }
      previosEntry = entry;
      entry = nextEntry;
    }

    return null;
  }

  protected void addEntry(int hash, K key, @Nullable V value, int index) {

    @Nullable E[] entries = entries();
    E currentEntry = entries[index];

    var newEntry = allocate(hash, key, value, currentEntry);
    entries[index] = newEntry;

    if (incrementSize() >= threshold()) {
      resize(2 * entries.length);
    }
  }

  protected abstract int threshold();
  protected abstract void threshold(int threshold);

  protected abstract float loadFactor();

  protected abstract int incrementSize();
  protected abstract int decrementSize();

  protected abstract void entries(@Nullable E[] entries);

  protected abstract E allocate(int hash, K key, @Nullable V value, @Nullable E next);

  @Nullable
  protected abstract E[] allocate(int length);

  protected final void resize(int newLength) {

    @Nullable E[] currentEntries = entries();
    int oldLength = currentEntries.length;

    if (oldLength >= DEFAULT_MAXIMUM_CAPACITY) {
      threshold(Integer.MAX_VALUE);
      return;
    }

    @Nullable E[] newEntries = allocate(newLength);

    transfer(currentEntries, newEntries);
    entries(newEntries);
    threshold((int) (newLength * loadFactor()));
  }

  protected void transfer(
      @Nullable E[] currentEntries,
      @Nullable E[] newEntries) {

    int newCapacity = newEntries.length;

    for (E entry : currentEntries) {
      if (entry == null) {
        continue;
      }
      do {

        E nextEntry = entry.next();
        int newIndex = indexFor(entry.hash(), newCapacity);

        entry.next(newEntries[newIndex]);
        newEntries[newIndex] = entry;
        entry = nextEntry;

      } while (entry != null);
    }
  }
}
