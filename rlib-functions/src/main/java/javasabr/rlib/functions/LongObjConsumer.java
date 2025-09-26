package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface LongObjConsumer<B> {

  void accept(long arg1, B arg2);
}
