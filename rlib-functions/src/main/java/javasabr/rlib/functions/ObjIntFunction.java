package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface ObjIntFunction<A, R> {

  R apply(A arg1, int arg2);
}
