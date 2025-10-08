package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javax.net.ssl.SSLEngine;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBR
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultSslNetworkPacketReader<
    R extends ReadableNetworkPacket<C>,
    C extends Connection<C>> extends AbstractSslNetworkPacketReader<R, C> {

  final IntFunction<R> packetResolver;
  final int packetLengthHeaderSize;

  public DefaultSslNetworkPacketReader(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Consumer<R> packetHandler,
      IntFunction<R> packetResolver,
      SSLEngine sslEngine,
      Consumer<WritableNetworkPacket<C>> packetWriter,
      int packetLengthHeaderSize,
      int maxPacketsByRead) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        packetHandler,
        sslEngine,
        packetWriter,
        maxPacketsByRead);
    this.packetResolver = packetResolver;
    this.packetLengthHeaderSize = packetLengthHeaderSize;
  }

  @Override
  protected boolean canStartReadPacket(ByteBuffer buffer) {
    return buffer.remaining() >= packetLengthHeaderSize;
  }

  @Override
  protected int readFullPacketLength(ByteBuffer buffer) {
    return readHeader(buffer, packetLengthHeaderSize);
  }

  @Nullable
  @Override
  protected R createPacketFor(
      ByteBuffer buffer,
      int startPacketPosition,
      int packetFullLength,
      int packetDataLength) {
    return packetResolver.apply(packetDataLength);
  }
}
