package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface IntObjFunction<B, R> {

  R apply(int arg1, B arg2);
}
