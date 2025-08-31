package javasabr.rlib.collections.array;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import org.jspecify.annotations.Nullable;

public interface ReversedArrayIterationFunctions<E> {

  @Nullable
  <T> E findAny(T arg1, BiPredicate<T, ? super E> filter);

  <T> ReversedArrayIterationFunctions<E> forEach(T arg1, BiConsumer<T, ? super E> consumer);

  <T> boolean anyMatch(T arg1, BiPredicate<T, ? super E> filter);
}
