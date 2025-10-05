package javasabr.rlib.network.packet;

import java.nio.ByteBuffer;


/**
 * Interface to implement a writable packet.
 *
 * @author JavaSaBr
 */
public interface WritableNetworkPacket extends NetworkPacket {

  /**
   * Write this packet to the buffer.
   *
   * @return true if writing was successful.
   */
  boolean write(ByteBuffer buffer);

  /**
   * @return expected data length of this packet or -1.
   */
  default int expectedLength() {
    return -1;
  }
}
