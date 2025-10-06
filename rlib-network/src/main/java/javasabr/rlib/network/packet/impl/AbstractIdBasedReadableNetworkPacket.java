package javasabr.rlib.network.packet.impl;

import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;

/**
 * @author JavaSaBr
 */
public abstract class AbstractIdBasedReadableNetworkPacket<S extends AbstractIdBasedReadableNetworkPacket<S>>
    extends AbstractReadableNetworkPacket implements IdBasedReadableNetworkPacket<S> {
}
