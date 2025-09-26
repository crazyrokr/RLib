package javasabr.rlib.functions;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

  /**
   * Performs this operation on the given arguments.
   */
  void accept(A arg1, B arg2, C arg3);
}
