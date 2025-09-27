package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface ObjIntPredicate<A> {

  boolean test(A arg1, int arg2);
}
