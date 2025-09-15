package javasabr.rlib.collections.dictionary.impl;

import java.util.Arrays;
import javasabr.rlib.collections.dictionary.RefToRefDictionary;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultMutableHashBasedRefToRefDictionary<K, V> extends
    AbstractMutableHashBasedRefToRefDictionary<K, V, DefaultLinkedHashEntry<K, V>> {

  @Nullable DefaultLinkedHashEntry<K, V>[] entries;
  int size;
  int threshold;

  public DefaultMutableHashBasedRefToRefDictionary() {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public DefaultMutableHashBasedRefToRefDictionary(int initCapacity, float loadFactor) {
    super(loadFactor);
    //noinspection unchecked
    this.entries = new DefaultLinkedHashEntry[initCapacity];
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
  protected void threshold(int threshold) {
    this.threshold = threshold;
  }

  @Override
  protected void entries(@Nullable DefaultLinkedHashEntry<K, V>[] entries) {
    this.entries = entries;
  }

  @Override
  protected DefaultLinkedHashEntry<K, V> allocate(
      int hash,
      K key,
      @Nullable V value,
      @Nullable DefaultLinkedHashEntry<K, V> next) {
    return new DefaultLinkedHashEntry<>(next, key, value, hash);
  }

  @Nullable
  @Override
  protected DefaultLinkedHashEntry<K, V>[] allocate(int length) {
    //noinspection unchecked
    return new DefaultLinkedHashEntry[length];
  }

  @Override
  public RefToRefDictionary<K, V> toReadOnly() {
    @Nullable DefaultLinkedHashEntry<K, V>[] copied = Arrays.copyOf(entries, entries.length);
    return new ImmutableHashBasedRefToRefDictionary<>(copied, size);
  }
}
