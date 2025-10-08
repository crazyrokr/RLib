package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.MarkerNetworkPacket;

public class SslWrapRequestNetworkPacket<C extends Connection<C>>
    extends AbstractWritableNetworkPacket<C> implements MarkerNetworkPacket {

  private static final SslWrapRequestNetworkPacket<?> INSTANCE = new SslWrapRequestNetworkPacket<>();

  public static <C extends Connection<C>> SslWrapRequestNetworkPacket<C> getInstance() {
    return (SslWrapRequestNetworkPacket<C>) INSTANCE;
  }

  @Override
  public boolean write(C connection, ByteBuffer buffer) {
    return true;
  }
}
