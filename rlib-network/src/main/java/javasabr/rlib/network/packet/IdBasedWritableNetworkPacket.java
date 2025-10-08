package javasabr.rlib.network.packet;

import javasabr.rlib.network.Connection;

/**
 * @author JavaSaBr
 */
public interface IdBasedWritableNetworkPacket<C extends Connection<C>>
    extends WritableNetworkPacket<C>, IdBasedNetworkPacket<C> {
}
