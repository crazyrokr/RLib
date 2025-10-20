package javasabr.rlib.network;

import java.nio.ByteOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The interface to implement a network config.
 *
 * @author JavaSaBr
 */
public interface NetworkConfig {

  @Builder
  @Getter
  @Accessors(fluent = true, chain = false)
  class SimpleNetworkConfig implements NetworkConfig {

    @Builder.Default
    private String groupName = "NetworkThread";
    @Builder.Default
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    @Builder.Default
    private int readBufferSize = 2048;
    @Builder.Default
    private int pendingBufferSize = 4096;
    @Builder.Default
    private int writeBufferSize = 2048;
    @Builder.Default
    private int retryDelayInMs = 1000;
    @Builder.Default
    private int maxPacketSize = 5 * 1024 * 1024;
    @Builder.Default
    private int maxEmptyReadsBeforeClose = 3;
    @Builder.Default
    private boolean useDirectByteBuffer = false;
  }

  NetworkConfig DEFAULT_CLIENT = new NetworkConfig() {

    @Override
    public String threadGroupName() {
      return "ClientNetworkThread";
    }
  };

  /**
   * Get a group name of network threads.
   */
  default String threadGroupName() {
    return "NetworkThread";
  }

  /**
   * Get a group name of scheduling network threads.
   */
  default String scheduledThreadGroupName() {
    return "ScheduledNetworkThread";
  }

  /**
   * Get size of buffer with will be used to collect received data from network.
   */
  default int readBufferSize() {
    return 2048;
  }

  /**
   * Gets size of buffer with pending reading data. Pending buffer allows to construct a packet with bigger data part than
   * {@link #readBufferSize()}. It should be at least 2x of {@link #readBufferSize()}
   */
  default int pendingBufferSize() {
    return readBufferSize() * 2;
  }

  /**
   * Gets a size of buffer which will be used for packet serialization.
   */
  default int writeBufferSize() {
    return 2048;
  }

  /**
   * Gets the max size of one single network packet.
   */
  default int maxPacketSize() {
    return 5 * 1024 * 1024;
  }

  /**
   * Gets a timeout for retry read/write operation.
   */
  default int retryDelayInMs() {
    return 1000;
  }

  /**
   * Gets a max allowed empty reads from socket channel before closing a connection.
   */
  default int maxEmptyReadsBeforeClose() {
    return 3;
  }

  default ByteOrder byteOrder() {
    return ByteOrder.BIG_ENDIAN;
  }

  default boolean useDirectByteBuffer() {
    return false;
  }
}
