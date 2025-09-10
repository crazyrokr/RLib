package javasabr.rlib.concurrent.lock;

/**
 * @author JavaSaBr
 */
public interface AsyncReadSyncWriteLock {

  void asyncLock();
  void asyncUnlock();

  void syncLock();
  void syncUnlock();
}
