package javasabr.rlib.network.packet.impl;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
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
public abstract class AbstractReadableNetworkPacket<C extends Connection<C>>
    extends AbstractNetworkPacket<C> implements ReadableNetworkPacket<C> {

  @Override
  public boolean read(C connection, ByteBuffer buffer, int remainingDataLength) {
    int oldLimit = buffer.limit();
    int oldPosition = buffer.position();
    try {
      buffer.limit(oldPosition + remainingDataLength);
      readImpl(connection, buffer);
      return true;
    } catch (Exception e) {
      buffer.position(oldPosition);
      handleException(connection, buffer, e);
      return false;
    } finally {
      buffer.limit(oldLimit);
    }
  }

  protected void readImpl(C connection, ByteBuffer buffer) {}

  /**
   * Reads 1 byte from the buffer.
   */
  protected int readByte(ByteBuffer buffer) {
    return buffer.get();
  }

  /**
   * Reads 1 byte from the buffer.
   */
  protected int readByteUnsigned(ByteBuffer buffer) {
    return buffer.get() & 0xFF;
  }

  /**
   * Fills byte array with data from the buffer.
   */
  protected byte[] readByteArray(ByteBuffer buffer) {
    int size = readInt(buffer);
    byte[] result = new byte[size];
    buffer.get(result);
    return result;
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
   * Reads 4 bytes from buffer.
   */
  protected long readIntUnsigned(ByteBuffer buffer) {
    return buffer.getInt() & 0xFFFFFFFFL;
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
   * Reads 2 bytes from buffer.
   */
  protected int readShortUnsigned(ByteBuffer buffer) {
    return buffer.getShort() & 0xFFFF;
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
