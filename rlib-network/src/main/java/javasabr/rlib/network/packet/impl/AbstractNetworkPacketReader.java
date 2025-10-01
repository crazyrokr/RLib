package javasabr.rlib.network.packet.impl;

import static javasabr.rlib.common.util.ObjectUtils.notNull;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javasabr.rlib.common.util.BufferUtils;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.util.NetworkUtils;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @param <R> the readable packet's type.
 * @param <C> the connection's type.
 * @author JavaSaBr
 */
@CustomLog
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractNetworkPacketReader<R extends ReadableNetworkPacket, C extends Connection<R, ?>>
    implements NetworkPacketReader {

  final CompletionHandler<Integer, ByteBuffer> readChannelHandler = new CompletionHandler<>() {

    @Override
    public void completed(Integer receivedBytes, ByteBuffer readingBuffer) {
      handleReceivedData(receivedBytes, readingBuffer);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer readingBuffer) {
      handleFailedReceiving(exc, readingBuffer);
    }
  };

  final AtomicBoolean reading = new AtomicBoolean(false);

  final C connection;
  final AsynchronousSocketChannel socketChannel;
  final BufferAllocator bufferAllocator;

  final ByteBuffer readBuffer;
  final ByteBuffer pendingBuffer;

  final Runnable updateActivityFunction;
  final Consumer<? super R> packetHandler;

  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PROTECTED)
  @Nullable
  volatile ByteBuffer tempBigBuffer;

  final int maxPacketsByRead;

  protected AbstractNetworkPacketReader(
      C connection,
      AsynchronousSocketChannel socketChannel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Consumer<? super R> packetHandler,
      int maxPacketsByRead) {
    this.connection = connection;
    this.socketChannel = socketChannel;
    this.bufferAllocator = bufferAllocator;
    this.readBuffer = bufferAllocator.takeReadBuffer();
    this.pendingBuffer = bufferAllocator.takePendingBuffer();
    this.updateActivityFunction = updateActivityFunction;
    this.packetHandler = packetHandler;
    this.maxPacketsByRead = maxPacketsByRead;
  }

  protected ByteBuffer bufferToReadFromChannel() {
    return readBuffer;
  }

  @Override
  public void startRead() {
    if (!reading.compareAndSet(false, true)) {
      return;
    }
    log.debug(socketChannel, ch -> "Start waiting for new data from channel:[" + NetworkUtils.getRemoteAddress(ch) + "]");
    ByteBuffer buffer = bufferToReadFromChannel();
    socketChannel.read(buffer, buffer, readChannelHandler);
  }

  /**
   * Reads packets from the buffer with received data.
   *
   * @param receivedBuffer the buffer with received data.
   * @return count of read packets.
   */
  protected int readPackets(ByteBuffer receivedBuffer) {
    return readPackets(receivedBuffer, pendingBuffer);
  }

  /**
   * Reads packets from the buffer with received data.
   *
   * @param receivedBuffer the buffer with received data.
   * @param pendingBuffer the buffer with pending data from prev. received buffer.
   * @return count of read packets.
   */
  protected int readPackets(ByteBuffer receivedBuffer, ByteBuffer pendingBuffer) {
    log.debug(receivedBuffer, "Start reading packets from received buffer:[%s]"::formatted);

    int waitedBytes = pendingBuffer.position();
    ByteBuffer bufferToRead = receivedBuffer;
    ByteBuffer tempBigBuffer = tempBigBuffer();

    // if we have a temp big buffer it means that we are reading a big packet now
    if (tempBigBuffer != null) {
      // check and extends temp buffer if need
      if (tempBigBuffer.remaining() < receivedBuffer.remaining()) {
        reAllocTempBigBuffers(tempBigBuffer.flip(), tempBigBuffer.capacity());
        tempBigBuffer = notNull(tempBigBuffer());
      }

      log.debug(receivedBuffer, tempBigBuffer, "Put received buffer:[%s] to temp big buffer:[%s]"::formatted);
      bufferToRead = BufferUtils.putToAndFlip(tempBigBuffer, receivedBuffer);
    }
    // if we have some pending data we need to append the received buffer to the pending buffer
    // and start to read pending buffer with result received data
    else if (waitedBytes > 0) {
      if (pendingBuffer.remaining() < receivedBuffer.remaining()) {
        log.debug(
            pendingBuffer,
            receivedBuffer,
            "Pending buffer:[%s] is too small to append received buffer:[%s], allocate new temp bug buffer for this"::formatted);

        allocTempBigBuffers(pendingBuffer.flip(), pendingBuffer.capacity());

        log.debug(pendingBuffer, "Clear pending buffer:[%s]"::formatted);
        pendingBuffer.clear();

        tempBigBuffer = notNull(tempBigBuffer());

        log.debug(receivedBuffer, tempBigBuffer, "Put received buffer:[%s] to temp big buffer:[%s]"::formatted);
        bufferToRead = BufferUtils.putToAndFlip(tempBigBuffer, receivedBuffer);
      } else {
        log.debug(receivedBuffer, pendingBuffer, "Put received buffer:[%s] to pending buffer:[%s]"::formatted);
        bufferToRead = BufferUtils.putToAndFlip(pendingBuffer, receivedBuffer);
      }
    }

    int maxPacketsByRead = maxPacketsByRead();
    int readPackets = 0;
    int endPosition = 0;

    while (canStartReadPacket(bufferToRead) && readPackets < maxPacketsByRead) {
      // set position to start reading a next packet
      bufferToRead.position(endPosition);

      int positionBeforeRead = endPosition;
      int alreadyReadBytes = bufferToRead.position() - endPosition;
      int packetFullLength = readFullPacketLength(bufferToRead);
      int packetDataLength = calculatePacketDataLength(packetFullLength, alreadyReadBytes, bufferToRead);

      log.debug(positionBeforeRead, packetFullLength, "Found next packet from position:[%s] with length:[%s] "::formatted);

      // calculate position of the end of next packet
      endPosition += packetFullLength;

      // if the packet isn't full presented in this buffer
      if (packetFullLength == -1 || endPosition > bufferToRead.limit()) {
        bufferToRead.position(positionBeforeRead);

        // if we read the received buffer we need to put not yet read data to the pending
        // buffer or temp big buffer
        if (bufferToRead == receivedBuffer) {
          if (packetFullLength <= pendingBuffer.capacity()) {
            pendingBuffer.put(receivedBuffer);
            log.debug(pendingBuffer, "Put pending data form received buffer to pending buffer:[%s]"::formatted);
          } else {
            allocTempBigBuffers(receivedBuffer, packetFullLength);
          }
        }
        // if we already read this pending buffer we need to compact it
        else if (bufferToRead == pendingBuffer) {
          if (packetFullLength <= pendingBuffer.capacity()) {
            pendingBuffer.compact();
            log.debug(pendingBuffer, "Compact pending buffer:[%s]"::formatted);
          } else {
            allocTempBigBuffers(pendingBuffer, packetFullLength);
            log.debug(pendingBuffer, "Clear pending buffer:[%s]"::formatted);
            pendingBuffer.clear();
          }

        } else if (bufferToRead == tempBigBuffer) {
          // if not yet read data is less than pending buffer, then we can switch to use the pending buffer
          if (Math.max(packetFullLength, tempBigBuffer.remaining()) <= pendingBuffer.capacity()) {
            pendingBuffer.clear().put(tempBigBuffer);
            log.debug(pendingBuffer, "Moved pending data from temp big buffer to pending buffer:[%s]"::formatted);
            freeTempBigBuffers();
          }
          // if a new packet is bigger than current temp big buffer
          else if (packetFullLength > tempBigBuffer.capacity()) {
            reAllocTempBigBuffers(tempBigBuffer, packetFullLength);
          }
          // or just compact this current temp big buffer
          else {
            tempBigBuffer.compact();
            log.debug(tempBigBuffer, "Compact temp big buffer:[%s]]"::formatted);
          }
        }

        log.debug(
            readPackets,
            connection.remoteAddress(),
            ("Read [%s] packets from received buffer of [%s], "
                 + "but 1 packet is still waiting for receiving additional data.")::formatted);

        receivedBuffer.clear();
        return readPackets;
      }

      R readablePacket = createPacketFor(
          bufferToRead,
          positionBeforeRead,
          packetFullLength,
          packetDataLength);

      if (readablePacket != null) {
        log.debug(readablePacket, "Created instance of packet to read data:[%s]"::formatted);
        readAndHandlePacket(bufferToRead, packetDataLength, readablePacket);
        log.debug(readablePacket, "Finished reading data for packet:[%s]"::formatted);
        readPackets++;
      } else {
        log.warning("Cannot create any instance of packet to read data");
      }

      bufferToRead.position(endPosition);
    }

    if (bufferToRead.hasRemaining()) {
      if (bufferToRead == receivedBuffer) {
        pendingBuffer.put(receivedBuffer);
        log.debug("Found not yet read data from receive buffer, will put it to pending buffer.");
      } else {
        bufferToRead.compact();
      }
    } else if (bufferToRead == pendingBuffer) {
      pendingBuffer.clear();
    } else if (tempBigBuffer != null) {
      freeTempBigBuffers();
    }

    log.debug(readPackets, connection.remoteAddress(), "Read [%s] packets from received buffer of [%s]"::formatted);
    receivedBuffer.clear();
    return readPackets;
  }

  protected void readAndHandlePacket(ByteBuffer bufferToRead, int packetDataLength, R packetInstance) {
    if (packetInstance.read(bufferToRead, packetDataLength)) {
      packetHandler.accept(packetInstance);
    } else {
      log.error(packetInstance, "Packet:[%s] was read incorrectly"::formatted);
    }
  }

  /**
   * Checks the buffer's data.
   *
   * @return true if this buffer has enough data to start initial reading.
   */
  protected abstract boolean canStartReadPacket(ByteBuffer buffer);

  /**
   * Calculates size of packet data.
   *
   * @param packetLength the full packet length.
   * @param alreadyReadBytes the count of already read bytes from buffer to get packet length.
   * @param buffer the data buffer.
   * @return the length of packet data part.
   */
  protected int calculatePacketDataLength(int packetLength, int alreadyReadBytes, ByteBuffer buffer) {
    return packetLength - alreadyReadBytes;
  }

  /**
   * Gets the packet's full length of next packet in the buffer.
   *
   * @param buffer the buffer with received data.
   * @return the packet length or -1 if we have no enough data to read length.
   */
  protected abstract int readFullPacketLength(ByteBuffer buffer);

  protected void reAllocTempBigBuffers(ByteBuffer sourceBuffer, int fullPacketLength) {
    log.debug(sourceBuffer.capacity(), fullPacketLength, "Resize temp big buffer from:[%s] to:[%s]"::formatted);

    var newTempBuffer = bufferAllocator.takeBuffer(fullPacketLength + readBuffer.capacity());
    log.debug(sourceBuffer, newTempBuffer, "Moved data from old temp big buffer:[%s] to new:[%s]"::formatted);
    newTempBuffer.put(sourceBuffer);

    freeTempBigBuffers();
    this.tempBigBuffer = newTempBuffer;
  }

  protected void allocTempBigBuffers(ByteBuffer sourceBuffer, int fullPacketLength) {
    int notConsumeBytes = sourceBuffer.remaining();
    log.debug(
        notConsumeBytes,
        fullPacketLength,
        "Request temp big buffer to store part:[%s] of big packet with length:[%s]"::formatted);

    var tempBigBuffer = bufferAllocator.takeBuffer(fullPacketLength + readBuffer.capacity());
    log.debug(sourceBuffer, tempBigBuffer, "Put data from old temp big buffer:[%s] to new:[%s]"::formatted);
    tempBigBuffer.put(sourceBuffer);

    this.tempBigBuffer = tempBigBuffer;
  }

  protected void freeTempBigBuffers() {
    ByteBuffer tempBuffer = tempBigBuffer();
    if (tempBuffer != null) {
      tempBigBuffer(null);
      bufferAllocator.putBuffer(tempBuffer);
    }
  }

  /**
   * Handles received data from the channel.
   */
  protected void handleReceivedData(Integer receivedBytes, ByteBuffer readingBuffer) {
    updateActivityFunction.run();

    if (receivedBytes == -1) {
      connection.close();
      return;
    }

    log.debug(receivedBytes, connection.remoteAddress(), "Received [%s] bytes from channel:[%s]"::formatted);
    readingBuffer.flip();
    try {
      readPackets(readingBuffer);
    } catch (Exception e) {
      log.error(e);
    }

    if (reading.compareAndSet(true, false)) {
      startRead();
    }
  }

  /**
   * Handles the exception during receiving data from the channel.
   *
   * @param exception the exception.
   * @param readingBuffer the currently reading buffer.
   */
  protected void handleFailedReceiving(Throwable exception, ByteBuffer readingBuffer) {
    if (exception instanceof AsynchronousCloseException) {
      log.info(connection.remoteAddress(), "Connection:[%s] was closed"::formatted);
    } else {
      log.error(exception);
      connection.close();
    }
  }

  /**
   * Gets how many packets can be read by the one method call {@link #readPackets(ByteBuffer, ByteBuffer)}}.
   */
  protected int maxPacketsByRead() {
    return maxPacketsByRead;
  }

  protected int readHeader(ByteBuffer buffer, int headerSize) {
    switch (headerSize) {
      case 1:
        return buffer.get() & 0xFF;
      case 2:
        return buffer.getShort() & 0xFFFF;
      case 4:
        return buffer.getInt();
      default:
        throw new IllegalStateException("Wrong packet's header size: " + headerSize);
    }
  }

  /**
   * Creates a packet to read the received data.
   *
   * @param buffer the buffer with received data.
   * @param startPacketPosition the start position of the packet in the buffer.
   * @param packetFullLength the length of packet.
   * @param packetDataLength length of packet's data.
   * @return the readable packet.
   */
  @Nullable
  protected abstract R createPacketFor(
      ByteBuffer buffer,
      int startPacketPosition,
      int packetFullLength,
      int packetDataLength);

  @Override
  public void close() {

    bufferAllocator
        .putReadBuffer(readBuffer)
        .putPendingBuffer(pendingBuffer);

    freeTempBigBuffers();
  }
}
