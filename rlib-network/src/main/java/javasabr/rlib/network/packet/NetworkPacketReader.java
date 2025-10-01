package javasabr.rlib.network.packet;

/**
 * @author JavaSaBr
 */
public interface NetworkPacketReader {

  /**
   * Activate a process of receiving packets.
   */
  void startRead();

  /**
   * Close all used resources.
   */
  void close();
}
