package javasabr.rlib.network.client.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import javasabr.rlib.common.util.AsyncUtils;
import javasabr.rlib.common.util.ThreadUtils;
import javasabr.rlib.common.util.Utils;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.NetworkConfig;
import javasabr.rlib.network.client.ClientNetwork;
import javasabr.rlib.network.impl.AbstractNetwork;
import javasabr.rlib.network.util.NetworkUtils;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * The default implementation of a client network.
 *
 * @author JavaSaBr
 */
@CustomLog
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultClientNetwork<C extends Connection<?, ?, C>> extends AbstractNetwork<C>
    implements ClientNetwork<C> {

  final AtomicBoolean connecting;

  @Nullable
  @Getter(AccessLevel.PROTECTED)
  volatile CompletableFuture<C> pendingConnection;

  @Getter
  @Nullable
  volatile C currentConnection;

  public DefaultClientNetwork(
      NetworkConfig config,
      BiFunction<Network<C>, AsynchronousSocketChannel, C> channelToConnection) {
    super(config, channelToConnection);
    this.connecting = new AtomicBoolean(false);
    log.info(config, DefaultClientNetwork::buildConfigDescription);
  }

  @Override
  public C connect(InetSocketAddress serverAddress) {
    return connectAsync(serverAddress).join();
  }

  @Override
  public CompletableFuture<C> connectAsync(InetSocketAddress serverAddress) {

    C currentConnection = currentConnection();
    if (currentConnection != null) {
      Utils.unchecked(currentConnection, C::close);
    }

    // if we are trying connection now
    if (!connecting.compareAndSet(false, true)) {
      CompletableFuture<C> pendingConnection = pendingConnection();
      if (pendingConnection != null) {
        return pendingConnection;
      }
      ThreadUtils.sleep(100);
      return connectAsync(serverAddress);
    }

    var asyncResult = new CompletableFuture<C>();

    @SuppressWarnings("resource")
    var channel = Utils.uncheckedGet(AsynchronousSocketChannel::open);
    channel.connect(serverAddress, this, new CompletionHandler<>() {
      @Override
      public void completed(@Nullable Void result, DefaultClientNetwork<C> network) {
        SocketAddress remoteAddress = NetworkUtils.getRemoteAddress(channel);
        log.info(remoteAddress, "Connected to server:[%s]"::formatted);
        asyncResult.complete(channelToConnection.apply(network, channel));
      }

      @Override
      public void failed(Throwable exc, DefaultClientNetwork<C> attachment) {
        asyncResult.completeExceptionally(exc);
      }
    });

    pendingConnection = asyncResult;

    return asyncResult.handle((connection, throwable) -> {
      this.currentConnection = connection;
      this.connecting.set(false);
      return AsyncUtils.continueCompletableStage(connection, throwable);
    });
  }

  @Override
  public Mono<C> connectReactive(InetSocketAddress serverAddress) {
    return Mono
        .create(monoSink -> connectAsync(serverAddress)
            .whenComplete((connection, ex) -> {
              if (ex != null) {
                monoSink.error(ex);
              } else {
                monoSink.success(connection);
              }
            }));
  }

  @Override
  public Optional<C> currentConnectionOptional() {
    return Optional.ofNullable(currentConnection());
  }

  @Override
  public void shutdown() {
    C connection = currentConnection();
    if (connection != null) {
      Utils.unchecked(connection, C::close);
    }
  }

  private static String buildConfigDescription(NetworkConfig conf) {
    return "Client network configuration: {\n" + "  groupName: \"" + conf.threadGroupName() + "\",\n"
        + "  readBufferSize: " + conf.readBufferSize() + ",\n" + "  pendingBufferSize: " + conf.pendingBufferSize()
        + ",\n" + "  writeBufferSize: " + conf.writeBufferSize() + "\n" + "}";
  }
}
