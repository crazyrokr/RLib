package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.impl.StringReadableNetworkPacket;
import javasabr.rlib.network.packet.impl.StringWritableNetworkPacket;
import javax.net.ssl.SSLContext;

/**
 * @author JavaSaBr
 */
public class StringDataSslConnection extends DefaultDataSslConnection<StringDataSslConnection> {

  public StringDataSslConnection(
      Network<StringDataSslConnection> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      SSLContext sslContext,
      boolean clientMode) {
    super(network, channel, bufferAllocator, sslContext, 100, 2, clientMode);
  }

  @Override
  protected StringReadableNetworkPacket<StringDataSslConnection> createReadablePacket() {
    return new StringReadableNetworkPacket<>();
  }
}
