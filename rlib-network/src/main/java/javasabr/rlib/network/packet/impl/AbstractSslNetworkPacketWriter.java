package javasabr.rlib.network.packet.impl;

import static javasabr.rlib.network.util.NetworkUtils.EMPTY_BUFFER;
import static javasabr.rlib.network.util.NetworkUtils.hexDump;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.functions.ObjBoolConsumer;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
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
import org.jspecify.annotations.Nullable;

@CustomLog
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractSslNetworkPacketWriter<W extends WritableNetworkPacket, C extends Connection<?, W>>
    extends AbstractNetworkPacketWriter<W, C> {

  private static final ByteBuffer[] EMPTY_BUFFERS = {
      NetworkUtils.EMPTY_BUFFER
  };

  final SSLEngine sslEngine;
  final Consumer<WritableNetworkPacket> queueAtFirst;

  @Getter(AccessLevel.PROTECTED)
  @Nullable
  volatile ByteBuffer sslTempBuffer;

  @Getter(AccessLevel.PROTECTED)
  volatile ByteBuffer sslNetworkBuffer;

  public AbstractSslNetworkPacketWriter(
      C connection,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      Runnable updateActivityFunction,
      Supplier<WritableNetworkPacket> packetProvider,
      Consumer<WritableNetworkPacket> serializedToChannelPacketHandler,
      ObjBoolConsumer<WritableNetworkPacket> sentPacketHandler,
      SSLEngine sslEngine,
      Consumer<WritableNetworkPacket> queueAtFirst) {
    super(
        connection,
        channel,
        bufferAllocator,
        updateActivityFunction,
        packetProvider,
        serializedToChannelPacketHandler,
        sentPacketHandler);
    this.sslEngine = sslEngine;
    this.queueAtFirst = queueAtFirst;
    this.sslNetworkBuffer = bufferAllocator.takeBuffer(sslEngine
        .getSession()
        .getPacketBufferSize());
  }

  @Override
  public boolean tryToSendNextPacket() {
    HandshakeStatus status = sslEngine.getHandshakeStatus();
    if (status == HandshakeStatus.NEED_UNWRAP) {
      return false;
    }
    return super.tryToSendNextPacket();
  }

  @Override
  protected boolean tryToSendNextPacketImpl() {
    HandshakeStatus status = sslEngine.getHandshakeStatus();
    if (SslUtils.isReadyToCrypt(status)) {
      return super.tryToSendNextPacketImpl();
    }

    ByteBuffer dataToSend = doHandshake(SslWritableNetworkPacket.getInstance());
    if (dataToSend != null) {
      return writeBuffer(dataToSend, null);
    }

    throw new IllegalStateException("Unexpected SSL state");
  }

  @Override
  protected ByteBuffer serialize(WritableNetworkPacket packet) {
    HandshakeStatus status = sslEngine.getHandshakeStatus();
    if (SslUtils.isReadyToCrypt(status)) {
      if (packet instanceof SslWritableNetworkPacket) {
        return EMPTY_BUFFER;
      }

      ByteBuffer serialized = super.serialize(packet);

      log.debug(remoteAddress(), serialized, (address, buff) ->
          "[%s] Try to encrypt data:\n%s".formatted(address, hexDump(buff)));

      ByteBuffer bufferToSend = sslNetworkBuffer();
      SSLEngineResult result = tryEncrypt(serialized, bufferToSend);

      if (result.getStatus() == SSLEngineResult.Status.OK && serialized.hasRemaining()) {
        int tempBufferSize = (int) ((bufferToSend.limit() + serialized.remaining()) * 1.2);
        ByteBuffer tempBuffer = bufferAllocator.takeBuffer(tempBufferSize);
        tempBuffer.put(bufferToSend.flip());
        while (serialized.hasRemaining()) {
          result = tryEncrypt(serialized, bufferToSend);
          if (result.getStatus() != SSLEngineResult.Status.OK) {
            break;
          }
          tempBuffer.put(bufferToSend.flip());
        }
        bufferToSend = tempBuffer;
        this.sslTempBuffer = tempBuffer;
      }

      return switch (result.getStatus()) {
        case BUFFER_UNDERFLOW -> increaseAndTryAgain(packet);
        case BUFFER_OVERFLOW -> throw new IllegalStateException("Unexpected ssl engine result");
        case OK -> bufferToSend.flip();
        case CLOSED -> closeAndReturn();
      };
    }

    ByteBuffer dataToSend = doHandshake(packet);
    if (dataToSend != null) {
      return dataToSend;
    }

    throw new IllegalStateException();
  }

  protected ByteBuffer closeAndReturn() {
    connection.close();
    return EMPTY_BUFFER;
  }

  protected ByteBuffer increaseAndTryAgain(WritableNetworkPacket packet) {
    log.debug(remoteAddress(), "[%s] Increase network buffer and try again..."::formatted);
    increaseNetworkBuffer();
    return serialize(packet);
  }

  protected SSLEngineResult tryEncrypt(ByteBuffer source, ByteBuffer destination) {
    try {
      return sslEngine.wrap(source, destination.clear());
    } catch (SSLException ex) {
      log.error(ex);
      throw new RuntimeException(ex);
    }
  }

  @Nullable
  protected ByteBuffer doHandshake(WritableNetworkPacket packet) {
    if (!(packet instanceof SslWritableNetworkPacket)) {
      log.debug(remoteAddress(), packet, "[%s] Return packet:[%s] to queue as first"::formatted);
      queueAtFirst.accept(packet);
    }

    HandshakeStatus handshakeStatus = sslEngine.getHandshakeStatus();
    while (SslUtils.needToProcess(handshakeStatus)) {
      SSLEngineResult result;
      switch (handshakeStatus) {
        case NEED_WRAP: {
          try {
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
/*              if (handshakeStatus == HandshakeStatus.NEED_WRAP) {
                log.debug("Send command to wrap data again");
                queueAtFirst.accept(SslWritableNetworkPacket.getInstance());
              }*/
              log.debug(sslNetworkBuffer, result, (buf, res) -> "Send wrapped data:\n" + hexDump(buf, res));
              return sslNetworkBuffer;
            case BUFFER_OVERFLOW: {
              log.debug(remoteAddress(), "[%s] Increase network buffer"::formatted);
              increaseNetworkBuffer();
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
          handshakeStatus = SslUtils.executeSslTasks(sslEngine);
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

  @Override
  protected void clearTempBuffers() {
    super.clearTempBuffers();
    ByteBuffer sslTempBuffer = this.sslTempBuffer;
    if (sslTempBuffer != null) {
      bufferAllocator.putBuffer(sslTempBuffer);
      this.sslTempBuffer = null;
    }
  }

  protected synchronized void increaseNetworkBuffer() {
    sslNetworkBuffer = NetworkUtils
        .increasePacketBuffer(sslNetworkBuffer, bufferAllocator, sslEngine);
  }

  @Override
  public void close() {
    sslEngine.closeOutbound();
    bufferAllocator.putBuffer(sslNetworkBuffer);
    super.close();
  }
}
