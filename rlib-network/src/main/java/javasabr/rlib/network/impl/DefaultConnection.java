package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.registry.ReadableNetworkPacketRegistry;

/**
 * @author JavaSaBr
 */
public class DefaultConnection extends IdBasedPacketConnection<DefaultConnection> {

  public DefaultConnection(
      Network<DefaultConnection> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      ReadableNetworkPacketRegistry<? extends IdBasedReadableNetworkPacket<DefaultConnection>, DefaultConnection> packetRegistry) {
    super(network, channel, bufferAllocator, packetRegistry, 100, 2, 2);
  }
}
