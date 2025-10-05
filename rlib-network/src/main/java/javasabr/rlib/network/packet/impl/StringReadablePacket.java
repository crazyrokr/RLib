package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StringReadablePacket extends AbstractReadableNetworkPacket {

  public static final int MAX_LENGTH = 100_000;

  @Nullable
  volatile String data;

  @Override
  protected void readImpl(ByteBuffer buffer) {
    readLong(buffer);
    readLong(buffer);
    this.data = readString(buffer, MAX_LENGTH);
    readLong(buffer);
    readLong(buffer);
  }

  @Override
  public String toString() {
    String data = data();
    if (data != null && data.length() > 20) {
      data = data.substring(0, 9) + "..." + data.substring(9, 19);
    }
    return "StringReadablePacket(" + "data='" + data + '\'' + ')';
  }
}
