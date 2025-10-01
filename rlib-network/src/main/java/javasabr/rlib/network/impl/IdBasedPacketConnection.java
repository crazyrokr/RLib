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

/**
 * @author JavaSaBr
 */
@Getter(AccessLevel.PROTECTED)
public class IdBasedPacketConnection<R extends IdBasedReadableNetworkPacket<R>, W extends IdBasedWritableNetworkPacket> extends
    AbstractConnection<R, W> {

  private final NetworkPacketReader packetReader;
  private final NetworkPacketWriter packetWriter;
  private final ReadableNetworkPacketRegistry<R> packetRegistry;

  private final int packetLengthHeaderSize;
  private final int packetIdHeaderSize;

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
        this::onWrittenPacket,
        this::onSentPacket,
        packetLengthHeaderSize,
        packetIdHeaderSize);
  }
}
