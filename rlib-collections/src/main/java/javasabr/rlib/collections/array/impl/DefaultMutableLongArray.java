package javasabr.rlib.collections.array.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter(AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultMutableLongArray extends AbstractMutableLongArray {

  protected static final int DEFAULT_CAPACITY = 10;

  long[] wrapped;
  int size;

  public DefaultMutableLongArray() {
    this(DEFAULT_CAPACITY);
  }

  public DefaultMutableLongArray(int capacity) {
    validateCapacity(capacity);
    this.wrapped = new long[capacity];
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
