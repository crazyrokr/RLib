package javasabr.rlib.collections.operation;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javasabr.rlib.functions.BiObjToBooleanFunction;
import javasabr.rlib.functions.ObjIntFunction;
import javasabr.rlib.functions.TriConsumer;
import javasabr.rlib.functions.TriFunction;

public interface LockableOperations<S extends LockableSource> {

  <R> R getInReadLock(Function<S, R> function);

  <R, A> R getInReadLock(A arg1, BiFunction<S, A, R> function);

  <R> R getInReadLock(int arg1, ObjIntFunction<S, R> function);

  <R, A, B> R getInReadLock(A arg1, B arg2, TriFunction<S, A, B, R> function);

  <A> boolean getBooleanInReadLock(A arg1, BiObjToBooleanFunction<S, A> function);

  <A> void inReadLock(A arg1, BiConsumer<S, A> function);

  <R, A> R getInWriteLock(A arg1, BiFunction<S, A, R> function);

  <R, A, B> R getInWriteLock(A arg1, B arg2, TriFunction<S, A, B, R> function);

  <A, B> void inWriteLock(A arg1, B arg2, TriConsumer<S, A, B> function);

  <A> void inWriteLock(A arg1, BiConsumer<S, A> function);
}
