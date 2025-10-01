package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.IdBasedWritableNetworkPacket;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.impl.IdBasedPacketReader;
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
public class IdBasedPacketConnection<R extends IdBasedReadableNetworkPacket<R>, W extends IdBasedWritableNetworkPacket>
    extends AbstractConnection<R, W> {

  final NetworkPacketReader packetReader;
  final NetworkPacketWriter packetWriter;
  final ReadableNetworkPacketRegistry<R> packetRegistry;

  final int packetLengthHeaderSize;
  final int packetIdHeaderSize;

  public IdBasedPacketConnection(
      Network<? extends Connection<R, W>> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      ReadableNetworkPacketRegistry<R> packetRegistry,
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
    return new IdBasedPacketReader<>(
        this,
        channel,
        bufferAllocator,
        this::updateLastActivity,
        this::handleReceivedPacket,
        packetLengthHeaderSize,
        maxPacketsByRead,
        packetIdHeaderSize,
        packetRegistry);
  }

  protected NetworkPacketWriter createPacketWriter() {
    return new IdBasedNetworkPacketWriter<>(
        this,
        channel,
        bufferAllocator,
        this::updateLastActivity,
        this::nextPacketToWrite,
        this::serializedPacket,
        this::handleSentPacket,
        packetLengthHeaderSize,
        packetIdHeaderSize);
  }
}
