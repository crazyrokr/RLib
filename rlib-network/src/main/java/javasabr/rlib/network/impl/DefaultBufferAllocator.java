package javasabr.rlib.network.impl;

import java.nio.ByteBuffer;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.NetworkConfig;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * The default byte buffer allocator.
 *
 * @author JavaSaBr
 */
@ToString
@CustomLog
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class DefaultBufferAllocator implements BufferAllocator {

  final NetworkConfig config;

  public DefaultBufferAllocator(NetworkConfig config) {
    this.config = config;
  }

  @Override
  public ByteBuffer takeReadBuffer() {
    int bufferSize = config.readBufferSize();
    log.debug(bufferSize, "Allocate new read buffer with size:[%s]"::formatted);
    return config.useDirectByteBuffer()
           ? ByteBuffer.allocateDirect(bufferSize)
           : ByteBuffer
               .allocate(bufferSize)
               .order(config.byteOrder())
               .clear();
  }

  @Override
  public ByteBuffer takePendingBuffer() {
    int bufferSize = config.pendingBufferSize();
    log.debug(bufferSize, "Allocate new pending buffer with size:[%s]"::formatted);
    return config.useDirectByteBuffer()
           ? ByteBuffer.allocateDirect(bufferSize)
           : ByteBuffer
               .allocate(bufferSize)
               .order(config.byteOrder())
               .clear();
  }

  @Override
  public ByteBuffer takeWriteBuffer() {
    int bufferSize = config.writeBufferSize();
    log.debug(bufferSize, "Allocate new write buffer with size:[%s]"::formatted);
    return config.useDirectByteBuffer()
           ? ByteBuffer.allocateDirect(bufferSize)
           : ByteBuffer
               .allocate(bufferSize)
               .order(config.byteOrder())
               .clear();
  }

  @Override
  public ByteBuffer takeBuffer(int bufferSize) {
    log.debug(bufferSize, "Allocate new buffer with size:[%s]"::formatted);
    return config.useDirectByteBuffer()
           ? ByteBuffer.allocateDirect(bufferSize)
           : ByteBuffer
               .allocate(bufferSize)
               .order(config.byteOrder())
               .clear();
  }

  @Override
  public DefaultBufferAllocator putReadBuffer(ByteBuffer buffer) {
    log.debug("Skip storing read buffer");
    return this;
  }

  @Override
  public DefaultBufferAllocator putPendingBuffer(ByteBuffer buffer) {
    log.debug("Skip storing pending buffer");
    return this;
  }

  @Override
  public DefaultBufferAllocator putWriteBuffer(ByteBuffer buffer) {
    log.debug("Skip storing write buffer");
    return this;
  }

  @Override
  public BufferAllocator putBuffer(ByteBuffer buffer) {
    log.debug("Skip storing buffer");
    return this;
  }
}
