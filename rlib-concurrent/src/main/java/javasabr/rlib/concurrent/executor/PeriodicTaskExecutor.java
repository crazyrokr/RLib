package javasabr.rlib.concurrent.executor;

import java.util.Collection;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.concurrent.task.PeriodicTask;

/**
 * @author JavaSaBr
 */
public interface PeriodicTaskExecutor<T extends PeriodicTask<L>, L> {

  void registerTask(T task);

  void registerTasks(Array<T> tasks);

  void registerTasks(Collection<T> tasks);

  void unregisterTask(T task);
}
