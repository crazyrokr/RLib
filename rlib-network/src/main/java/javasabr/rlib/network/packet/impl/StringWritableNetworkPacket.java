package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StringWritableNetworkPacket extends AbstractWritableNetworkPacket {

  String data;

  @Override
  protected void writeImpl(ByteBuffer buffer) {
    super.writeImpl(buffer);
    writeString(buffer, data);
  }

  @Override
  public int expectedLength() {
    return 4 + data.length() * 2;
  }

  @Override
  public String toString() {
    return "StringWritablePacket[length:" + data.length() + "]";
  }
}
