package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultNetworkPacketWriter<W extends WritableNetworkPacket, C extends Connection<?, W>>
    extends AbstractNetworkPacketWriter<W, C> {

  final int packetLengthHeaderSize;

  public DefaultNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<WritableNetworkPacket> nextWritePacketSupplier,
      Consumer<WritableNetworkPacket> writtenPacketHandler,
      BiConsumer<WritableNetworkPacket, Boolean> sentPacketHandler,
      int packetLengthHeaderSize) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        nextWritePacketSupplier,
        writtenPacketHandler,
        sentPacketHandler);
    this.packetLengthHeaderSize = packetLengthHeaderSize;
  }

  @Override
  protected int totalSize(WritableNetworkPacket packet, int expectedLength) {
    return expectedLength + packetLengthHeaderSize;
  }

  @Override
  protected boolean onBeforeWrite(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    firstBuffer
        .clear()
        .position(packetLengthHeaderSize);
    return true;
  }

  @Override
  protected ByteBuffer onResult(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    return writePacketLength(firstBuffer, firstBuffer.limit()).position(0);
  }

  protected ByteBuffer writePacketLength(ByteBuffer buffer, int packetLength) {
    return writeHeader(buffer, 0, packetLength, packetLengthHeaderSize);
  }
}
