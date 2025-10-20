package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.impl.IdBasedNetworkPacketReader;
import javasabr.rlib.network.packet.impl.IdBasedNetworkPacketWriter;
import javasabr.rlib.network.packet.registry.ReadableNetworkPacketRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Getter(AccessLevel.PROTECTED)
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class IdBasedPacketConnection<C extends IdBasedPacketConnection<C>> extends AbstractConnection<C> {

  final NetworkPacketReader packetReader;
  final NetworkPacketWriter packetWriter;
  final ReadableNetworkPacketRegistry<? extends IdBasedReadableNetworkPacket<C>, C> packetRegistry;

  final int packetLengthHeaderSize;
  final int packetIdHeaderSize;

  public IdBasedPacketConnection(
      Network<C> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      ReadableNetworkPacketRegistry<? extends IdBasedReadableNetworkPacket<C>, C> packetRegistry,
      int maxPacketsByRead,
      int packetLengthHeaderSize,
      int packetIdHeaderSize) {
    super(network, channel, bufferAllocator, maxPacketsByRead);
    this.packetRegistry = packetRegistry;
    this.packetLengthHeaderSize = packetLengthHeaderSize;
    this.packetIdHeaderSize = packetIdHeaderSize;
    this.packetReader = createPacketReader();
    this.packetWriter = createPacketWriter();
  }

  protected NetworkPacketReader createPacketReader() {
    return new IdBasedNetworkPacketReader<>(
        (C) this,
        this::updateLastActivity,
        this::handleReceivedPacket,
        packetLengthHeaderSize,
        maxPacketsByRead,
        packetIdHeaderSize,
        packetRegistry);
  }

  protected NetworkPacketWriter createPacketWriter() {
    return new IdBasedNetworkPacketWriter<>(
        (C) this,
        this::updateLastActivity,
        () -> nextPacketToWrite(),
        this::serializedPacket,
        this::handleSentPacket,
        packetLengthHeaderSize,
        packetIdHeaderSize);
  }
}
