package javasabr.rlib.network.packet.impl;

import static javasabr.rlib.network.util.NetworkUtils.EMPTY_BUFFER;
import static javasabr.rlib.network.util.NetworkUtils.hexDump;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.functions.ObjBoolConsumer;
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
public abstract class AbstractNetworkPacketWriter<
    W extends WritableNetworkPacket<C>,
    C extends Connection<C>> implements NetworkPacketWriter {

  final CompletionHandler<Integer, @Nullable WritableNetworkPacket<C>> writeHandler = new CompletionHandler<>() {

    @Override
    public void completed(Integer result, @Nullable WritableNetworkPacket<C> packet) {
      handleSuccessfulWritingData(result, packet);
    }

    @Override
    public void failed(Throwable exc, @Nullable WritableNetworkPacket<C> packet) {
      handleFailedWritingData(exc, packet);
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
  final Supplier<@Nullable WritableNetworkPacket<C>> writablePacketProvider;
  final ObjBoolConsumer<WritableNetworkPacket<C>> sentPacketHandler;
  final Consumer<WritableNetworkPacket<C>> serializedToChannelPacketHandler;

  public AbstractNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel socketChannel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<@Nullable WritableNetworkPacket<C>> packetProvider,
      Consumer<WritableNetworkPacket<C>> serializedToChannelPacketHandler,
      ObjBoolConsumer<WritableNetworkPacket<C>> sentPacketHandler) {
    this.connection = connection;
    this.socketChannel = socketChannel;
    this.bufferAllocator = bufferAllocator;
    this.firstWriteBuffer = bufferAllocator.takeWriteBuffer();
    this.secondWriteBuffer = bufferAllocator.takeWriteBuffer();
    this.updateActivityFunction = updateActivityFunction;
    this.writablePacketProvider = packetProvider;
    this.serializedToChannelPacketHandler = serializedToChannelPacketHandler;
    this.sentPacketHandler = sentPacketHandler;
  }

  protected String remoteAddress() {
    return connection.remoteAddress();
  }

  @Override
  public boolean tryToSendNextPacket() {
    if (connection.closed() || !writing.compareAndSet(false, true)) {
      return false;
    }

    boolean startedWriting = false;

    try {
      startedWriting = tryToSendNextPacketImpl();
    } catch (Exception ex) {
      log.error(ex);
    }

    if (!startedWriting) {
      writing.set(false);
    }

    return startedWriting;
  }

  protected boolean tryToSendNextPacketImpl() {
    for (var nextPacket = writablePacketProvider.get();
         nextPacket != null; nextPacket = writablePacketProvider.get()) {
      ByteBuffer resultBuffer = serialize(nextPacket);
      boolean startedWriting = writeBuffer(resultBuffer, nextPacket);
      serializedToChannelPacketHandler.accept(nextPacket);
      if (startedWriting) {
        return true;
      }
    }
    return false;
  }

  protected boolean writeBuffer(
      ByteBuffer resultBuffer,
      @Nullable WritableNetworkPacket<C> nextPacket) {
    if (resultBuffer.limit() == 0) {
      return false;
    }
    writingBuffer = resultBuffer;
    log.debug(remoteAddress(), resultBuffer, (address, buff) -> "[%s] Write to channel data:\n" + hexDump(buff));
    socketChannel.write(resultBuffer, nextPacket, writeHandler);
    return true;
  }

  /**
   * Serializes the network packet to buffer.
   *
   * @param packet the network packet.
   * @return the final byte buffer with data.
   */
  protected ByteBuffer serialize(WritableNetworkPacket<C> packet) {

    if (packet instanceof WritablePacketWrapper<?, C> wrapper) {
      packet = wrapper.getPacket();
    }

    W resultPacket = (W) packet;

    int expectedLength = packet.expectedLength(connection);
    int totalSize = expectedLength == -1 ? -1 : totalSize(packet, expectedLength);

    // if the packet is too big to use a write buffer
    if (expectedLength != -1 && totalSize > firstWriteBuffer.capacity()) {
      ByteBuffer first = bufferAllocator.takeBuffer(totalSize);
      ByteBuffer second = bufferAllocator.takeBuffer(totalSize);
      firstWriteTempBuffer = first;
      secondWriteTempBuffer = second;
      try {
        return serialize(resultPacket, expectedLength, totalSize, first, second);
      } catch (BufferOverflowException ex) {
        log.error(ex);
        bufferAllocator.putBuffer(first);
        bufferAllocator.putBuffer(second);
        firstWriteTempBuffer = null;
        secondWriteTempBuffer = null;
        throw new RuntimeException(ex);
      }
    } else {
      try {
        return serialize(resultPacket, expectedLength, totalSize, firstWriteBuffer, secondWriteBuffer);
      } catch (BufferOverflowException ex) {
        log.error(ex);
        firstWriteBuffer.clear();
        secondWriteBuffer.clear();
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * Gets total size of the packet if it's possible.
   *
   * @return the total size or -1.
   */
  protected abstract int totalSize(WritableNetworkPacket<C> packet, int expectedLength);

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

    if (!onBeforeSerialize(packet, expectedLength, totalSize, firstBuffer, secondBuffer)) {
      return firstBuffer.clear().limit(0);
    } else if (!doSerialize(packet, expectedLength, totalSize, firstBuffer, secondBuffer)) {
      return firstBuffer.clear().limit(0);
    } else if (!onAfterSerialize(packet, expectedLength, totalSize, firstBuffer, secondBuffer)) {
      return firstBuffer.clear().limit(0);
    }

    return onSerializeResult(packet, expectedLength, totalSize, firstBuffer, secondBuffer);
  }

  /**
   * Handles the buffers before serializing packet's data.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return true if handling was successful.
   */
  protected boolean onBeforeSerialize(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    firstBuffer.clear();
    return true;
  }

  /**
   * Serializes the network packet data to the buffers.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return true if writing was successful.
   */
  protected boolean doSerialize(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    return packet.write(connection, firstBuffer);
  }

  /**
   * Handles the buffers after serializing packet's data.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return true if handling was successful.
   */
  protected boolean onAfterSerialize(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer firstBuffer,
      ByteBuffer secondBuffer) {
    firstBuffer.flip();
    return true;
  }

  /**
   * Handles the final result of serializing packet data and return the buffer which contains the data to send.
   *
   * @param packet the network packet.
   * @param expectedLength the packet's expected size.
   * @param totalSize the packet's total size.
   * @param firstBuffer the first buffer.
   * @param secondBuffer the second buffer.
   * @return the result buffer.
   */
  protected ByteBuffer onSerializeResult(
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
      log.error(remoteAddress(), position, headerSize, buffer,
          "[%s] Cannot write header by position:[%s] with header size:[%s] to buffer:[%s]"::formatted);
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
   * Handles successful writing part of network packet data.
   *
   * @param wroteBytes the count of wrote bytes.
   * @param packet the sent packet.
   */
  protected void handleSuccessfulWritingData(Integer wroteBytes, @Nullable WritableNetworkPacket<C> packet) {
    updateActivityFunction.run();

    if (wroteBytes == -1) {
      if (packet != null) {
        sentPacketHandler.accept(packet, false);
      }
      if (writing.compareAndSet(true, false)) {
        clearTempBuffers();
      }
      connection.close();
      return;
    }

    ByteBuffer writingBuffer = writingBuffer();
    if (writingBuffer.remaining() > 0) {
      log.debug(remoteAddress(), writingBuffer,
          "[%s] Buffer was not consumed fully, try to write else [%s] bytes to channel"::formatted);
      try {
        socketChannel.write(writingBuffer, packet, writeHandler);
      } catch (RuntimeException ex) {
        log.error(ex);
        if (writing.compareAndSet(true, false)) {
          clearTempBuffers();
        }
      }
      return;
    } else {
      log.debug(remoteAddress(), wroteBytes, "[%s] Finished writing [%s] bytes"::formatted);
    }

    if (packet != null) {
      sentPacketHandler.accept(packet, true);
    }

    if (writing.compareAndSet(true, false)) {
      clearTempBuffers();
      tryToSendNextPacket();
    }
  }

  /**
   * Handles the exception during writing the packet.
   *
   * @param exception the exception.
   * @param packet the packet.
   */
  protected void handleFailedWritingData(Throwable exception, @Nullable WritableNetworkPacket<C> packet) {
    log.error(new RuntimeException("Failed writing packet:" + packet, exception));
    if (exception instanceof IOException) {
      connection.close();
      return;
    }
    if (!connection.closed()) {
      if (writing.compareAndSet(true, false)) {
        clearTempBuffers();
        tryToSendNextPacket();
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
    writing.compareAndSet(true, false);
  }

  protected void clearTempBuffers() {
    this.writingBuffer = EMPTY_BUFFER;

    var firstWriteTempBuffer = this.firstWriteTempBuffer;
    if (firstWriteTempBuffer != null) {
      this.firstWriteTempBuffer = null;
      bufferAllocator.putBuffer(firstWriteTempBuffer);
    }

    var secondWriteTempBuffer = this.secondWriteTempBuffer;
    if (secondWriteTempBuffer != null) {
      this.secondWriteTempBuffer = null;
      bufferAllocator.putBuffer(secondWriteTempBuffer);
    }
  }
}
