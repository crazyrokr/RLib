package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.functions.ObjBoolConsumer;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultNetworkPacketWriter<
    W extends WritableNetworkPacket<C>,
    C extends Connection<?, W, C>> extends AbstractNetworkPacketWriter<W, C> {

  final int packetLengthHeaderSize;

  public DefaultNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<@Nullable WritableNetworkPacket<C>> packetProvider,
      Consumer<WritableNetworkPacket<C>> serializedToChannelPacketHandler,
      ObjBoolConsumer<WritableNetworkPacket<C>> sentPacketHandler,
      int packetLengthHeaderSize) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        packetProvider,
        serializedToChannelPacketHandler,
        sentPacketHandler);
    this.packetLengthHeaderSize = packetLengthHeaderSize;
  }

  @Override
  protected int totalSize(WritableNetworkPacket<C> packet, int expectedLength) {
    return expectedLength + packetLengthHeaderSize;
  }

  @Override
  protected boolean onBeforeSerialize(
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
  protected ByteBuffer onSerializeResult(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    return writePacketLength(firstBuffer, firstBuffer.limit())
        .position(0);
  }

  protected ByteBuffer writePacketLength(ByteBuffer buffer, int packetLength) {
    return writeHeader(buffer, 0, packetLength, packetLengthHeaderSize);
  }
}
