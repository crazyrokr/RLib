package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The writable packet wrapper with additional attachment.
 */
@Getter
@RequiredArgsConstructor
public class WritablePacketWrapper<A, C extends Connection<C>>
    implements WritableNetworkPacket<C> {

  private final A attachment;
  private final WritableNetworkPacket<C> packet;

  @Override
  public boolean write(C connection, ByteBuffer buffer) {
    return packet.write(connection, buffer);
  }

  @Override
  public int expectedLength(C connection) {
    return packet.expectedLength(connection);
  }

  @Override
  public String name() {
    return "WritablePacketWrapper";
  }
}
