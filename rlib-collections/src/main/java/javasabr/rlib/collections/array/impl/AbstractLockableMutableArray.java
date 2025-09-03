package javasabr.rlib.collections.array.impl;

import java.util.concurrent.atomic.AtomicInteger;
import javasabr.rlib.collections.array.LockableMutableArray;
import javasabr.rlib.common.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractLockableMutableArray<E> extends AbstractMutableArray<E> implements
    LockableMutableArray<E> {

  protected static final int DEFAULT_CAPACITY = 10;

  final AtomicInteger size;

  @Getter
  @Setter(AccessLevel.PROTECTED)
  volatile @Nullable E[] wrapped;

  protected AbstractLockableMutableArray(Class<? super E> type) {
    this(type, DEFAULT_CAPACITY);
  }

  protected AbstractLockableMutableArray(Class<? super E> type, int capacity) {
    super(type);
    validateCapacity(capacity);
    this.wrapped = ArrayUtils.create(type, capacity);
    this.size = new AtomicInteger(0);
  }

  @Override
  public int size() {
    return size.get();
  }

  @Override
  protected void size(int size) {
    this.size.set(size);
  }

  @Override
  public boolean isEmpty() {
    return size.get() < 1;
  }

  @Override
  protected int getAndIncrementSize() {
    return size.getAndIncrement();
  }

  @Override
  protected int decrementAnGetSize() {
    return size.decrementAndGet();
  }
}
