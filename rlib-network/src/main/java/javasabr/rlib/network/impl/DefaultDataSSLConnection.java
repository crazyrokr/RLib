package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javasabr.rlib.network.packet.impl.DefaultSslNetworkPacketReader;
import javasabr.rlib.network.packet.impl.DefaultSslNetworkPacketWriter;
import javax.net.ssl.SSLContext;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author JavaSaBr
 */
@Getter(AccessLevel.PROTECTED)
public abstract class DefaultDataSSLConnection<R extends ReadableNetworkPacket, W extends WritableNetworkPacket> extends
    AbstractSSLConnection<R, W> {

  private final NetworkPacketReader packetReader;
  private final NetworkPacketWriter packetWriter;

  private final int packetLengthHeaderSize;

  public DefaultDataSSLConnection(
      Network<? extends Connection<R, W>> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      SSLContext sslContext,
      int maxPacketsByRead,
      int packetLengthHeaderSize,
      boolean clientMode) {
    super(network, channel, bufferAllocator, sslContext, maxPacketsByRead, clientMode);
    this.packetLengthHeaderSize = packetLengthHeaderSize;
    this.packetReader = createPacketReader();
    this.packetWriter = createPacketWriter();
  }

  protected NetworkPacketReader createPacketReader() {
    return new DefaultSslNetworkPacketReader<>(
        this,
        channel,
        bufferAllocator,
        this::updateLastActivity,
        this::handleReceivedPacket,
        value -> createReadablePacket(),
        sslEngine,
        this::sendImpl,
        packetLengthHeaderSize,
        maxPacketsByRead);
  }

  protected NetworkPacketWriter createPacketWriter() {
    return new DefaultSslNetworkPacketWriter<W, Connection<R, W>>(
        this,
        channel,
        bufferAllocator,
        this::updateLastActivity,
        this::nextPacketToWrite,
        this::onWrittenPacket,
        this::onSentPacket,
        sslEngine,
        this::sendImpl,
        this::queueAtFirst,
        packetLengthHeaderSize);
  }

  protected abstract R createReadablePacket();
}
