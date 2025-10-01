package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javasabr.rlib.network.packet.impl.DefaultNetworkPacketReader;
import javasabr.rlib.network.packet.impl.DefaultNetworkPacketWriter;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author JavaSaBr
 */
@Getter(AccessLevel.PROTECTED)
public abstract class DefaultDataConnection<R extends ReadableNetworkPacket, W extends WritableNetworkPacket> extends
    AbstractConnection<R, W> {

  private final NetworkPacketReader packetReader;
  private final NetworkPacketWriter packetWriter;

  private final int packetLengthHeaderSize;

  public DefaultDataConnection(
      Network<? extends Connection<R, W>> network,
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
        this,
        channel,
        bufferAllocator,
        this::updateLastActivity,
        this::handleReceivedPacket,
        value -> createReadablePacket(),
        packetLengthHeaderSize,
        maxPacketsByRead);
  }

  protected NetworkPacketWriter createPacketWriter() {
    return new DefaultNetworkPacketWriter<W, Connection<R, W>>(
        this,
        channel,
        bufferAllocator,
        this::updateLastActivity,
        this::nextPacketToWrite,
        this::onWrittenPacket,
        this::onSentPacket,
        packetLengthHeaderSize);
  }

  protected abstract R createReadablePacket();
}
