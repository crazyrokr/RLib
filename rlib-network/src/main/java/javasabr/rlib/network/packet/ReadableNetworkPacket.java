package javasabr.rlib.network.packet;

import java.nio.ByteBuffer;

/**
 * The interface to implement a readable network packet.
 *
 * @author JavaSaBr
 */
public interface ReadableNetworkPacket extends NetworkPacket {

  /**
   * Read packet's data from byte buffer.
   *
   * @param buffer the buffer with received data.
   * @param expectedLength the expected data length.
   * @return true if reading was success.
   */
  boolean read(ByteBuffer buffer, int expectedLength);
}
