package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.impl.DefaultSslNetworkPacketReader;
import javasabr.rlib.network.packet.impl.DefaultSslNetworkPacketWriter;
import javax.net.ssl.SSLContext;
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
public abstract class DefaultDataSslConnection<C extends DefaultDataSslConnection<C>> extends AbstractSslConnection<C> {

  final NetworkPacketReader packetReader;
  final NetworkPacketWriter packetWriter;

  final int packetLengthHeaderSize;

  public DefaultDataSslConnection(
      Network<C> network,
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
        (C) this,
        this::updateLastActivity,
        this::handleReceivedPacket,
        value -> createReadablePacket(),
        sslEngine,
        this::sendImpl,
        packetLengthHeaderSize,
        maxPacketsByRead);
  }

  protected NetworkPacketWriter createPacketWriter() {
    return new DefaultSslNetworkPacketWriter<>(
        (C) this,
        this::updateLastActivity,
        this::nextPacketToWrite,
        this::serializedPacket,
        this::handleSentPacket,
        sslEngine,
        this::queueAtFirst,
        packetLengthHeaderSize);
  }

  protected abstract ReadableNetworkPacket<C> createReadablePacket();
}
