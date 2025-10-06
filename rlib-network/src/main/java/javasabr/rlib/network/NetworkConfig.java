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
   * Get size of buffer with used to collect received data from network.
   */
  default int readBufferSize() {
    return 2048;
  }

  /**
   * Get size of buffer with pending data. Pending buffer allows to construct a packet with bigger data than
   * {@link #readBufferSize()}. It should be at least 2x of {@link #readBufferSize()}
   *
   * @return the pending buffer's size.
   */
  default int pendingBufferSize() {
    return readBufferSize() * 2;
  }

  /**
   * Get size of buffer which used to serialize packets to bytes.
   */
  default int writeBufferSize() {
    return 2048;
  }

  default ByteOrder byteOrder() {
    return ByteOrder.BIG_ENDIAN;
  }

  default boolean isDirectByteBuffer() {
    return false;
  }
}
