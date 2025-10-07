package javasabr.rlib.network.packet;

import java.nio.ByteBuffer;
import javasabr.rlib.network.Connection;

/**
 * The interface to implement a readable network packet.
 *
 * @author JavaSaBr
 */
public interface ReadableNetworkPacket<C extends Connection> extends NetworkPacket<C> {

  /**
   * Read packet's data from byte buffer.
   *
   * @param buffer the buffer with received data.
   * @param remainingDataLength the expected remaining data length.
   * @return true if reading was success.
   */
  boolean read(C connection, ByteBuffer buffer, int remainingDataLength);
}
