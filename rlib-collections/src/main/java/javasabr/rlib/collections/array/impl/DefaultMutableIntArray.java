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
public class DefaultMutableIntArray extends AbstractMutableIntArray {

  protected static final int DEFAULT_CAPACITY = 10;

  int[] wrapped;
  int size;

  public DefaultMutableIntArray() {
    this(DEFAULT_CAPACITY);
  }

  public DefaultMutableIntArray(int capacity) {
    validateCapacity(capacity);
    this.wrapped = new int[capacity];
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
