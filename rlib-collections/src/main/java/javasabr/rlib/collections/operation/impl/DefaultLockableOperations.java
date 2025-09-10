package javasabr.rlib.collections.operation.impl;

import java.util.function.BiFunction;
import java.util.function.Function;
import javasabr.rlib.collections.operation.LockableOperations;
import javasabr.rlib.collections.operation.LockableSource;

public record DefaultLockableOperations<S extends LockableSource>(S source) implements LockableOperations<S> {

  @Override
  public <R> R getInReadLock(Function<S, R> function) {
    return function.apply(source);
  }

  @Override
  public <R, F> R getInReadLock(F arg1, BiFunction<S, F, R> function) {
    return function.apply(source, arg1);
  }
}
