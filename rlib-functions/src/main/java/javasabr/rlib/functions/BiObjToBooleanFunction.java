package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface BiObjToBooleanFunction<A, B> {

  boolean apply(A arg1, B arg2);
}
