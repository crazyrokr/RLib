package javasabr.rlib.io;

import java.io.IOException;

/**
 * @author JavaSaBr
 */
public interface ReusableStream {

  void reset() throws IOException;

  /**
   * Initialize this stream to use the buffer.
   *
   * @param buffer the buffer data.
   * @param offset the offset.
   * @param length the length.
   */
  void initFor(byte[] buffer, int offset, int length);
}
