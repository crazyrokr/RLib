package javasabr.rlib.collections.deque.impl;

import javasabr.rlib.common.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultArrayBasedDeque<E> extends AbstractArrayBasedDeque<E> {

  @Nullable E[] items;
  int head;
  int tail;
  int rebalanceTrigger;

  @Getter int size;

  public DefaultArrayBasedDeque(Class<? super E> type) {
    this(type, DEFAULT_CAPACITY);
  }

  public DefaultArrayBasedDeque(Class<? super E> type, int capacity) {
    super(type, REBALANCE_FACTOR);
    this.items = ArrayUtils.create(type, capacity);
    resetIndexes();
  }

  @Override
  protected int decrementHeadAndGet() {
    return --head;
  }

  @Override
  protected int incrementHeadAndGet() {
    return ++head;
  }

  @Override
  protected int incrementTailAndGet() {
    return ++tail;
  }

  @Override
  protected int decrementTailAndGet() {
    return --tail;
  }

  @Override
  protected void incrementSize() {
    size++;
  }

  @Override
  protected void decrementSize() {
    size--;
  }
}
