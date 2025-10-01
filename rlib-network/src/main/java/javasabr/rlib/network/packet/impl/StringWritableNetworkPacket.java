package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;

/**
 * @author JavaSaBr
 */
@RequiredArgsConstructor
public class StringWritableNetworkPacket extends AbstractWritableNetworkPacket {

  private final String data;

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
    return "StringWritablePacket {\n" + "\"\tdataLength\":" + data.length() + "\n}";
  }
}
