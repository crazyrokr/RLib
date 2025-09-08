package javasabr.rlib.functions;

@FunctionalInterface
public interface ObjObjLongConsumer<F, S> {

  /**
   * Performs this operation on the given arguments.
   */
  void accept(F arg1, S arg2, long arg3);
}
