package javasabr.rlib.network.packet.impl;

import javasabr.rlib.network.impl.DefaultConnection;
import javasabr.rlib.network.packet.IdBasedWritableNetworkPacket;

/**
 * @author JavaSaBr
 */
public class DefaultWritableNetworkPacket
    extends AbstractWritableNetworkPacket<DefaultConnection>
    implements IdBasedWritableNetworkPacket<DefaultConnection> {}
