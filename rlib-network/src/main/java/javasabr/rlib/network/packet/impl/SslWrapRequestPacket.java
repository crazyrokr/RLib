package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import javasabr.rlib.network.packet.MarkerNetworkPacket;

public class SslWrapRequestPacket extends AbstractWritableNetworkPacket
    implements MarkerNetworkPacket {

  private static final SslWrapRequestPacket INSTANCE = new SslWrapRequestPacket();

  public static SslWrapRequestPacket getInstance() {
    return INSTANCE;
  }

  @Override
  public boolean write(ByteBuffer buffer) {
    return true;
  }
}
