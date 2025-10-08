package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import javasabr.rlib.network.Connection;
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
public class StringReadableNetworkPacket<C extends Connection<C>> extends AbstractReadableNetworkPacket<C> {

  public static final int MAX_LENGTH = 100_000;

  @Nullable
  volatile String data;

  @Override
  protected void readImpl(C connection, ByteBuffer buffer) {
    this.data = readString(buffer, MAX_LENGTH);
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
