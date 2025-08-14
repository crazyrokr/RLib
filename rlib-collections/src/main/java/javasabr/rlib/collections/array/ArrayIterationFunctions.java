package javasabr.rlib.collections.array;

import java.util.function.BiPredicate;
import org.jspecify.annotations.Nullable;

public interface ArrayIterationFunctions<E> {

  <T> @Nullable E findAny(T arg1, BiPredicate<? super E, T> filter);

  <T> boolean anyMatch(T arg1, BiPredicate<? super E, T> filter);
}
