package javasabr.rlib.collections.dictionary.impl;

import java.util.Arrays;
import javasabr.rlib.collections.dictionary.RefDictionary;
import javasabr.rlib.common.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultMutableRefHashDictionary<K, V> extends
    AbstractMutableHashBasedRefDictionary<K, V, DefaultLinkedHashEntry<K, V>> {

  final float loadFactor;

  @Nullable DefaultLinkedHashEntry<K, V>[] entries;
  int size;
  int threshold;

  public DefaultMutableRefHashDictionary() {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public DefaultMutableRefHashDictionary(int initCapacity, float loadFactor) {
    this.entries = ArrayUtils.create(DefaultLinkedHashEntry.class, initCapacity);
    this.loadFactor = loadFactor;
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
  public RefDictionary<K, V> toReadOnly() {
    @Nullable DefaultLinkedHashEntry<K, V>[] copied = Arrays.copyOf(entries, entries.length);
    return new ImmutableHashBasedRefDictionary<>(copied, size);
  }
}
