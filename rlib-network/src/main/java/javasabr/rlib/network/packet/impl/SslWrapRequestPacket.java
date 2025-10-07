package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.MarkerNetworkPacket;

public class SslWrapRequestPacket<C extends Connection> extends AbstractWritableNetworkPacket<C>
    implements MarkerNetworkPacket {

  private static final SslWrapRequestPacket<?> INSTANCE = new SslWrapRequestPacket<>();

  public static <C extends Connection> SslWrapRequestPacket<C> getInstance() {
    return (SslWrapRequestPacket<C>) INSTANCE;
  }

  @Override
  public boolean write(C connection, ByteBuffer buffer) {
    return true;
  }
}
