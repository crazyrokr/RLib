package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.common.function.NotNullBiConsumer;
import javasabr.rlib.common.function.NotNullConsumer;
import javasabr.rlib.common.function.NullableSupplier;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javax.net.ssl.SSLEngine;

/**
 * @author JavaSaBr
 */
public class DefaultSslNetworkPacketWriter<W extends WritableNetworkPacket, C extends Connection<?, W>> extends
    AbstractSslNetworkPacketWriter<W, C> {

  protected final int packetLengthHeaderSize;

  public DefaultSslNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      NullableSupplier<WritableNetworkPacket> nextWritePacketSupplier,
      NotNullConsumer<WritableNetworkPacket> writtenPacketHandler,
      NotNullBiConsumer<WritableNetworkPacket, Boolean> sentPacketHandler,
      SSLEngine sslEngine,
      NotNullConsumer<WritableNetworkPacket> packetWriter,
      NotNullConsumer<WritableNetworkPacket> queueAtFirst,
      int packetLengthHeaderSize) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        nextWritePacketSupplier,
        writtenPacketHandler,
        sentPacketHandler,
        sslEngine,
        packetWriter,
        queueAtFirst);
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
