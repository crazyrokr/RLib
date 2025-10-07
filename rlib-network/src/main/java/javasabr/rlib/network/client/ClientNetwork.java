package javasabr.rlib.network.client;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * Interface to implement a client network.
 *
 * @author JavaSaBr
 */
public interface ClientNetwork<C extends Connection<?, ?, C>> extends Network<C> {

  /**
   * Connect to a server by the address.
   */
  C connect(InetSocketAddress serverAddress);

  /**
   * Connect to a server by the address.
   */
  CompletableFuture<C> connectAsync(InetSocketAddress serverAddress);

  /**
   * Connect to a server by the address.
   */
  Mono<C> connectReactive(InetSocketAddress serverAddress);

  /**
   * Get a current connection to a server or null.
   */
  @Nullable
  C currentConnection();

  /**
   * Get a current connection to a server or empty.
   */
  Optional<C> currentConnectionOptional();
}
