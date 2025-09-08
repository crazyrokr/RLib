package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface LongObjConsumer<T> {

  void accept(long arg1, T arg2);
}
