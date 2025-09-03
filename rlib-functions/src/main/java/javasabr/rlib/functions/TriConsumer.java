package javasabr.rlib.functions;

@FunctionalInterface
public interface TriConsumer<F, S, T> {

  /**
   * Performs this operation on the given arguments.
   */
  void accept(F arg1, S arg2, T arg3);
}
