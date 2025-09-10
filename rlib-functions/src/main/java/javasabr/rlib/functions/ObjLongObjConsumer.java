package javasabr.rlib.functions;

@FunctionalInterface
public interface ObjLongObjConsumer<F, T> {

  /**
   * Performs this operation on the given arguments.
   */
  void accept(F arg1, long arg2, T arg3);
}
