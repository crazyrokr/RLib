package javasabr.rlib.network.client.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import javasabr.rlib.common.util.AsyncUtils;
import javasabr.rlib.common.util.GroupThreadFactory;
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
public class DefaultClientNetwork<C extends Connection<C>> extends AbstractNetwork<C>
    implements ClientNetwork<C> {

  final AtomicBoolean connecting;

  @Getter
  final ScheduledExecutorService scheduledExecutor;
  final ExecutorService networkExecutor;
  final AsynchronousChannelGroup channelGroup;

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
    this.scheduledExecutor = buildScheduledExecutor(config);
    this.networkExecutor = buildExecutor(config);
    this.channelGroup = Utils.uncheckedGet(networkExecutor, AsynchronousChannelGroup::withThreadPool);
    log.info(config, DefaultClientNetwork::buildConfigDescription);
  }

  @Override
  public void inNetworkThread(Runnable task) {
    networkExecutor.execute(task);
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
    var channel = Utils.uncheckedGet(channelGroup, AsynchronousSocketChannel::open);
    channel.connect(serverAddress, this, new CompletionHandler<>() {
      @Override
      public void completed(@Nullable Void result, DefaultClientNetwork<C> network) {
        SocketAddress remoteAddress = NetworkUtils.getRemoteAddress(channel);
        log.info(remoteAddress, "[%s] Connected to server."::formatted);
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
    channelGroup.shutdown();
    scheduledExecutor.shutdown();
    networkExecutor.shutdown();
  }

  protected ExecutorService buildExecutor(NetworkConfig config) {
    var threadFactory = new GroupThreadFactory(
        config.threadGroupName(),
        config.threadConstructor(),
        config.threadPriority(),
        false);
    ExecutorService executorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
    // activate the executor
    executorService.submit(() -> {});
    return executorService;
  }

  protected ScheduledExecutorService buildScheduledExecutor(NetworkConfig config) {
    var threadFactory = new GroupThreadFactory(
        config.scheduledThreadGroupName(),
        config.threadConstructor(),
        config.threadPriority(),
        false);
    ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
    // activate the executor
    scheduledExecutor.submit(() -> {});
    return scheduledExecutor;
  }

  private static String buildConfigDescription(NetworkConfig conf) {
    return """
        Server network configuration: {
          "threadGroupName": "%s",
          "scheduledThreadGroupName": "%s",
          "readBufferSize": %d,
          "pendingBufferSize": %d,
          "writeBufferSize": %d,
          "useDirectByteBuffer": %s,
          "maxEmptyReadsBeforeClose": %d,
          "maxPacketSize": %d,
          "retryDelayInMs": %d
        }""".formatted(
        conf.threadGroupName(),
        conf.scheduledThreadGroupName(),
        conf.readBufferSize(),
        conf.pendingBufferSize(),
        conf.writeBufferSize(),
        conf.useDirectByteBuffer(),
        conf.maxEmptyReadsBeforeClose(),
        conf.maxPacketSize(),
        conf.retryDelayInMs());
  }
}
