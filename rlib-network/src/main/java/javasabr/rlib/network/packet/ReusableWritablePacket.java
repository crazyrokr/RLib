package javasabr.rlib.network.packet;

import javasabr.rlib.network.Connection;
import javasabr.rlib.reusable.Reusable;
import javasabr.rlib.reusable.pool.Pool;

/**
 * The interface to implement a reusable writable packet.
 *
 * @author JavaSaBr
 */
public interface ReusableWritablePacket<C extends Connection<C>> extends WritableNetworkPacket<C>, Reusable {

  /**
   * Handle completion of packet sending.
   */
  void complete();

  /**
   * Force complete this packet.
   */
  void forceComplete();

  /**
   * Decrease sending count.
   */
  void decreaseSends();

  /**
   * Decrease sending count.
   *
   * @param count the count.
   */
  void decreaseSends(int count);

  /**
   * Increase sending count.
   */
  void increaseSends();

  /**
   * Increase sending count.
   *
   * @param count the count.
   */
  void increaseSends(int count);

  /**
   * Set the pool.
   *
   * @param pool the pool to store used packet.
   */
  void setPool(Pool<ReusableWritablePacket<C>> pool);

  default void notifyAddedToSend() {
    increaseSends();
  }
}
