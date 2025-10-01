package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.common.function.NotNullBiConsumer;
import javasabr.rlib.common.function.NotNullConsumer;
import javasabr.rlib.common.function.NullableSupplier;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.IdBasedWritableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;

/**
 * @author JavaSaBr
 */
public class IdBasedNetworkPacketWriter<W extends IdBasedWritableNetworkPacket, C extends Connection<?, W>> extends
    DefaultNetworkPacketWriter<W, C> {

  protected final int packetIdHeaderSize;

  public IdBasedNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      NullableSupplier<WritableNetworkPacket> nextWritePacketSupplier,
      NotNullConsumer<WritableNetworkPacket> writtenPacketHandler,
      NotNullBiConsumer<WritableNetworkPacket, Boolean> sentPacketHandler,
      int packetLengthHeaderSize,
      int packetIdHeaderSize) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        nextWritePacketSupplier,
        writtenPacketHandler,
        sentPacketHandler,
        packetLengthHeaderSize);
    this.packetIdHeaderSize = packetIdHeaderSize;
  }

  @Override
  protected boolean write(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    writeHeader(firstBuffer, packet.packetId(), packetIdHeaderSize);
    return super.write(packet, expectedLength, totalSize, firstBuffer, secondBuffer);
  }
}
