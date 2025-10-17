package javasabr.rlib.network.packet.impl;

import javasabr.rlib.network.Connection;
import javasabr.rlib.network.packet.IdBasedWritableNetworkPacket;

/**
 * @author JavaSaBr
 */
public class DefaultWritableNetworkPacket<C extends Connection<C>> extends AbstractWritableNetworkPacket<C>
    implements IdBasedWritableNetworkPacket<C> {}
