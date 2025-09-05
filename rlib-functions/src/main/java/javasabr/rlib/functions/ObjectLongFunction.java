package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface ObjectLongFunction<T, R> {

  R apply(T arg1, long arg2);
}
