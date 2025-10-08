package javasabr.rlib.network.packet.impl;

import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;

/**
 * @author JavaSaBr
 */
public abstract class AbstractIdBasedReadableNetworkPacket<C extends Connection<C>>
    extends AbstractReadableNetworkPacket<C> implements IdBasedReadableNetworkPacket<C> {
}
