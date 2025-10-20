package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.impl.DefaultNetworkPacketReader;
import javasabr.rlib.network.packet.impl.DefaultNetworkPacketWriter;
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
public abstract class DefaultDataConnection<C extends DefaultDataConnection<C>> extends AbstractConnection<C> {

  final NetworkPacketReader packetReader;
  final NetworkPacketWriter packetWriter;

  final int packetLengthHeaderSize;

  public DefaultDataConnection(
      Network<C> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      int maxPacketsByRead,
      int packetLengthHeaderSize) {
    super(network, channel, bufferAllocator, maxPacketsByRead);
    this.packetLengthHeaderSize = packetLengthHeaderSize;
    this.packetReader = createPacketReader();
    this.packetWriter = createPacketWriter();
  }

  protected NetworkPacketReader createPacketReader() {
    return new DefaultNetworkPacketReader<>(
        (C) this,
        this::updateLastActivity,
        this::handleReceivedPacket,
        value -> createReadablePacket(),
        packetLengthHeaderSize,
        maxPacketsByRead);
  }

  protected NetworkPacketWriter createPacketWriter() {
    return new DefaultNetworkPacketWriter<>(
        (C) this,
        this::updateLastActivity,
        this::nextPacketToWrite,
        this::serializedPacket,
        this::handleSentPacket,
        packetLengthHeaderSize);
  }

  protected abstract ReadableNetworkPacket<C> createReadablePacket();
}
