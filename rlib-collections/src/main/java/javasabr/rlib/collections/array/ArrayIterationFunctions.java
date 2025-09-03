package javasabr.rlib.collections.array;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javasabr.rlib.functions.TriConsumer;
import org.jspecify.annotations.Nullable;

public interface ArrayIterationFunctions<E> {

  @Nullable
  <T> E findAny(T arg1, BiPredicate<? super E, T> filter);

  <T> ArrayIterationFunctions<E> forEach(T arg1, BiConsumer<? super E, T> consumer);

  <F, S> ArrayIterationFunctions<E> forEach(F arg1, S arg2, TriConsumer<? super E, F, S> consumer);

  <T> boolean anyMatch(T arg1, BiPredicate<? super E, T> filter);
}
