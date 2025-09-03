package javasabr.rlib.collections.array.impl;

import javasabr.rlib.common.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@Getter
@Setter(AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultMutableArray<E> extends AbstractMutableArray<E> {

  protected static final int DEFAULT_CAPACITY = 10;

  /*
    It's initialized during object construction by setter #wrapped
   */
  @Nullable
  E[] wrapped;
  int size;

  public DefaultMutableArray(Class<? super E> type) {
    this(type, DEFAULT_CAPACITY);
  }

  public DefaultMutableArray(Class<? super E> type, int capacity) {
    super(type);
    validateCapacity(capacity);
    this.wrapped = ArrayUtils.create(type, capacity);
  }

  @Override
  public boolean isEmpty() {
    return size < 1;
  }

  @Override
  protected int getAndIncrementSize() {
    return size++;
  }

  @Override
  protected int decrementAnGetSize() {
    return --size;
  }
}
