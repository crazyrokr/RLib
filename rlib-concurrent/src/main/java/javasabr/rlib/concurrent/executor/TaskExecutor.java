package javasabr.rlib.concurrent.executor;

import java.util.concurrent.CompletionStage;
import javasabr.rlib.concurrent.task.CallableTask;
import javasabr.rlib.concurrent.task.SimpleTask;

/**
 * @author JavaSaBr
 */
public interface TaskExecutor<L> {

  void execute(SimpleTask<L> task);

  <R> CompletionStage<R> submit(CallableTask<R, L> task);
}
