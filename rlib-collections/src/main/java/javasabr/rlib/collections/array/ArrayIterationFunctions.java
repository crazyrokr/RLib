package javasabr.rlib.collections.array;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javasabr.rlib.functions.ObjLongObjConsumer;
import javasabr.rlib.functions.ObjObjLongConsumer;
import javasabr.rlib.functions.TriConsumer;
import org.jspecify.annotations.Nullable;

public interface ArrayIterationFunctions<E> {

  @Nullable
  <T> E findAny(T arg1, BiPredicate<? super E, T> filter);

  <T> ArrayIterationFunctions<E> forEach(T arg1, BiConsumer<? super E, T> consumer);

  <F, S> ArrayIterationFunctions<E> forEach(F arg1, S arg2, TriConsumer<? super E, F, S> consumer);

  <F> ArrayIterationFunctions<E> forEach(F arg1, long arg2, ObjObjLongConsumer<? super E, F> consumer);

  <T> boolean anyMatch(T arg1, BiPredicate<? super E, T> filter);
}
