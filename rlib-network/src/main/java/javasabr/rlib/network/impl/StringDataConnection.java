package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.impl.StringReadableNetworkPacket;

/**
 * @author JavaSaBr
 */
public class StringDataConnection extends DefaultDataConnection<StringDataConnection> {

  public StringDataConnection(
      Network<StringDataConnection> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator) {
    super(network, channel, bufferAllocator, 100, 2);
  }

  @Override
  protected StringReadableNetworkPacket<StringDataConnection> createReadablePacket() {
    return new StringReadableNetworkPacket<>();
  }
}
