package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {

  R apply(A arg1, B arg2, C arg3);
}
