package javasabr.rlib.network.packet.impl;

import static javasabr.rlib.network.util.NetworkUtils.hexDump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javasabr.rlib.network.util.NetworkUtils;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.experimental.FieldDefaults;

/**
 * @param <R> the readable packet's type.
 * @param <C> the connection's type.
 */
@CustomLog
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractSslNetworkPacketReader<R extends ReadableNetworkPacket, C extends Connection<R, ?>>
    extends AbstractNetworkPacketReader<R, C> {

  private static final ByteBuffer[] EMPTY_BUFFERS = {
      NetworkUtils.EMPTY_BUFFER
  };

  private static final int SKIP_READ_PACKETS = -1;

  final SSLEngine sslEngine;
  final Consumer<WritableNetworkPacket> packetWriter;

  volatile ByteBuffer sslNetworkBuffer;
  volatile ByteBuffer sslDataBuffer;

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
    this.sslNetworkBuffer = bufferAllocator.takeBuffer(sslEngine
        .getSession()
        .getPacketBufferSize());
    this.packetWriter = packetWriter;
  }

  @Override
  protected ByteBuffer bufferToReadFromChannel() {
    return sslNetworkBuffer;
  }

  @Override
  protected void handleReceivedData(Integer receivedBytes, ByteBuffer readingBuffer) {
    if (receivedBytes == -1) {
      doHandshake(readingBuffer, -1);
      return;
    }
    super.handleReceivedData(receivedBytes, readingBuffer);
  }

  @Override
  protected int readPackets(ByteBuffer receivedBuffer) {
    var handshakeStatus = sslEngine.getHandshakeStatus();
    // ssl engine is ready to decrypt
    if (handshakeStatus == HandshakeStatus.FINISHED || handshakeStatus == HandshakeStatus.NOT_HANDSHAKING) {
      return decryptAndRead(receivedBuffer);
    } else {
      return doHandshake(receivedBuffer, receivedBuffer.limit());
    }
  }

  protected int doHandshake(ByteBuffer receivedBuffer, int receivedBytes) {
    HandshakeStatus handshakeStatus = sslEngine.getHandshakeStatus();
    while (handshakeStatus != HandshakeStatus.FINISHED && handshakeStatus != HandshakeStatus.NOT_HANDSHAKING) {
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
            receivedBuffer.clear();
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
            case OK:
              break;
            case BUFFER_OVERFLOW:
              throw new IllegalStateException("Unexpected ssl engine result");
            case BUFFER_UNDERFLOW:
              log.debug("Increase ssl network buffer");
              increaseNetworkBuffer();
              break;
            case CLOSED:
              if (sslEngine.isOutboundDone()) {
                return SKIP_READ_PACKETS;
              } else {
                sslEngine.closeOutbound();
                handshakeStatus = sslEngine.getHandshakeStatus();
                break;
              }
            default:
              throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
          }
          break;
        }
        case NEED_WRAP: {
          log.debug("Send command to wrap data");
          packetWriter.accept(SslWritableNetworkPacket.getInstance());
          sslNetworkBuffer.clear();
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
            sslNetworkBuffer.clear();
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

      receivedBuffer.clear();

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
          total += readPackets(sslDataBuffer, pendingBuffer);
          break;
        }
        case BUFFER_OVERFLOW: {
          increaseDataBuffer();
          return decryptAndRead(receivedBuffer);
        }
        case CLOSED: {
          closeConnection();
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

  private void increaseNetworkBuffer() {
    sslNetworkBuffer = NetworkUtils.increasePacketBuffer(sslNetworkBuffer, bufferAllocator, sslEngine);
  }

  private void increaseDataBuffer() {
    sslDataBuffer = NetworkUtils.increaseApplicationBuffer(sslDataBuffer, bufferAllocator, sslEngine);
  }

  protected void closeConnection() {
    try {
      sslEngine.closeOutbound();
      socketChannel.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
