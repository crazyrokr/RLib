package javasabr.rlib.network.packet.impl;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.CustomLog;

/**
 * The base implementation of the {@link WritableNetworkPacket}.
 *
 * @author JavaSaBr
 */
@CustomLog
public abstract class AbstractWritableNetworkPacket extends AbstractNetworkPacket
    implements WritableNetworkPacket {

  @Override
  public boolean write(ByteBuffer buffer) {
    try {
      writeImpl(buffer);
      return true;
    } catch (Exception e) {
      handleException(buffer, e);
      return false;
    }
  }

  /**
   * The process of writing this packet to the buffer.
   */
  protected void writeImpl(ByteBuffer buffer) {}

  /**
   * Write 1 byte to the buffer.
   */
  protected void writeByte(ByteBuffer buffer, int value) {
    buffer.put((byte) value);
  }

  /**
   * Write 2 bytes to the buffer.
   */
  protected void writeChar(ByteBuffer buffer, char value) {
    buffer.putChar(value);
  }

  /**
   * Write 2 bytes to the buffer.
   */
  protected void writeChar(ByteBuffer buffer, int value) {
    buffer.putChar((char) value);
  }

  /**
   * Write 4 bytes to the buffer.
   */
  protected void writeFloat(ByteBuffer buffer, float value) {
    buffer.putFloat(value);
  }

  /**
   * Write 4 bytes to the buffer.
   */
  protected void writeInt(ByteBuffer buffer, int value) {
    buffer.putInt(value);
  }

  /**
   * Write 8 bytes to the buffer.
   */
  protected void writeLong(ByteBuffer buffer, long value) {
    buffer.putLong(value);
  }

  /**
   * Writes 2 bytes to the buffer.
   */
  protected void writeShort(ByteBuffer buffer, int value) {
    buffer.putShort((short) value);
  }

  /**
   * Writes the string to the buffer.
   */
  protected void writeString(ByteBuffer buffer, String string) {
    try {
      writeInt(buffer, string.length());
      for (int i = 0, length = string.length(); i < length; i++) {
        buffer.putChar(string.charAt(i));
      }
    } catch (BufferOverflowException ex) {
      log.error(
          string.length(),
          buffer,
          "Cannot write string to buffer because the string is too long. String length:[%s], buffer:[%s]"::formatted);
      throw ex;
    }
  }

  /**
   * Write a data buffer to packet buffer.
   */
  protected void writeBuffer(ByteBuffer buffer, ByteBuffer data) {
    buffer.put(data.array(), data.position(), data.limit());
  }
}
