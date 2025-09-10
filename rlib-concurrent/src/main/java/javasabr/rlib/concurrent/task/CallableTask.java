package javasabr.rlib.concurrent.task;

import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface CallableTask<R, L> {

  /**
   *
   * @param local the container of thread local objects
   * @param cycleTime the start time of executing package of tasks iteration
   * @return the task result.
   */
  @Nullable
  R call(L local, long cycleTime);
}
