package javasabr.rlib.network;

import java.util.concurrent.CompletableFuture;
import javasabr.rlib.network.client.ClientNetwork;
import javasabr.rlib.network.impl.DefaultBufferAllocator;
import javasabr.rlib.network.impl.DefaultConnection;
import javasabr.rlib.network.impl.StringDataConnection;
import javasabr.rlib.network.impl.StringDataSSLConnection;
import javasabr.rlib.network.packet.impl.DefaultReadableNetworkPacket;
import javasabr.rlib.network.packet.registry.ReadableNetworkPacketRegistry;
import javasabr.rlib.network.server.ServerNetwork;
import javax.net.ssl.SSLContext;
import lombok.AllArgsConstructor;

/**
 * @author JavaSaBr
 */
public class BaseNetworkTest {

  @AllArgsConstructor
  public static class TestNetwork<C extends Connection<?, ?>> implements AutoCloseable {

    public final ServerNetworkConfig serverNetworkConfig;
    public final NetworkConfig clientNetworkConfig;

    public final C clientToServer;
    public final C serverToClient;

    private final ServerNetwork<C> serverNetwork;
    private final ClientNetwork<C> clientNetwork;

    @Override
    public void close() {
      shutdown();
    }

    public void shutdown() {
      serverNetwork.shutdown();
      clientNetwork.shutdown();
    }
  }

  protected TestNetwork<StringDataConnection> buildStringNetwork() {
    return buildStringNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        new DefaultBufferAllocator(ServerNetworkConfig.DEFAULT_SERVER),
        NetworkConfig.DEFAULT_CLIENT,
        new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT));
  }

  protected TestNetwork<StringDataSSLConnection> buildStringSSLNetwork(
      SSLContext serverSSLContext,
      SSLContext clientSSLContext) {
    return buildStringSSLNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        new DefaultBufferAllocator(ServerNetworkConfig.DEFAULT_SERVER),
        serverSSLContext,
        NetworkConfig.DEFAULT_CLIENT,
        new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT),
        clientSSLContext);
  }

  protected TestNetwork<StringDataConnection> buildStringNetwork(
      BufferAllocator serverBufferAllocator,
      BufferAllocator clientBufferAllocator) {
    return buildStringNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        serverBufferAllocator,
        NetworkConfig.DEFAULT_CLIENT,
        clientBufferAllocator);
  }

  protected TestNetwork<StringDataConnection> buildStringNetwork(
      ServerNetworkConfig serverNetworkConfig,
      BufferAllocator serverBufferAllocator) {
    return buildStringNetwork(
        serverNetworkConfig,
        serverBufferAllocator,
        NetworkConfig.DEFAULT_CLIENT,
        new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT));
  }

  protected TestNetwork<StringDataConnection> buildStringNetwork(
      ServerNetworkConfig serverNetworkConfig,
      BufferAllocator serverBufferAllocator,
      NetworkConfig clientNetworkConfig,
      BufferAllocator clientBufferAllocator) {

    var asyncClientToServer = new CompletableFuture<StringDataConnection>();
    var asyncServerToClient = new CompletableFuture<StringDataConnection>();

    var serverNetwork = NetworkFactory.stringDataServerNetwork(serverNetworkConfig, serverBufferAllocator);
    var serverAddress = serverNetwork.start();

    serverNetwork.onAccept(asyncServerToClient::complete);

    var clientNetwork = NetworkFactory.stringDataClientNetwork(clientNetworkConfig, clientBufferAllocator);
    clientNetwork
        .connectAsync(serverAddress)
        .thenApply(asyncClientToServer::complete);

    return new TestNetwork<>(
        serverNetworkConfig,
        clientNetworkConfig,
        asyncClientToServer.join(),
        asyncServerToClient.join(),
        serverNetwork,
        clientNetwork);
  }

  protected TestNetwork<StringDataSSLConnection> buildStringSSLNetwork(
      ServerNetworkConfig serverNetworkConfig,
      BufferAllocator serverBufferAllocator,
      SSLContext serverSSLContext,
      NetworkConfig clientNetworkConfig,
      BufferAllocator clientBufferAllocator,
      SSLContext clientSSLContext) {

    var asyncClientToServer = new CompletableFuture<StringDataSSLConnection>();
    var asyncServerToClient = new CompletableFuture<StringDataSSLConnection>();

    var serverNetwork = NetworkFactory.stringDataSslServerNetwork(
        serverNetworkConfig,
        serverBufferAllocator,
        serverSSLContext);

    var serverAddress = serverNetwork.start();
    serverNetwork.onAccept(asyncServerToClient::complete);

    var clientNetwork = NetworkFactory.stringDataSslClientNetwork(
        clientNetworkConfig,
        clientBufferAllocator,
        clientSSLContext);

    clientNetwork
        .connectAsync(serverAddress)
        .thenApply(asyncClientToServer::complete);

    return new TestNetwork<>(
        serverNetworkConfig,
        clientNetworkConfig,
        asyncClientToServer.join(),
        asyncServerToClient.join(),
        serverNetwork,
        clientNetwork);
  }

  protected TestNetwork<DefaultConnection> buildDefaultNetwork(
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket> serverPacketRegistry,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket> clientPacketRegistry) {
    return buildDefaultNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        new DefaultBufferAllocator(ServerNetworkConfig.DEFAULT_SERVER),
        serverPacketRegistry,
        NetworkConfig.DEFAULT_CLIENT,
        new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT),
        clientPacketRegistry);
  }

  protected TestNetwork<DefaultConnection> buildDefaultNetwork(
      BufferAllocator serverBufferAllocator,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket> serverPacketRegistry,
      BufferAllocator clientBufferAllocator,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket> clientPacketRegistry) {
    return buildDefaultNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        serverBufferAllocator,
        serverPacketRegistry,
        NetworkConfig.DEFAULT_CLIENT,
        clientBufferAllocator,
        clientPacketRegistry);
  }

  protected TestNetwork<DefaultConnection> buildDefaultNetwork(
      ServerNetworkConfig serverNetworkConfig,
      BufferAllocator serverBufferAllocator,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket> serverPacketRegistry,
      NetworkConfig clientNetworkConfig,
      BufferAllocator clientBufferAllocator,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket> clientPacketRegistry) {

    var asyncClientToServer = new CompletableFuture<DefaultConnection>();
    var asyncServerToClient = new CompletableFuture<DefaultConnection>();

    var serverNetwork = NetworkFactory.defaultServerNetwork(
        serverNetworkConfig,
        serverBufferAllocator,
        serverPacketRegistry);
    var serverAddress = serverNetwork.start();

    serverNetwork.onAccept(asyncServerToClient::complete);

    var clientNetwork = NetworkFactory.defaultClientNetwork(
        clientNetworkConfig,
        clientBufferAllocator,
        clientPacketRegistry);
    clientNetwork
        .connectAsync(serverAddress)
        .thenApply(asyncClientToServer::complete);

    return new TestNetwork<>(
        serverNetworkConfig,
        clientNetworkConfig,
        asyncClientToServer.join(),
        asyncServerToClient.join(),
        serverNetwork,
        clientNetwork);
  }
}
