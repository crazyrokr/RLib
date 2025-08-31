package javasabr.rlib.collections.array;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import org.jspecify.annotations.Nullable;

public interface ArrayIterationFunctions<E> {

  @Nullable
  <T> E findAny(T arg1, BiPredicate<? super E, T> filter);

  <T> ArrayIterationFunctions<E> forEach(T arg1, BiConsumer<? super E, T> consumer);

  <T> boolean anyMatch(T arg1, BiPredicate<? super E, T> filter);
}
