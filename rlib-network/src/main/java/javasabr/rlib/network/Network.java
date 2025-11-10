package javasabr.rlib.network;

import java.util.concurrent.ScheduledExecutorService;

/**
 * The interface to implement an asynchronous network.
 *
 * @author JavaSaBr
 */
public interface Network<C extends Connection<C>> {

  ScheduledExecutorService scheduledExecutor();

  NetworkConfig config();

  void inNetworkThread(Runnable task);

  /**
   * Shutdown this network.
   */
  void shutdown();
}
