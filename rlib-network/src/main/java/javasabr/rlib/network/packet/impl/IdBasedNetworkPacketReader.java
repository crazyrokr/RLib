package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.UnsafeConnection;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.registry.ReadableNetworkPacketRegistry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class IdBasedNetworkPacketReader<
    R extends IdBasedReadableNetworkPacket<C>,
    C extends UnsafeConnection<C>> extends AbstractNetworkPacketReader<R, C> {

  final ReadableNetworkPacketRegistry<R, C> packetRegistry;
  final int packetLengthHeaderSize;
  final int packetIdHeaderSize;

  public IdBasedNetworkPacketReader(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Consumer<R> packetHandler,
      int packetLengthHeaderSize,
      int maxPacketsByRead,
      int packetIdHeaderSize,
      ReadableNetworkPacketRegistry<R, C> packetRegistry) {
    super(connection, channel, bufferAllocator, updateActivityFunction, packetHandler, maxPacketsByRead);
    this.packetLengthHeaderSize = packetLengthHeaderSize;
    this.packetIdHeaderSize = packetIdHeaderSize;
    this.packetRegistry = packetRegistry;
  }

  @Override
  protected boolean canStartReadPacket(ByteBuffer buffer) {
    return buffer.remaining() > packetLengthHeaderSize;
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
    return (R) packetRegistry
        .resolvePrototypeById(readHeader(buffer, packetIdHeaderSize))
        .newInstance();
  }
}
