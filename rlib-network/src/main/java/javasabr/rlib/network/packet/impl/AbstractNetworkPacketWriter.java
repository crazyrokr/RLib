package javasabr.rlib.network.packet.impl;

import static javasabr.rlib.network.util.NetworkUtils.EMPTY_BUFFER;
import static javasabr.rlib.network.util.NetworkUtils.hexDump;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@CustomLog
@RequiredArgsConstructor
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractNetworkPacketWriter<W extends WritableNetworkPacket, C extends Connection<?, W>> implements
    NetworkPacketWriter {


  final CompletionHandler<Integer, WritableNetworkPacket> writeHandler = new CompletionHandler<>() {

    @Override
    public void completed(Integer result, WritableNetworkPacket packet) {
      handleSuccessfulWriting(result, packet);
    }

    @Override
    public void failed(Throwable exc, WritableNetworkPacket packet) {
      handleFailedWriting(exc, packet);
    }
  };

  final AtomicBoolean writing = new AtomicBoolean();

  final C connection;
  final AsynchronousSocketChannel socketChannel;
  final BufferAllocator bufferAllocator;
  final ByteBuffer firstWriteBuffer;
  final ByteBuffer secondWriteBuffer;

  @Nullable
  volatile ByteBuffer firstWriteTempBuffer;
  @Nullable
  volatile ByteBuffer secondWriteTempBuffer;

  @Getter(AccessLevel.PROTECTED)
  volatile ByteBuffer writingBuffer = EMPTY_BUFFER;

  final Runnable updateActivityFunction;
  final Supplier<@Nullable WritableNetworkPacket> nextWritePacketSupplier;
  final BiConsumer<WritableNetworkPacket, Boolean> sentPacketHandler;
  final Consumer<WritableNetworkPacket> packetHandler;

  public AbstractNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel socketChannel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<@Nullable WritableNetworkPacket> packetProvider,
      Consumer<WritableNetworkPacket> packetHandler,
      BiConsumer<WritableNetworkPacket, Boolean> sentPacketHandler) {
    this.connection = connection;
    this.socketChannel = socketChannel;
    this.bufferAllocator = bufferAllocator;
    this.firstWriteBuffer = bufferAllocator.takeWriteBuffer();
    this.secondWriteBuffer = bufferAllocator.takeWriteBuffer();
    this.updateActivityFunction = updateActivityFunction;
    this.nextWritePacketSupplier = packetProvider;
    this.packetHandler = packetHandler;
    this.sentPacketHandler = sentPacketHandler;
  }

  @Override
  public void writeNextPacket() {

    if (connection.closed() || !writing.compareAndSet(false, true)) {
      return;
    }

    WritableNetworkPacket waitPacket = nextWritePacketSupplier.get();
    if (waitPacket == null) {
      writing.set(false);
      return;
    }

    ByteBuffer resultBuffer = serialize(waitPacket);

    if (resultBuffer.limit() != 0) {
      writingBuffer = resultBuffer;
      log.debug(connection.remoteAddress(), resultBuffer,
          (address, buf) -> "Write to channel:[%] data:\n" + hexDump(buf));
      socketChannel.write(resultBuffer, waitPacket, writeHandler);
    } else {
      writing.set(false);
    }

    packetHandler.accept(waitPacket);
  }

  /**
   * Serializes the network packet to buffer.
   *
   * @param packet the network packet.
   * @return the final byte buffer with data.
   */
  protected ByteBuffer serialize(WritableNetworkPacket packet) {

    if (packet instanceof WritablePacketWrapper) {
      packet = ((WritablePacketWrapper<?, ?>) packet).getPacket();
    }

    W resultPacket = (W) packet;

    int expectedLength = packet.expectedLength();
    int totalSize = expectedLength == -1 ? -1 : totalSize(packet, expectedLength);

    // if the packet is too big to use a write buffer
    if (expectedLength != -1 && totalSize > firstWriteBuffer.capacity()) {
      ByteBuffer first = bufferAllocator.takeBuffer(totalSize);
      ByteBuffer second = bufferAllocator.takeBuffer(totalSize);
      firstWriteTempBuffer = first;
      secondWriteTempBuffer = second;
      return serialize(resultPacket, expectedLength, totalSize, first, second);
    } else {
      return serialize(resultPacket, expectedLength, totalSize, firstWriteBuffer, secondWriteBuffer);
    }
  }

  /**
   * Gets total size of the packet if it's possible.
   *
   * @return the total size or -1.
   */
  protected abstract int totalSize(WritableNetworkPacket packet, int expectedLength);

  /**
   * Serializes the packet to the buffers.
   *
   * @param packet the network packet to serialize.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return the final buffer to write to channel.
   */
  protected ByteBuffer serialize(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {

    if (!onBeforeWrite(packet, expectedLength, totalSize, firstBuffer, secondBuffer)) {
      return firstBuffer.clear().limit(0);
    } else if (!write(packet, expectedLength, totalSize, firstBuffer, secondBuffer)) {
      return firstBuffer.clear().limit(0);
    } else if (!onAfterWrite(packet, expectedLength, totalSize, firstBuffer, secondBuffer)) {
      return firstBuffer.clear().limit(0);
    }

    return onResult(packet, expectedLength, totalSize, firstBuffer, secondBuffer);
  }

  /**
   * Handles the buffers before writing packet's data.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return true if handling was successful.
   */
  protected boolean onBeforeWrite(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    firstBuffer.clear();
    return true;
  }

  /**
   * Writes the network packet data to the buffers.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return true if writing was successful.
   */
  protected boolean write(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    return packet.write(firstBuffer);
  }

  /**
   * Handles the buffers after writing packet's data.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return true if handling was successful.
   */
  protected boolean onAfterWrite(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    firstBuffer.flip();
    return true;
  }

  /**
   * Handles the final result of writing packet data and return the buffer which contains the data to send.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return the result buffer.
   */
  protected ByteBuffer onResult(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    return firstBuffer.position(0);
  }

  protected ByteBuffer writeHeader(ByteBuffer buffer, int position, int value, int headerSize) {
    try {
      switch (headerSize) {
        case 1:
          buffer.put(position, (byte) value);
          break;
        case 2:
          buffer.putShort(position, (short) value);
          break;
        case 4:
          buffer.putInt(position, value);
          break;
        default:
          throw new IllegalStateException("Wrong packet's header size: " + headerSize);
      }
      return buffer;
    } catch (IndexOutOfBoundsException ex) {
      log.error(position, headerSize, buffer,
          "Cannot write header by position:[%s] with header size:[%s] to buffer:[%s]"::formatted);
      throw ex;
    }
  }

  protected ByteBuffer writeHeader(ByteBuffer buffer, int value, int headerSize) {
    switch (headerSize) {
      case 1:
        buffer.put((byte) value);
        break;
      case 2:
        buffer.putShort((short) value);
        break;
      case 4:
        buffer.putInt(value);
        break;
      default:
        throw new IllegalStateException("Wrong packet's header size: " + headerSize);
    }
    return buffer;
  }

  /**
   * Handles successful wrote data.
   *
   * @param wroteBytes the count of wrote bytes.
   * @param packet the sent packet.
   */
  protected void handleSuccessfulWriting(Integer wroteBytes, WritableNetworkPacket packet) {
    updateActivityFunction.run();

    if (wroteBytes == -1) {
      sentPacketHandler.accept(packet, Boolean.FALSE);
      connection.close();
      return;
    }

    ByteBuffer writingBuffer = writingBuffer();
    if (writingBuffer.remaining() > 0) {
      log.debug(writingBuffer, connection.remoteAddress(),
          "Buffer was not consumed fully, try to write else [%s] bytes to channel:[%s]"::formatted);
      socketChannel.write(writingBuffer, packet, writeHandler);
      return;
    } else {
      log.debug(wroteBytes, "Finished writing [%s] bytes"::formatted);
    }

    sentPacketHandler.accept(packet, Boolean.TRUE);

    if (writing.compareAndSet(true, false)) {
      // if we have temp buffers, we can remove it after finishing writing a packet
      if (firstWriteTempBuffer != null || secondWriteTempBuffer != null) {
        clearTempBuffers();
      }
      writeNextPacket();
    }
  }

  /**
   * Handles the exception during writing the packet.
   *
   * @param exception the exception.
   * @param packet the packet.
   */
  protected void handleFailedWriting(Throwable exception, WritableNetworkPacket packet) {
    log.error(new RuntimeException("Failed writing packet: " + packet, exception));
    if (!connection.closed()) {
      if (writing.compareAndSet(true, false)) {
        writeNextPacket();
      }
    }
  }

  @Override
  public void close() {

    bufferAllocator
        .putWriteBuffer(firstWriteBuffer)
        .putWriteBuffer(secondWriteBuffer);

    clearTempBuffers();

    writingBuffer = EMPTY_BUFFER;
  }

  protected void clearTempBuffers() {

    var secondWriteTempBuffer = this.secondWriteTempBuffer;
    var firstWriteTempBuffer = this.firstWriteTempBuffer;

    if (secondWriteTempBuffer != null) {
      this.secondWriteTempBuffer = null;
      bufferAllocator.putBuffer(secondWriteTempBuffer);
    }

    if (firstWriteTempBuffer != null) {
      this.firstWriteTempBuffer = null;
      bufferAllocator.putBuffer(firstWriteTempBuffer);
    }
  }
}
