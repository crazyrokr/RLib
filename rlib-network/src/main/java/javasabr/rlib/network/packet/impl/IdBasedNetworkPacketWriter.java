package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.common.function.NotNullBiConsumer;
import javasabr.rlib.common.function.NotNullConsumer;
import javasabr.rlib.common.function.NullableSupplier;
import javasabr.rlib.functions.ObjBoolConsumer;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.IdBasedWritableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class IdBasedNetworkPacketWriter<
    W extends IdBasedWritableNetworkPacket<C>,
    C extends Connection<?, W, C>> extends DefaultNetworkPacketWriter<W, C> {

  final int packetIdHeaderSize;

  public IdBasedNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<@Nullable WritableNetworkPacket<C>> packetProvider,
      Consumer<WritableNetworkPacket<C>> serializedToChannelPacketHandler,
      ObjBoolConsumer<WritableNetworkPacket<C>> sentPacketHandler,
      int packetLengthHeaderSize,
      int packetIdHeaderSize) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        packetProvider,
        serializedToChannelPacketHandler,
        sentPacketHandler,
        packetLengthHeaderSize);
    this.packetIdHeaderSize = packetIdHeaderSize;
  }

  @Override
  protected boolean doSerialize(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    writeHeader(firstBuffer, packet.packetId(), packetIdHeaderSize);
    return super.doSerialize(packet, expectedLength, totalSize, firstBuffer, secondBuffer);
  }
}
