package javasabr.rlib.network;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import reactor.core.publisher.Flux;

/**
 * The interface to implement an async connection.
 *
 * @author JavaSaBr
 */
public interface Connection<C extends Connection<C>> {

  record ReceivedPacketEvent<C, R>(C connection, R packet) {
    @Override
    public String toString() {
      return "[" + connection + "|" + packet + ']';
    }
  }

  /**
   * Get a remote address of this connection.
   */
  String remoteAddress();

  /**
   * Get a timestamp of last write/read activity.
   */
  long lastActivity();

  /**
   * Close this connection if this connection is still opened.
   */
  void close();

  /**
   * @return true if this connection is already closed.
   */
  boolean closed();

  /**
   * Send a packet to connection's owner.
   */
  void send(WritableNetworkPacket<C> packet);

  /**
   * Send a packet to connection's owner with async feedback of this sending.
   *
   * @return the async result with true if the packet was sent or false if sending was failed.
   */
  CompletableFuture<Boolean> sendWithFeedback(WritableNetworkPacket<C> packet);

  /**
   * Register a consumer to handle received packets.
   */
  void onReceive(BiConsumer<C, ? super ReadableNetworkPacket<C>> consumer);

  /**
   * Get a stream of received packet events.
   */
  Flux<ReceivedPacketEvent<C, ? extends ReadableNetworkPacket<C>>> receivedEvents();

  /**
   * Get a stream of received packet events with expected packet type.
   */
  default <R extends ReadableNetworkPacket<C>> Flux<ReceivedPacketEvent<C, R>> receivedEvents(Class<R> packetType) {
    return receivedEvents()
        .filter(event -> packetType.isInstance(event.packet()))
        .map(event -> (ReceivedPacketEvent<C, R>) event);
  }

  /**
   * Get a stream of received packets.
   */
  Flux<? extends ReadableNetworkPacket<C>> receivedPackets();

  /**
   * Get a stream of received packets with expected type.
   */
  default <R extends ReadableNetworkPacket<C>> Flux<R> receivedPackets(Class<R> packetType) {
    return receivedPackets()
        .filter(packetType::isInstance)
        .map(networkPacket -> (R) networkPacket);
  }
}
