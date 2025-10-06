package javasabr.rlib.network.packet;

import javasabr.rlib.network.annotation.NetworkPacketDescription;

/**
 * @author JavaSaBr
 */
public interface IdBasedNetworkPacket extends NetworkPacket {

  /**
   * Get id of this packet.
   *
   * @return the packet type's id.
   */
  default int packetId() {
    return getClass()
        .getAnnotation(NetworkPacketDescription.class)
        .id();
  }
}
