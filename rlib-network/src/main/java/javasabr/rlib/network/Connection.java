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
public interface Connection<R extends ReadableNetworkPacket, W extends WritableNetworkPacket> {

  record ReceivedPacketEvent<C extends Connection<?, ?>, R extends ReadableNetworkPacket>(
      C connection, R packet) {}

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
  void send(W packet);

  /**
   * Send a packet to connection's owner with async feedback of this sending.
   *
   * @return the async result with true if the packet was sent or false if sending was failed.
   */
  CompletableFuture<Boolean> sendWithFeedback(W packet);

  /**
   * Register a consumer to handle received packets.
   */
  void onReceive(BiConsumer<? super Connection<R, W>, ? super R> consumer);

  /**
   * Get a stream of received packet events.
   */
  Flux<ReceivedPacketEvent<? extends Connection<R, W>, ? extends R>> receivedEvents();

  /**
   * Get a stream of received packets.
   */
  Flux<? extends R> receivedPackets();
}
