package javasabr.rlib.network.packet.impl;

import static javasabr.rlib.network.util.NetworkUtils.EMPTY_BUFFER;
import static javasabr.rlib.network.util.NetworkUtils.hexDump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javasabr.rlib.network.util.NetworkUtils;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

@CustomLog
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractSslNetworkPacketWriter<W extends WritableNetworkPacket, C extends Connection<?, W>>
    extends AbstractNetworkPacketWriter<W, C> {

  private static final ByteBuffer[] EMPTY_BUFFERS = {
      NetworkUtils.EMPTY_BUFFER
  };

  final SSLEngine sslEngine;
  final Consumer<WritableNetworkPacket> packetWriter;
  final Consumer<WritableNetworkPacket> queueAtFirst;

  protected volatile ByteBuffer sslNetworkBuffer;

  public AbstractSslNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<WritableNetworkPacket> packetProvider,
      Consumer<WritableNetworkPacket> writtenPacketHandler,
      BiConsumer<WritableNetworkPacket, Boolean> sentPacketHandler,
      SSLEngine sslEngine,
      Consumer<WritableNetworkPacket> packetWriter,
      Consumer<WritableNetworkPacket> queueAtFirst) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        packetProvider,
        writtenPacketHandler,
        sentPacketHandler);
    this.sslEngine = sslEngine;
    this.packetWriter = packetWriter;
    this.queueAtFirst = queueAtFirst;
    this.sslNetworkBuffer = bufferAllocator.takeBuffer(sslEngine
        .getSession()
        .getPacketBufferSize());
  }

  @Override
  public void writeNextPacket() {
    HandshakeStatus status = sslEngine.getHandshakeStatus();
    if (status == HandshakeStatus.NEED_UNWRAP) {
      return;
    }
    super.writeNextPacket();
  }

  @Override
  protected ByteBuffer serialize(WritableNetworkPacket packet) {
    HandshakeStatus status = sslEngine.getHandshakeStatus();
    if (status == HandshakeStatus.FINISHED || status == HandshakeStatus.NOT_HANDSHAKING) {
      if (packet instanceof SslWritableNetworkPacket) {
        return EMPTY_BUFFER;
      }

      ByteBuffer packetDataBuffer = super.serialize(packet);
      log.debug(packetDataBuffer, buff -> "Try to encrypt data:\n" + hexDump(buff));

      SSLEngineResult result;
      try {
        result = sslEngine.wrap(packetDataBuffer, sslNetworkBuffer.clear());
      } catch (SSLException e) {
        throw new RuntimeException(e);
      }

      switch (result.getStatus()) {
        case BUFFER_UNDERFLOW: {
          increaseNetworkBuffer();
          break;
        }
        case BUFFER_OVERFLOW: {
          throw new IllegalStateException("Unexpected ssl engine result");
        }
        case OK: {
          return sslNetworkBuffer.flip();
        }
        case CLOSED: {
          closeConnection();
          return EMPTY_BUFFER;
        }
      }
    }

    ByteBuffer bufferToWrite = doHandshake(packet);
    if (bufferToWrite != null) {
      return bufferToWrite;
    }

    throw new IllegalStateException();
  }

  @Nullable
  protected ByteBuffer doHandshake(WritableNetworkPacket packet) {
    if (!(packet instanceof SslWritableNetworkPacket)) {
      log.debug(packet, "Return packet:[%s] to queue as first"::formatted);
      queueAtFirst.accept(packet);
    }

    HandshakeStatus handshakeStatus = sslEngine.getHandshakeStatus();

    while (handshakeStatus != HandshakeStatus.FINISHED && handshakeStatus != HandshakeStatus.NOT_HANDSHAKING) {
      SSLEngineResult result;
      switch (handshakeStatus) {
        case NEED_WRAP: {
          try {
            // check result
            result = sslEngine.wrap(EMPTY_BUFFERS, sslNetworkBuffer.clear());
            handshakeStatus = result.getHandshakeStatus();
          } catch (SSLException sslException) {
            log.error("A problem was encountered while processing the data that caused the SSLEngine "
                + "to abort. Will try to properly close connection...");
            sslEngine.closeOutbound();
            handshakeStatus = sslEngine.getHandshakeStatus();
            break;
          }
          switch (result.getStatus()) {
            case OK:
              sslNetworkBuffer.flip();
              if (handshakeStatus == HandshakeStatus.NEED_WRAP) {
                log.debug("Send command to wrap data again");
                queueAtFirst.accept(SslWritableNetworkPacket.getInstance());
              }
              log.debug(sslNetworkBuffer, result, (buf, res) -> "Send wrapped data:\n" + hexDump(buf, res));
              return sslNetworkBuffer;
            case BUFFER_OVERFLOW: {
              sslNetworkBuffer = NetworkUtils.enlargePacketBuffer(bufferAllocator, sslEngine);
              break;
            }
            case BUFFER_UNDERFLOW: {
              throw new IllegalStateException("Unexpected ssl engine result");
            }
            case CLOSED: {
              try {
                return EMPTY_BUFFER;
              } catch (Exception e) {
                log.error("Failed to send server's CLOSE message due to socket channel's failure.");
                handshakeStatus = sslEngine.getHandshakeStatus();
              }
              break;
            }
            default: {
              throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
          }
          break;
        }
        case NEED_TASK: {
          Runnable task;
          while ((task = sslEngine.getDelegatedTask()) != null) {
            log.debug(task, "Execute SSL Engine's task:[%s]"::formatted);
            task.run();
          }
          handshakeStatus = sslEngine.getHandshakeStatus();
          break;
        }
        case NEED_UNWRAP: {
          break;
        }
        default: {
          throw new IllegalStateException("Invalid SSL status: " + handshakeStatus);
        }
      }
    }

    return EMPTY_BUFFER;
  }

  private void increaseNetworkBuffer() {
    sslNetworkBuffer = NetworkUtils.increasePacketBuffer(sslNetworkBuffer, bufferAllocator, sslEngine);
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
