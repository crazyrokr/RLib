package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface IntObjFunction<T, R> {

  R apply(int arg1, T arg2);
}
