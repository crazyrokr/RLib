package javasabr.rlib.network.packet.impl;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;

/**
 * The base implementation of {@link ReadableNetworkPacket}.
 *
 * @author JavaSaBr
 */
@CustomLog
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractReadableNetworkPacket
    extends AbstractNetworkPacket implements ReadableNetworkPacket {

  @Override
  public boolean read(ByteBuffer buffer, int remainingDataLength) {
    int oldLimit = buffer.limit();
    int oldPosition = buffer.position();
    try {
      buffer.limit(oldPosition + remainingDataLength);
      readImpl(buffer);
      return true;
    } catch (Exception e) {
      buffer.position(oldPosition);
      handleException(buffer, e);
      return false;
    } finally {
      buffer.limit(oldLimit);
    }
  }

  protected void readImpl(ByteBuffer buffer) {}

  /**
   * Reads 1 byte from the buffer.
   */
  protected int readByte(ByteBuffer buffer) {
    return buffer.get();
  }

  /**
   * Fills byte array with data from the buffer.
   */
  protected void readBytes(ByteBuffer buffer, byte[] array) {
    buffer.get(array);
  }

  /**
   * Fills byte array with data from the buffer.
   */
  protected void readBytes(ByteBuffer buffer, byte[] array, int offset, int length) {
    buffer.get(array, offset, length);
  }

  /**
   * Reads 4 bytes from buffer.
   */
  protected float readFloat(ByteBuffer buffer) {
    return buffer.getFloat();
  }

  /**
   * Reads 8 bytes from buffer.
   */
  protected double readDouble(ByteBuffer buffer) {
    return buffer.getDouble();
  }

  /**
   * Reads 4 bytes from buffer.
   */
  protected int readInt(ByteBuffer buffer) {
    return buffer.getInt();
  }

  /**
   * Reads 8 bytes from buffer.
   */
  protected long readLong(ByteBuffer buffer) {
    return buffer.getLong();
  }

  /**
   * Reads 2 bytes from buffer.
   */
  protected int readShort(ByteBuffer buffer) {
    return buffer.getShort();
  }

  /**
   * Read a string from buffer.
   */
  protected String readString(ByteBuffer buffer, int maxLength) {
    int length = readInt(buffer);
    if (maxLength < length) {
      throw new IllegalArgumentException("Buffer contains too long string:[%s] with limit:[%s]".formatted(
          length,
          maxLength));
    }
    try {
      int requiredRemainingBytes = length * 2;
      if (requiredRemainingBytes > buffer.remaining()) {
        throw new IllegalArgumentException("Buffer:[%s] has no enough data for string:[%s]".formatted(buffer, length));
      }

      var chars = new char[length];

      for (int i = 0; i < length; i++) {
        chars[i] = buffer.getChar();
      }

      return new String(chars);
    } catch (OutOfMemoryError ex) {
      log.error(length, "Cannot read too long string:[%s] by memory reason"::formatted);
      throw ex;
    } catch (BufferUnderflowException ex) {
      log.error(length, buffer, "Cannot read too long string:[%s] by not enough data in buffer:[%s]"::formatted);
      throw ex;
    }
  }
}
