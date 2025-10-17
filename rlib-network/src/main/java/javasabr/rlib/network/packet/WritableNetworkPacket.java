package javasabr.rlib.network.packet;

import java.nio.ByteBuffer;
import javasabr.rlib.network.Connection;

/**
 * Interface to implement a writable packet.
 *
 * @author JavaSaBr
 */
public interface WritableNetworkPacket<C extends Connection<C>> extends NetworkPacket<C> {

  int UNKNOWN_EXPECTED_BYTES = -1;

  /**
   * Write this packet to the buffer.
   *
   * @return true if writing was successful.
   */
  boolean write(C connection, ByteBuffer buffer);

  /**
   * @return expected data length of this packet or {@code UNKNOWN_EXPECTED_BYTES}.
   */
  default int expectedLength(C connection) {
    return UNKNOWN_EXPECTED_BYTES;
  }
}
