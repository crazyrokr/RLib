package javasabr.rlib.collections.array;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javasabr.rlib.functions.TriConsumer;
import org.jspecify.annotations.Nullable;

public interface ReversedArrayIterationFunctions<E> {

  @Nullable
  <A> E findAny(A arg1, BiPredicate<A, ? super E> filter);

  <A> ReversedArrayIterationFunctions<E> forEach(A arg1, BiConsumer<A, ? super E> consumer);

  <A, B> ReversedArrayIterationFunctions<E> forEach(A arg1, B arg2, TriConsumer<A, B, ? super E> consumer);

  <A> boolean anyMatch(A arg1, BiPredicate<A, ? super E> filter);
}
