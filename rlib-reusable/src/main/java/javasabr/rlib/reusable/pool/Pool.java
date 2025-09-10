package javasabr.rlib.reusable.pool;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import javasabr.rlib.functions.ObjLongFunction;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
public interface Pool<E> {

  void put(E object);

  @Nullable
  E take();

  default E take(Supplier<E> factory) {
    E take = take();
    return take != null ? take : factory.get();
  }

  default <T> E take(T arg1, Function<T, E> factory) {
    E take = take();
    return take != null ? take : factory.apply(arg1);
  }

  default E take(long arg1, LongFunction<E> factory) {
    E take = take();
    return take != null ? take : factory.apply(arg1);
  }

  default <F> E take(
      F arg1,
      long arg2,
      ObjLongFunction<F, E> factory) {
    E take = take();
    return take != null ? take : factory.apply(arg1, arg2);
  }

  default <F, S> E take(
      F arg1,
      S arg2,
      BiFunction<F, S, E> factory) {
    E take = take();
    return take != null ? take : factory.apply(arg1, arg2);
  }
}
