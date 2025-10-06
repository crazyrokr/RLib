package javasabr.rlib.network;

import java.nio.ByteBuffer;
import org.jspecify.annotations.Nullable;

/**
 * The interface to implement a buffer allocator for network things.
 *
 * @author JavaSaBr
 */
public interface BufferAllocator {

  /**
   * Get a new read buffer to use.
   */
  ByteBuffer takeReadBuffer();

  /**
   * Get a new pending buffer to use.
   */
  ByteBuffer takePendingBuffer();

  /**
   * Get a new write buffer to use.
   */
  ByteBuffer takeWriteBuffer();

  /**
   * Get a new buffer with requested capacity.
   */
  ByteBuffer takeBuffer(int bufferSize);

  /**
   * Store an already used read buffer.
   */
  BufferAllocator putReadBuffer(ByteBuffer buffer);

  /**
   * Store an already used pending buffer.
   */
  BufferAllocator putPendingBuffer(ByteBuffer buffer);

  /**
   * Store an already used write buffer.
   */
  BufferAllocator putWriteBuffer(ByteBuffer buffer);

  /**
   * Store an already used byte buffer.
   */
  BufferAllocator putBuffer(ByteBuffer buffer);
}
