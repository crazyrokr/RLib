package javasabr.rlib.concurrent.lock;

/**
 * @author JavaSaBr
 */
public interface Lockable {

  void lock();

  void unlock();
}
