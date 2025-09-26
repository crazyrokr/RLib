package javasabr.rlib.functions;

@FunctionalInterface
public interface ObjObjLongConsumer<A, B> {

  /**
   * Performs this operation on the given arguments.
   */
  void accept(A arg1, B arg2, long arg3);
}
