package javasabr.rlib.network.packet;

import javasabr.rlib.network.Connection;

/**
 * The interface to implement a network packet.
 *
 * @author JavaSaBr
 */
public interface NetworkPacket<C extends Connection> {

  /**
   * @return the packet's name.
   */
  String name();
}
