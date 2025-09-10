package javasabr.rlib.concurrent.task;

/**
 * @author JavaSaBr
 */
public interface SimpleTask<L> extends CallableTask<Void, L> {

  @Override
  default Void call(L local, long cycleTime) {
    execute(local, cycleTime);
    return null;
  }

  void execute(L local, long cycleTime);
}
