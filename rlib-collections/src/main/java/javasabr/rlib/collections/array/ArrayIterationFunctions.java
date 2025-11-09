package javasabr.rlib.collections.array;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import javasabr.rlib.functions.ObjIntPredicate;
import javasabr.rlib.functions.ObjObjLongConsumer;
import javasabr.rlib.functions.TriConsumer;
import org.jspecify.annotations.Nullable;

public interface ArrayIterationFunctions<E> {

  ReversedArgsArrayIterationFunctions<E> reversedArgs();

  @Nullable
  <A> E findAny(A arg1, BiPredicate<? super E, A> filter);

  @Nullable
  E findAny(int arg1, ObjIntPredicate<? super E> filter);

  <A> ArrayIterationFunctions<E> forEach(A arg1, BiConsumer<? super E, A> consumer);

  <A, B> ArrayIterationFunctions<E> forEach(A arg1, B arg2, TriConsumer<? super E, A, B> consumer);

  <A> ArrayIterationFunctions<E> forEach(A arg1, long arg2, ObjObjLongConsumer<? super E, A> consumer);

  <A> boolean anyMatch(A arg1, BiPredicate<? super E, A> filter);
}
