package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface IntObjConsumer<T> {

  void accept(int arg1, T arg2);
}
