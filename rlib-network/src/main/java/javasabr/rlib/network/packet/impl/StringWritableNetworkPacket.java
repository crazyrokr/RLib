package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true, chain = false)
@RequiredArgsConstructor
public class StringWritableNetworkPacket extends AbstractWritableNetworkPacket {

  private final String data;

  @Override
  protected void writeImpl(ByteBuffer buffer) {
    super.writeImpl(buffer);
    writeLong(buffer, 0);
    writeLong(buffer, Long.MAX_VALUE);
    writeString(buffer, data);
    writeLong(buffer, Long.MAX_VALUE);
    writeLong(buffer, 0);
  }

  @Override
  public int expectedLength() {
    return 32 + 4 + data.length() * 2;
  }

  @Override
  public String toString() {
    return "StringWritablePacket[length:" + data.length() + "]";
  }
}
