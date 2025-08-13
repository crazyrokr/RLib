package javasabr.rlib.collections.array;

import javasabr.rlib.collections.array.impl.DefaultMutableArray;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayFactory {

  public static <E> MutableArray<E> mutableArray(Class<E> type) {
    return new DefaultMutableArray<>(type);
  }
}
