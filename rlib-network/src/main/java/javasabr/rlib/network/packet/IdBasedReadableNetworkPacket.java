package javasabr.rlib.network.packet;

import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.network.Connection;

/**
 * @author JavaSaBr
 */
public interface IdBasedReadableNetworkPacket<S extends IdBasedReadableNetworkPacket<S>>
    extends ReadableNetworkPacket, IdBasedNetworkPacket {

  /**
   * Create a new instance of this type.
   */
  default S newInstance() {
    return ClassUtils.newInstance(getClass());
  }
}
