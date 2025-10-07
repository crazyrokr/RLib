package javasabr.rlib.network;

import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;

public interface UnsafeConnection<
    R extends ReadableNetworkPacket<C>,
    W extends WritableNetworkPacket<C>,
    C extends Connection<R, W, C>> extends Connection<R, W, C> {

  void onConnected();
}
