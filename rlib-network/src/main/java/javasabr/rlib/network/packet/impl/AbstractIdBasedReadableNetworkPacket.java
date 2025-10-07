package javasabr.rlib.network.packet.impl;

import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;

/**
 * @author JavaSaBr
 */
public abstract class AbstractIdBasedReadableNetworkPacket<S extends AbstractIdBasedReadableNetworkPacket<S, C>, C extends Connection>
    extends AbstractReadableNetworkPacket<C> implements IdBasedReadableNetworkPacket<S, C> {
}
