package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.functions.ObjBoolConsumer;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javax.net.ssl.SSLEngine;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultSslNetworkPacketWriter<
    W extends WritableNetworkPacket<C>,
    C extends Connection<C>> extends AbstractSslNetworkPacketWriter<W, C> {

  final int packetLengthHeaderSize;

  public DefaultSslNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<@Nullable WritableNetworkPacket<C>> nextWritePacketSupplier,
      Consumer<WritableNetworkPacket<C>> serializedToChannelPacketHandler,
      ObjBoolConsumer<WritableNetworkPacket<C>> sentPacketHandler,
      SSLEngine sslEngine,
      Consumer<WritableNetworkPacket<C>> queueAtFirst,
      int packetLengthHeaderSize) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        nextWritePacketSupplier,
        serializedToChannelPacketHandler,
        sentPacketHandler,
        sslEngine,
        queueAtFirst);
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
      ByteBuffer writeBuffer) {
    writeBuffer
        .clear()
        .position(packetLengthHeaderSize);
    return true;
  }

  @Override
  protected ByteBuffer onSerializeResult(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer writeBuffer) {
    return writePacketLength(writeBuffer, writeBuffer.limit())
        .position(0);
  }

  protected ByteBuffer writePacketLength(ByteBuffer buffer, int packetLength) {
    return writeHeader(buffer, 0, packetLength, packetLengthHeaderSize);
  }
}
