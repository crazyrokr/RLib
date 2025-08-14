package javasabr.rlib.collections.array;

import java.util.function.BiPredicate;
import org.jspecify.annotations.Nullable;

public interface ReversedArrayIterationFunctions<E> {

  <T> @Nullable E findAny(T arg1, BiPredicate<T, ? super E> filter);

  <T> boolean anyMatch(T arg1, BiPredicate<T, ? super E> filter);
}
