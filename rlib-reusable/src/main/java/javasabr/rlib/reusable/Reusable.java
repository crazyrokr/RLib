package javasabr.rlib.reusable;

/**
 * @author JavaSaBr
 */
public interface Reusable extends AutoCloseable {

  /**
   * Cleanup this object
   */
  default void cleanup() {}

  @Override
  default void close() {
    cleanup();
  }
}
