package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface IntObjConsumer<B> {

  void accept(int arg1, B arg2);
}
