package javasabr.rlib.functions;

@FunctionalInterface
public interface ObjLongObjConsumer<A, C> {

  /**
   * Performs this operation on the given arguments.
   */
  void accept(A arg1, long arg2, C arg3);
}
