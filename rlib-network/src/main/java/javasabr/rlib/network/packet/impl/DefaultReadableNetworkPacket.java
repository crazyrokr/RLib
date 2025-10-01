package javasabr.rlib.network.packet.impl;

/**
 * @author JavaSaBr
 */
public class DefaultReadableNetworkPacket extends
    AbstractIdBasedReadableNetworkPacket<DefaultReadableNetworkPacket> {

  @Override
  public DefaultReadableNetworkPacket newInstance() {
    return new DefaultReadableNetworkPacket();
  }
}
