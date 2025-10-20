package javasabr.rlib.network.server.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javasabr.rlib.collections.array.ArrayFactory;
import javasabr.rlib.collections.array.MutableArray;
import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.common.util.GroupThreadFactory;
import javasabr.rlib.common.util.Utils;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.ServerNetworkConfig;
import javasabr.rlib.network.UnsafeConnection;
import javasabr.rlib.network.impl.AbstractNetwork;
import javasabr.rlib.network.server.ServerNetwork;
import javasabr.rlib.network.util.NetworkUtils;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * The base implementation of {@link ServerNetwork}.
 *
 * @author JavaSaBr
 */
@CustomLog
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class DefaultServerNetwork<C extends UnsafeConnection<C>>
    extends AbstractNetwork<C> implements ServerNetwork<C> {

  private interface ServerCompletionHandler<C extends UnsafeConnection<C>> extends
      CompletionHandler<AsynchronousSocketChannel, DefaultServerNetwork<C>> {}

  private final ServerCompletionHandler<C> acceptHandler = new ServerCompletionHandler<>() {

    @Override
    public void completed(AsynchronousSocketChannel channel, DefaultServerNetwork<C> network) {
      var connection = network.channelToConnection.apply(network, channel);
      log.debug(connection.remoteAddress(), "Accepted new connection:[%s]"::formatted);
      network.onAccept(connection);
      network.acceptNext();
    }

    @Override
    public void failed(Throwable exc, DefaultServerNetwork<C> network) {
      if (exc instanceof AsynchronousCloseException) {
        log.warning("Server network was closed");
      } else {
        log.error("Got exception during accepting new connection:");
        log.error(exc);
        if (channel.isOpen()) {
          network.acceptNext();
        }
      }
    }
  };

  @Getter
  ScheduledExecutorService scheduledExecutor;

  AsynchronousChannelGroup group;
  AsynchronousServerSocketChannel channel;
  MutableArray<Consumer<? super C>> subscribers;

  public DefaultServerNetwork(
      ServerNetworkConfig config,
      BiFunction<Network<C>, AsynchronousSocketChannel, C> channelToConnection) {
    super(config, channelToConnection);
    this.group = Utils.uncheckedGet(buildExecutor(config), AsynchronousChannelGroup::withThreadPool);
    this.scheduledExecutor = buildScheduledExecutor(config);
    this.channel = Utils.uncheckedGet(group, AsynchronousServerSocketChannel::open);
    this.subscribers = ArrayFactory.copyOnModifyArray(Consumer.class);
    log.info(config, DefaultServerNetwork::buildConfigDescription);
  }

  @Override
  public InetSocketAddress start() {

    InetSocketAddress address = null;
    while (address == null) {
      address = new InetSocketAddress(NetworkUtils.getAvailablePort(1500));
      try {
        channel.bind(address);
      } catch (IOException e) {
        address = null;
      }
    }

    log.info(address, "Started server socket on address:[%s]"::formatted);

    if (!subscribers.isEmpty()) {
      acceptNext();
    }

    return address;
  }

  @Override
  public <S extends ServerNetwork<C>> S start(InetSocketAddress serverAddress) {
    Utils.unchecked(channel, serverAddress, AsynchronousServerSocketChannel::bind);
    log.info(serverAddress, addr -> "Started server socket on address: " + addr);
    if (!subscribers.isEmpty()) {
      acceptNext();
    }
    return ClassUtils.unsafeNNCast(this);
  }

  protected void acceptNext() {
    if (channel.isOpen()) {
      try {
        channel.accept(this, acceptHandler);
      } catch (AcceptPendingException ignored) {}
    } else {
      log.error("Cannot accept next connection because server channel is already closed");
    }
  }

  protected void onAccept(C connection) {
    connection.onConnected();
    subscribers
        .iterations()
        .forEach(connection, Consumer::accept);
  }

  @Override
  public void onAccept(Consumer<? super C> consumer) {
    subscribers.add(consumer);
    acceptNext();
  }

  @Override
  public Flux<C> accepted() {
    return Flux.create(this::registerFluxOnAccepted);
  }

  protected void registerFluxOnAccepted(FluxSink<C> sink) {
    Consumer<? super C> listener = sink::next;
    onAccept(listener);
    sink.onDispose(() -> subscribers.remove(listener));
  }

  @Override
  public void shutdown() {
    Utils.unchecked(channel, AsynchronousChannel::close);
    group.shutdown();
  }

  protected ExecutorService buildExecutor(ServerNetworkConfig config) {

    var threadFactory = new GroupThreadFactory(
        config.threadGroupName(),
        config.threadConstructor(),
        config.threadPriority(),
        false);

    ExecutorService executorService;
    if (config.threadGroupMinSize() < config.threadGroupMaxSize()) {
      executorService = new ThreadPoolExecutor(
          config.threadGroupMinSize(),
          config.threadGroupMaxSize(),
          120,
          TimeUnit.SECONDS,
          new LinkedBlockingQueue<>(),
          threadFactory,
          new ThreadPoolExecutor.AbortPolicy());
    } else {
      executorService = Executors.newFixedThreadPool(config.threadGroupMinSize(), threadFactory);
    }

    // activate the executor
    executorService.submit(() -> {});
    return executorService;
  }

  protected ScheduledExecutorService buildScheduledExecutor(ServerNetworkConfig config) {

    var threadFactory = new GroupThreadFactory(
        config.scheduledThreadGroupName(),
        config.threadConstructor(),
        config.threadPriority(),
        false);

    ScheduledExecutorService executorService;
    if (config.threadGroupMinSize() < config.threadGroupMaxSize()) {
      executorService = new ScheduledThreadPoolExecutor(
          config.scheduledThreadGroupSize(),
          threadFactory,
          new ThreadPoolExecutor.CallerRunsPolicy());
    } else {
      executorService = Executors.newScheduledThreadPool(config.threadGroupMinSize(), threadFactory);
    }

    // activate the executor
    executorService.submit(() -> {});
    return executorService;
  }

  private static String buildConfigDescription(ServerNetworkConfig conf) {
    return "Server network configuration: {\n" + "  minThreads: " + conf.threadGroupMinSize() + ",\n" + "  maxThreads: "
        + conf.threadGroupMaxSize() + ",\n" + "  priority: " + conf.threadPriority() + ",\n" + "  threadGroupName: \""
        + conf.threadGroupName() + "\",\n" + "  readBufferSize: " + conf.readBufferSize() + ",\n"
        + "  pendingBufferSize: " + conf.pendingBufferSize() + ",\n" + "  writeBufferSize: " + conf.writeBufferSize()
        + "\n" + "}";
  }
}
