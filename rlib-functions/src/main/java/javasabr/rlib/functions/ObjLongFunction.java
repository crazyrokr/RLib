package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface ObjLongFunction<T, R> {

  R apply(T arg1, long arg2);
}
