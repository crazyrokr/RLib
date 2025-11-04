package javasabr.rlib.collections.dictionary.impl;

import java.util.Arrays;
import javasabr.rlib.collections.dictionary.LongToRefDictionary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultMutableHashBasedLongToRefDictionary<V> extends
    AbstractMutableHashBasedLongToRefDictionary<V, DefaultLinkedHashLongToRefEntry<V>> {

  @Nullable DefaultLinkedHashLongToRefEntry<V>[] entries;
  int size;
  int threshold;

  public DefaultMutableHashBasedLongToRefDictionary() {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public DefaultMutableHashBasedLongToRefDictionary(int initCapacity, float loadFactor) {
    super(loadFactor);
    //noinspection unchecked
    this.entries = new DefaultLinkedHashLongToRefEntry[initCapacity];
    this.threshold = (int) (initCapacity * loadFactor);
  }

  @Override
  public boolean isEmpty() {
    return size < 1;
  }

  @Override
  protected int incrementSize() {
    return size++;
  }

  @Override
  protected int decrementSize() {
    return size--;
  }

  @Override
  public void clear() {
    Arrays.fill(entries, null);
    size = 0;
  }

  @Override
  protected void threshold(int threshold) {
    this.threshold = threshold;
  }

  @Override
  protected void entries(@Nullable DefaultLinkedHashLongToRefEntry<V>[] entries) {
    this.entries = entries;
  }

  @Override
  protected DefaultLinkedHashLongToRefEntry<V> allocate(
      int hash,
      long key,
      @Nullable V value,
      @Nullable DefaultLinkedHashLongToRefEntry<V> next) {
    return new DefaultLinkedHashLongToRefEntry<>(next, key, value, hash);
  }

  @Nullable
  @Override
  protected DefaultLinkedHashLongToRefEntry<V>[] allocate(int length) {
    //noinspection unchecked
    return new DefaultLinkedHashLongToRefEntry[length];
  }

  @Override
  public LongToRefDictionary<V> toReadOnly() {
    @Nullable DefaultLinkedHashLongToRefEntry<V>[] copied = Arrays.copyOf(entries, entries.length);
    return new ImmutableHashBasedLongToRefDictionary<>(copied, size);
  }
}
