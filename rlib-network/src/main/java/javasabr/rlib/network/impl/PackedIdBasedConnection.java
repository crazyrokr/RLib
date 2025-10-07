package javasabr.rlib.network.impl;

import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;

/**
 * The interface to implement a packet id based async connection.
 *
 * @author JavaSaBr
 */
public interface PackedIdBasedConnection<
    R extends ReadableNetworkPacket<C>,
    W extends WritableNetworkPacket<C>,
    C extends Connection<R, W, C>> extends Connection<R, W, C> {

  /**
   * Get length of packet's header with packet's data length.
   *
   * @return the length of packet's header with packet's data length.
   */
  int getPacketLengthHeaderSize();

  /**
   * Get length of packet's header with packet's id.
   *
   * @return the length of packet's header with packet's id.
   */
  int getPacketIdHeaderSize();
}
