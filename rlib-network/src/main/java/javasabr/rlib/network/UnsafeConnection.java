package javasabr.rlib.network;

import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;

public interface UnsafeConnection<R extends ReadableNetworkPacket, W extends WritableNetworkPacket>
    extends Connection<R, W> {

  void onConnected();
}
