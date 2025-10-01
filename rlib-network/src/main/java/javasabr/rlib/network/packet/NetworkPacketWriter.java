package javasabr.rlib.network.packet;

/**
 * @author JavaSaBr
 */
public interface NetworkPacketWriter {

  /**
   * @return true if the writer starting writing new data to channel.
   */
  boolean tryToSendNextPacket();

  /**
   * Close all used resources.
   */
  void close();
}
