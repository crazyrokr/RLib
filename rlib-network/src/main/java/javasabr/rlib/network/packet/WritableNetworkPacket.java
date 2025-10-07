package javasabr.rlib.network.packet;

import java.nio.ByteBuffer;
import javasabr.rlib.network.Connection;

/**
 * Interface to implement a writable packet.
 *
 * @author JavaSaBr
 */
public interface WritableNetworkPacket<C extends Connection> extends NetworkPacket<C> {

  /**
   * Write this packet to the buffer.
   *
   * @return true if writing was successful.
   */
  boolean write(C connection, ByteBuffer buffer);

  /**
   * @return expected data length of this packet or -1.
   */
  default int expectedLength(C connection) {
    return -1;
  }
}
