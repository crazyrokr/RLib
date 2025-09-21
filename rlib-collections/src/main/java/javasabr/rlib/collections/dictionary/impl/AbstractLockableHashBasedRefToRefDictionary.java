package javasabr.rlib.collections.dictionary.impl;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javasabr.rlib.collections.dictionary.LockableRefToRefDictionary;
import javasabr.rlib.collections.dictionary.RefToRefDictionary;
import javasabr.rlib.collections.operation.LockableOperations;
import javasabr.rlib.collections.operation.impl.DefaultLockableOperations;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractLockableHashBasedRefToRefDictionary<K, V> extends
    AbstractMutableHashBasedRefToRefDictionary<K, V, DefaultLinkedHashEntry<K, V>> implements
    LockableRefToRefDictionary<K, V> {

  final AtomicReference<@Nullable DefaultLinkedHashEntry<K, V>[]> entries;
  final AtomicInteger size;

  int threshold;

  public AbstractLockableHashBasedRefToRefDictionary() {
    this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
  }

  public AbstractLockableHashBasedRefToRefDictionary(int initCapacity, float loadFactor) {
    super(loadFactor);
    //noinspection unchecked,rawtypes
    this.entries = new AtomicReference<>(new DefaultLinkedHashEntry[initCapacity]);
    this.size = new AtomicInteger();
    this.threshold = (int) (initCapacity * loadFactor);
  }

  @Override
  public boolean isEmpty() {
    return size.get() < 1;
  }

  @Override
  public int size() {
    return size.get();
  }

  @Override
  protected int incrementSize() {
    return size.incrementAndGet();
  }

  @Override
  protected int decrementSize() {
    return size.decrementAndGet();
  }

  @Override
  protected void threshold(int threshold) {
    this.threshold = threshold;
  }

  @Override
  protected int threshold() {
    return threshold;
  }

  @Override
  protected void entries(@Nullable DefaultLinkedHashEntry<K, V>[] entries) {
    this.entries.set(entries);
  }

  @Override
  public @Nullable DefaultLinkedHashEntry<K, V>[] entries() {
    return entries.get();
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
    @Nullable DefaultLinkedHashEntry<K, V>[] currentEntries = entries.get();
    @Nullable DefaultLinkedHashEntry<K, V>[] copied = Arrays.copyOf(currentEntries, currentEntries.length);
    return new ImmutableHashBasedRefToRefDictionary<>(copied, size.get());
  }

  @Override
  public LockableOperations<LockableRefToRefDictionary<K, V>> operations() {
    return new DefaultLockableOperations<>(this);
  }
}
