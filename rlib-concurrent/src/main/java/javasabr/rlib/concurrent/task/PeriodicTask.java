package javasabr.rlib.concurrent.task;

/**
 * @author JavaSaBr
 */
public interface PeriodicTask<L> extends CallableTask<Boolean, L> {

  @Override
  default Boolean call(L local, long cycleTime) {
    if (update(local, cycleTime)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  default void onFinish(L local, long cycleTime) {}

  boolean update(L local, long cycleTime);
}
