package javasabr.rlib.collections.array;

import javasabr.rlib.collections.array.impl.CopyOnWriteMutableArray;
import javasabr.rlib.collections.array.impl.DefaultMutableArray;
import javasabr.rlib.collections.array.impl.StampedLockBasedArray;
import javasabr.rlib.common.util.ClassUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayFactory {

  public static <E> MutableArray<E> mutableArray(Class<? super E> type) {
    return new DefaultMutableArray<>(ClassUtils.unsafeCast(type));
  }

  public static <E> MutableArray<E> mutableArray(Class<? super E> type, int capacity) {
    return new DefaultMutableArray<>(ClassUtils.unsafeCast(type), capacity);
  }

  public static <E> MutableArray<E> copyOnModifyArray(Class<? super E> type) {
    return new CopyOnWriteMutableArray<>(type);
  }

  public static <E> LockableArray<E> stampedLockBasedArray(Class<? super E> type) {
    return new StampedLockBasedArray<>(type);
  }
}
