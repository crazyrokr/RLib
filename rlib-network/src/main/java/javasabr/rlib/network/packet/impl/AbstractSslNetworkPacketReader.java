package javasabr.rlib.network.packet.impl;

import static javasabr.rlib.network.util.NetworkUtils.hexDump;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import javasabr.rlib.common.util.BufferUtils;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javasabr.rlib.network.util.NetworkUtils;
import javasabr.rlib.network.util.SslUtils;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@CustomLog
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractSslNetworkPacketReader<R extends ReadableNetworkPacket, C extends Connection<R, ?>>
    extends AbstractNetworkPacketReader<R, C> {

  private static final ByteBuffer[] EMPTY_BUFFERS = {
      NetworkUtils.EMPTY_BUFFER
  };

  private static final int SKIP_READ_PACKETS = -1;

  final SSLEngine sslEngine;
  final Consumer<WritableNetworkPacket> packetWriter;

  @Getter(value = AccessLevel.PROTECTED)
  volatile ByteBuffer sslNetworkBuffer;
  @Getter(value = AccessLevel.PROTECTED)
  volatile ByteBuffer sslDataBuffer;
  @Getter(value = AccessLevel.PROTECTED)
  volatile ByteBuffer sslDataPendingBuffer;

  protected AbstractSslNetworkPacketReader(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Consumer<? super R> readPacketHandler,
      SSLEngine sslEngine,
      Consumer<WritableNetworkPacket> packetWriter,
      int maxPacketsByRead) {
    super(connection, channel, bufferAllocator, updateActivityFunction, readPacketHandler, maxPacketsByRead);
    this.sslEngine = sslEngine;
    this.sslDataBuffer = bufferAllocator.takeBuffer(sslEngine
        .getSession()
        .getApplicationBufferSize());
    this.sslDataPendingBuffer = bufferAllocator.takeBuffer(sslEngine
        .getSession()
        .getApplicationBufferSize() * 2);
    this.sslNetworkBuffer = bufferAllocator.takeBuffer(sslEngine
        .getSession()
        .getPacketBufferSize())
        .limit(0);
    this.packetWriter = packetWriter;
  }

  @Override
  protected void handleReceivedData(int receivedBytes, ByteBuffer readingBuffer) {
    if (receivedBytes == -1) {
      doHandshake(sslNetworkBuffer(), -1);
      return;
    }
    super.handleReceivedData(receivedBytes, readingBuffer);
  }

  @Override
  protected int readPackets(ByteBuffer readingBuffer) {
    ByteBuffer networkBuffer = moveDataToNetworkBuffer(readingBuffer);
    HandshakeStatus handshakeStatus = sslEngine.getHandshakeStatus();
    if (SslUtils.isReadyToDecrypt(handshakeStatus)) {
      return decryptAndRead(networkBuffer);
    } else {
      return doHandshake(networkBuffer, networkBuffer.limit());
    }
  }

  private ByteBuffer moveDataToNetworkBuffer(ByteBuffer readingBuffer) {
    ByteBuffer sslNetworkBuffer = sslNetworkBuffer();
    int freeSpace = sslNetworkBuffer.capacity() - sslNetworkBuffer.limit();
    if (freeSpace >= readingBuffer.limit()) {
      BufferUtils.appendAndClear(sslNetworkBuffer, readingBuffer);
    } else {
      sslNetworkBuffer = increaseNetworkBuffer(readingBuffer.limit());
      BufferUtils.appendAndClear(sslNetworkBuffer, readingBuffer);
    }
    return sslNetworkBuffer;
  }

  protected int doHandshake(ByteBuffer receivedBuffer, int receivedBytes) {
    HandshakeStatus handshakeStatus = sslEngine.getHandshakeStatus();
    while (SslUtils.needToProcess(handshakeStatus)) {
      log.debug(handshakeStatus, "Do handshake with status:[%s] "::formatted);
      SSLEngineResult result;
      switch (handshakeStatus) {
        case NEED_UNWRAP: {
          if (receivedBytes == -1) {
            if (sslEngine.isInboundDone() && sslEngine.isOutboundDone()) {
              return SKIP_READ_PACKETS;
            }
            try {
              sslEngine.closeInbound();
            } catch (SSLException e) {
              log.error("This engine was forced to close inbound, without having received the "
                  + "proper SSL/TLS close notification message from the peer, due to end of stream.");
            }
            sslEngine.closeOutbound();
            handshakeStatus = sslEngine.getHandshakeStatus();
            break;
          } else if (!receivedBuffer.hasRemaining()) {
            receivedBuffer.clear().limit(0);
            return SKIP_READ_PACKETS;
          }
          try {
            log.debug(receivedBuffer, buff -> "Try to unwrap data:\n" + hexDump(buff));
            result = sslEngine.unwrap(receivedBuffer, EMPTY_BUFFERS);
            handshakeStatus = result.getHandshakeStatus();
            log.debug(handshakeStatus, "Handshake status:[%s] after unwrapping"::formatted);
          } catch (SSLException sslException) {
            log.error("A problem was encountered while processing the data that caused the "
                + "SSLEngine to abort. Will try to properly close connection...");
            sslEngine.closeOutbound();
            handshakeStatus = sslEngine.getHandshakeStatus();
            break;
          }
          switch (result.getStatus()) {
            case OK: {
              break;
            }
            case BUFFER_OVERFLOW: {
              throw new IllegalStateException("Unexpected ssl engine result");
            }
            case BUFFER_UNDERFLOW: {
              log.debug("Increase ssl network buffer");
              increaseNetworkBuffer(0);
              break;
            }
            case CLOSED: {
              if (sslEngine.isOutboundDone()) {
                return SKIP_READ_PACKETS;
              } else {
                sslEngine.closeOutbound();
                handshakeStatus = sslEngine.getHandshakeStatus();
                break;
              }
            }
            default: {
              throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
          }
          break;
        }
        case NEED_WRAP: {
          log.debug("Send command to wrap data");
          packetWriter.accept(SslWritableNetworkPacket.getInstance());
          receivedBuffer.clear().limit(0);
          return SKIP_READ_PACKETS;
        }
        case NEED_TASK: {
          Runnable task;
          while ((task = sslEngine.getDelegatedTask()) != null) {
            log.debug(task, "Execute SSL Engine's task:[%s]"::formatted);
            task.run();
          }
          handshakeStatus = sslEngine.getHandshakeStatus();
          log.debug(handshakeStatus, "Handshake status:[%s] after engine tasks"::formatted);
          if (handshakeStatus == HandshakeStatus.NEED_UNWRAP && !receivedBuffer.hasRemaining()) {
            receivedBuffer.clear().limit(0);
            return SKIP_READ_PACKETS;
          }
          break;
        }
        default: {
          throw new IllegalStateException("Invalid SSL status: " + handshakeStatus);
        }
      }
    }

    if (!receivedBuffer.hasRemaining()) {
      // if buffer is empty and status is FINISHED then we can notify writer
      if (handshakeStatus == HandshakeStatus.FINISHED) {
        packetWriter.accept(SslWritableNetworkPacket.getInstance());
      }
      receivedBuffer.clear().limit(0);
      return SKIP_READ_PACKETS;
    }

    return decryptAndRead(receivedBuffer);
  }

  protected int decryptAndRead(ByteBuffer receivedBuffer) {
    int total = 0;
    while (receivedBuffer.hasRemaining()) {
      SSLEngineResult result;
      try {
        log.debug(receivedBuffer, buf -> "Try to decrypt data:\n" + hexDump(buf));
        result = sslEngine.unwrap(receivedBuffer, sslDataBuffer.clear());
      } catch (SSLException e) {
        throw new IllegalStateException(e);
      }
      switch (result.getStatus()) {
        case OK: {
          sslDataBuffer.flip();
          log.debug(sslDataBuffer, buf -> "Decrypted data:\n" + hexDump(buf));
          total += readPackets(sslDataBuffer, sslDataPendingBuffer);
          break;
        }
        case BUFFER_OVERFLOW: {
          increaseDataBuffer();
          return decryptAndRead(receivedBuffer);
        }
        case BUFFER_UNDERFLOW: {
          if (receivedBuffer.position() > 0) {
            receivedBuffer
                .compact()
                .limit(receivedBuffer.position());
          }
          return SKIP_READ_PACKETS;
        }
        case CLOSED: {
          connection.close();
          return SKIP_READ_PACKETS;
        }
        default: {
          if (receivedBuffer.position() > 0) {
            receivedBuffer.compact();
            return total;
          }
          throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
        }
      }
    }

    receivedBuffer.clear();
    return total;
  }

  private synchronized ByteBuffer increaseNetworkBuffer(int extra) {
    ByteBuffer current = sslNetworkBuffer();
    int newSize = (int) Math.max(current.capacity() * 1.3, current.capacity() + extra);
    sslNetworkBuffer = NetworkUtils
        .increaseBuffer(current, bufferAllocator, newSize)
        .flip();
    return sslNetworkBuffer;
  }

  private synchronized void increaseDataBuffer() {
    int newSize = sslEngine
        .getSession()
        .getApplicationBufferSize();
    sslDataBuffer = NetworkUtils
        .increaseBuffer(sslDataBuffer, bufferAllocator, newSize)
        .flip();
    sslDataPendingBuffer = NetworkUtils
        .increaseBuffer(sslDataPendingBuffer, bufferAllocator, newSize * 2)
        .flip();
  }

  @Override
  public void close() {
    sslEngine.closeOutbound();
    bufferAllocator
        .putBuffer(sslDataBuffer)
        .putBuffer(sslDataPendingBuffer)
        .putBuffer(sslNetworkBuffer);
    super.close();
  }
}
