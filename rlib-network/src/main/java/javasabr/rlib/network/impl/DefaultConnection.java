package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.impl.DefaultReadableNetworkPacket;
import javasabr.rlib.network.packet.impl.DefaultWritableNetworkPacket;
import javasabr.rlib.network.packet.registry.ReadableNetworkPacketRegistry;

/**
 * @author JavaSaBr
 */
public class DefaultConnection extends IdBasedPacketConnection<DefaultReadableNetworkPacket, DefaultWritableNetworkPacket> {

  public DefaultConnection(
      Network<? extends Connection<DefaultReadableNetworkPacket, DefaultWritableNetworkPacket>> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket> packetRegistry) {
    super(network, channel, bufferAllocator, packetRegistry, 100, 2, 2);
  }
}
