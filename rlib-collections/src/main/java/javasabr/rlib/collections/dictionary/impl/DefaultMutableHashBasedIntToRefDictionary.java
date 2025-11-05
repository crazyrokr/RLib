package javasabr.rlib.collections.dictionary.impl;

import java.util.Arrays;
import javasabr.rlib.collections.dictionary.IntToRefDictionary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultMutableHashBasedIntToRefDictionary<V> extends
    AbstractMutableHashBasedIntToRefDictionary<V, DefaultLinkedHashIntToRefEntry<V>> {

  @Nullable DefaultLinkedHashIntToRefEntry<V>[] entries;
  int size;
  int threshold;

  public DefaultMutableHashBasedIntToRefDictionary() {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public DefaultMutableHashBasedIntToRefDictionary(int initCapacity, float loadFactor) {
    super(loadFactor);
    //noinspection unchecked
    this.entries = new DefaultLinkedHashIntToRefEntry[initCapacity];
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
  protected void entries(@Nullable DefaultLinkedHashIntToRefEntry<V>[] entries) {
    this.entries = entries;
  }

  @Override
  protected DefaultLinkedHashIntToRefEntry<V> allocate(
      int hash,
      int key,
      @Nullable V value,
      @Nullable DefaultLinkedHashIntToRefEntry<V> next) {
    return new DefaultLinkedHashIntToRefEntry<>(next, key, value, hash);
  }

  @Nullable
  @Override
  protected DefaultLinkedHashIntToRefEntry<V>[] allocate(int length) {
    //noinspection unchecked
    return new DefaultLinkedHashIntToRefEntry[length];
  }

  @Override
  public IntToRefDictionary<V> toReadOnly() {
    @Nullable DefaultLinkedHashIntToRefEntry<V>[] copied = Arrays.copyOf(entries, entries.length);
    return new ImmutableHashBasedIntToRefDictionary<>(copied, size);
  }
}
