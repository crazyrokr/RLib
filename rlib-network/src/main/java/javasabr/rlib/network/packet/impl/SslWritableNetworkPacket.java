package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;

/**
 * Packet marker.
 */
public class SslWritableNetworkPacket extends AbstractWritableNetworkPacket {

  private static final SslWritableNetworkPacket INSTANCE = new SslWritableNetworkPacket();

  public static SslWritableNetworkPacket getInstance() {
    return INSTANCE;
  }

  @Override
  public boolean write(ByteBuffer buffer) {
    return true;
  }
}
