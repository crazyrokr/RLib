package javasabr.rlib.collections.operation;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface LockableOperations<S extends LockableSource> {

  <R> R getInReadLock(Function<S, R> function);

  <R, F> R getInReadLock(F arg1, BiFunction<S, F, R> function);
}
