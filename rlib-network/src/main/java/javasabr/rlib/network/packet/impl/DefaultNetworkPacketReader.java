package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.UnsafeConnection;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBR
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultNetworkPacketReader<
    R extends ReadableNetworkPacket<C>,
    C extends UnsafeConnection<C>> extends AbstractNetworkPacketReader<R, C> {

  final IntFunction<R> readablePacketFactory;
  final int packetLengthHeaderSize;

  public DefaultNetworkPacketReader(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Consumer<R> packetHandler,
      IntFunction<R> readablePacketFactory,
      int packetLengthHeaderSize,
      int maxPacketsByRead) {
    super(connection, channel, bufferAllocator, updateActivityFunction, packetHandler, maxPacketsByRead);
    this.readablePacketFactory = readablePacketFactory;
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
    return readablePacketFactory.apply(packetDataLength);
  }
}
