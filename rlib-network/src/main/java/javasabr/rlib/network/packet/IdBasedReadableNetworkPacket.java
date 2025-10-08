package javasabr.rlib.network.packet;

import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.network.Connection;

/**
 * @author JavaSaBr
 */
public interface IdBasedReadableNetworkPacket<C extends Connection<C>>
    extends ReadableNetworkPacket<C>, IdBasedNetworkPacket<C> {

  /**
   * Create a new instance of this type.
   */
  default IdBasedReadableNetworkPacket<C> newInstance() {
    return ClassUtils.newInstance(getClass());
  }
}
