package javasabr.rlib.network;

import java.nio.ByteOrder;
import javasabr.rlib.common.util.GroupThreadFactory.ThreadConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The interface to implement a server network config.
 *
 * @author JavaSaBr
 */
public interface ServerNetworkConfig extends NetworkConfig {

  @Builder
  @Getter
  @Accessors(fluent = true, chain = false)
  class SimpleServerNetworkConfig implements ServerNetworkConfig {

    @Builder.Default
    private String threadGroupName = "ServerNetworkThread";
    @Builder.Default
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    @Builder.Default
    private ThreadConstructor threadConstructor = Thread::new;

    @Builder.Default
    private int readBufferSize = 2048;
    @Builder.Default
    private int pendingBufferSize = 4096;
    @Builder.Default
    private int writeBufferSize = 2048;
    @Builder.Default
    private int retryDelayInMs = 1000;
    @Builder.Default
    private int threadGroupMinSize = 1;
    @Builder.Default
    private int threadGroupMaxSize = 1;
    @Builder.Default
    private int scheduledThreadGroupSize = 1;
    @Builder.Default
    private int threadPriority = Thread.NORM_PRIORITY;
  }

  ServerNetworkConfig DEFAULT_SERVER = new ServerNetworkConfig() {

    @Override
    public String threadGroupName() {
      return "ServerNetworkThread";
    }

    @Override
    public String scheduledThreadGroupName() {
      return "ServerScheduledNetworkThread";
    }
  };

  /**
   * Get a minimal size of network thread executor.
   */
  default int threadGroupMinSize() {
    return 1;
  }

  /**
   * Get a maximum size of network thread executor.
   */
  default int threadGroupMaxSize() {
    return threadGroupMinSize();
  }

  /**
   * Get a size of network scheduled thread executor.
   */
  default int scheduledThreadGroupSize() {
    return 1;
  }

  /**
   * Get a thread constructor which should be used to create network threads.
   */
  default ThreadConstructor threadConstructor() {
    return Thread::new;
  }

  /**
   * Get a priority of network threads.
   *
   * @return the priority of network threads.
   */
  default int threadPriority() {
    return Thread.NORM_PRIORITY;
  }
}
