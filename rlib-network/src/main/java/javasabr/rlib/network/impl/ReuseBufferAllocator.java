package javasabr.rlib.network.impl;

import java.nio.ByteBuffer;
import java.util.function.Function;
import javasabr.rlib.collections.array.ArrayFactory;
import javasabr.rlib.collections.array.LockableArray;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.NetworkConfig;
import javasabr.rlib.reusable.pool.Pool;
import javasabr.rlib.reusable.pool.PoolFactory;
import lombok.CustomLog;
import lombok.ToString;

/**
 * @author JavaSaBr
 */
@ToString
@CustomLog
public class ReuseBufferAllocator implements BufferAllocator {

  protected final Pool<ByteBuffer> readBufferPool;
  protected final Pool<ByteBuffer> pendingBufferPool;
  protected final Pool<ByteBuffer> writeBufferPool;
  protected final LockableArray<ByteBuffer> byteBuffers;

  protected final NetworkConfig config;

  public ReuseBufferAllocator(NetworkConfig config) {
    this.config = config;
    this.readBufferPool = PoolFactory.newLockBasePool(ByteBuffer.class);
    this.pendingBufferPool = PoolFactory.newLockBasePool(ByteBuffer.class);
    this.writeBufferPool = PoolFactory.newLockBasePool(ByteBuffer.class);
    this.byteBuffers = ArrayFactory.stampedLockBasedArray(ByteBuffer.class);
  }

  @Override
  public ByteBuffer takeReadBuffer() {
    return readBufferPool
        .take(config, readBufferFactory())
        .clear();
  }

  @Override
  public ByteBuffer takePendingBuffer() {
    return pendingBufferPool
        .take(config, pendingBufferFactory())
        .clear();
  }

  @Override
  public ByteBuffer takeWriteBuffer() {
    return writeBufferPool
        .take(config, writeBufferFactory())
        .clear();
  }

  protected Function<NetworkConfig, ByteBuffer> pendingBufferFactory() {
    return config -> {
      var bufferSize = config.pendingBufferSize();
      log.debug(bufferSize, "Allocate new pending buffer with size:[%s]"::formatted);
      return config.useDirectByteBuffer()
             ? ByteBuffer.allocateDirect(bufferSize)
             : ByteBuffer
                 .allocate(bufferSize)
                 .order(config.byteOrder())
                 .clear();
    };
  }

  protected Function<NetworkConfig, ByteBuffer> readBufferFactory() {
    return config -> {
      var bufferSize = config.readBufferSize();
      log.debug(bufferSize, "Allocate new read buffer with size:[%s]"::formatted);
      return config.useDirectByteBuffer()
             ? ByteBuffer.allocateDirect(bufferSize)
             : ByteBuffer
                 .allocate(bufferSize)
                 .order(config.byteOrder())
                 .clear();
    };
  }

  protected Function<NetworkConfig, ByteBuffer> writeBufferFactory() {
    return config -> {
      var bufferSize = config.writeBufferSize();
      log.debug(bufferSize, "Allocate new write buffer with size:[%s]"::formatted);
      return config.useDirectByteBuffer()
             ? ByteBuffer.allocateDirect(bufferSize)
             : ByteBuffer
                 .allocate(bufferSize)
                 .order(config.byteOrder())
                 .clear();
    };
  }

  @Override
  public ByteBuffer takeBuffer(int bufferSize) {

    // check of existing enough buffer for the size under read lock
    ByteBuffer exist;

    long stamp = byteBuffers.readLock();
    try {
      exist = byteBuffers
          .iterations()
          .findAny(bufferSize, (buffer, require) -> buffer.capacity() >= require);
    } finally {
      byteBuffers.readUnlock(stamp);
    }

    // if we already possible have this buffer we need to take it under write lock
    if (exist != null) {
      stamp = byteBuffers.writeLock();
      try {

        // re-find enough buffer again
        exist = byteBuffers
            .iterations()
            .findAny(bufferSize, (buffer, require) -> buffer.capacity() >= require);

        // take it from pool if exist
        if (exist != null && byteBuffers.remove(exist)) {
          log.debug(exist, buffer -> "Reuse old buffer: " + buffer + " - (" + buffer.hashCode() + ")");
          return exist;
        }

      } finally {
        byteBuffers.writeUnlock(stamp);
      }
    }

    log.debug(bufferSize, "Allocate a new buffer with size:[%s]"::formatted);
    return config.useDirectByteBuffer()
           ? ByteBuffer.allocateDirect(bufferSize)
           : ByteBuffer
               .allocate(bufferSize)
               .order(config.byteOrder())
               .clear();
  }

  @Override
  public ReuseBufferAllocator putReadBuffer(ByteBuffer buffer) {
    readBufferPool.put(buffer);
    return this;
  }

  @Override
  public ReuseBufferAllocator putPendingBuffer(ByteBuffer buffer) {
    pendingBufferPool.put(buffer);
    return this;
  }

  @Override
  public ReuseBufferAllocator putWriteBuffer(ByteBuffer buffer) {
    writeBufferPool.put(buffer);
    return this;
  }

  @Override
  public BufferAllocator putBuffer(ByteBuffer buffer) {
    log.debug(buffer, buf -> "Save used temp buffer: " + buf + " - (" + buf.hashCode() + ")");
    long stamp = byteBuffers.writeLock();
    try {
      byteBuffers.add(buffer.clear());
    } finally {
      byteBuffers.writeUnlock(stamp);
    }
    return this;
  }
}
