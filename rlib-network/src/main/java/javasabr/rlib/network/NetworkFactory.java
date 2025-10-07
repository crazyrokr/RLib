package javasabr.rlib.network;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.BiFunction;
import javasabr.rlib.network.client.ClientNetwork;
import javasabr.rlib.network.client.impl.DefaultClientNetwork;
import javasabr.rlib.network.impl.DefaultBufferAllocator;
import javasabr.rlib.network.impl.DefaultConnection;
import javasabr.rlib.network.impl.StringDataConnection;
import javasabr.rlib.network.impl.StringDataSslConnection;
import javasabr.rlib.network.packet.impl.DefaultReadableNetworkPacket;
import javasabr.rlib.network.packet.registry.ReadableNetworkPacketRegistry;
import javasabr.rlib.network.server.ServerNetwork;
import javasabr.rlib.network.server.impl.DefaultServerNetwork;
import javax.net.ssl.SSLContext;
import lombok.experimental.UtilityClass;

/**
 * Class with factory methods to build client/server networks.
 *
 * @author JavaSaBr
 */
@UtilityClass
public final class NetworkFactory {

  public static <C extends UnsafeConnection<?, ?, C>> ClientNetwork<C> clientNetwork(
      NetworkConfig networkConfig,
      BiFunction<Network<C>, AsynchronousSocketChannel, C> channelToConnection) {
    return new DefaultClientNetwork<>(networkConfig, channelToConnection);
  }

  public static <C extends UnsafeConnection<?, ?, C>> ServerNetwork<C> serverNetwork(
      ServerNetworkConfig networkConfig,
      BiFunction<Network<C>, AsynchronousSocketChannel, C> channelToConnection) {
    return new DefaultServerNetwork<>(networkConfig, channelToConnection);
  }

  /**
   * Create a string packet based asynchronous client network.
   */
  public static ClientNetwork<StringDataConnection> stringDataClientNetwork() {
    return stringDataClientNetwork(NetworkConfig.DEFAULT_CLIENT);
  }

  /**
   * Create a string packet based asynchronous client network.
   */
  public static ClientNetwork<StringDataConnection> stringDataClientNetwork(
      NetworkConfig networkConfig) {
    return stringDataClientNetwork(networkConfig, new DefaultBufferAllocator(networkConfig));
  }

  /**
   * Create a string packet based asynchronous client network.
   */
  public static ClientNetwork<StringDataConnection> stringDataClientNetwork(
      NetworkConfig networkConfig,
      BufferAllocator bufferAllocator) {
    return clientNetwork(
        networkConfig,
        (network, channel) -> new StringDataConnection(network, channel, bufferAllocator));
  }

  /**
   * Create id based packet default asynchronous client network.
   */
  public static ClientNetwork<DefaultConnection> defaultClientNetwork(
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket, DefaultConnection> packetRegistry) {
    return defaultClientNetwork(
        NetworkConfig.DEFAULT_CLIENT,
        new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT),
        packetRegistry);
  }

  /**
   * Create id based packet default asynchronous client network.
   */
  public static ClientNetwork<DefaultConnection> defaultClientNetwork(
      NetworkConfig networkConfig,
      BufferAllocator bufferAllocator,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket, DefaultConnection> packetRegistry) {
    return clientNetwork(
        networkConfig,
        (network, channel) -> new DefaultConnection(network, channel, bufferAllocator, packetRegistry));
  }

  /**
   * Create string packet based asynchronous secure client network.
   *
   */
  public static ClientNetwork<StringDataSslConnection> stringDataSslClientNetwork(
      NetworkConfig networkConfig,
      BufferAllocator bufferAllocator,
      SSLContext sslContext) {
    return clientNetwork(
        networkConfig,
        (network, channel) -> new StringDataSslConnection(network, channel, bufferAllocator, sslContext, true));
  }

  /**
   * Create string packet based asynchronous server network.
   */
  public static ServerNetwork<StringDataConnection> stringDataServerNetwork() {
    return stringDataServerNetwork(ServerNetworkConfig.DEFAULT_SERVER);
  }

  /**
   * Create string packet based asynchronous server network.
   */
  public static ServerNetwork<StringDataConnection> stringDataServerNetwork(
      ServerNetworkConfig networkConfig) {
    return stringDataServerNetwork(networkConfig, new DefaultBufferAllocator(networkConfig));
  }

  /**
   * Create string packet based asynchronous server network.
   */
  public static ServerNetwork<StringDataConnection> stringDataServerNetwork(
      ServerNetworkConfig networkConfig,
      BufferAllocator bufferAllocator) {
    return serverNetwork(
        networkConfig,
        (network, channel) -> new StringDataConnection(network, channel, bufferAllocator));
  }

  /**
   * Create string packet based asynchronous secure server network.
   */
  public static ServerNetwork<StringDataSslConnection> stringDataSslServerNetwork(
      ServerNetworkConfig networkConfig,
      BufferAllocator bufferAllocator,
      SSLContext sslContext) {
    return serverNetwork(
        networkConfig,
        (network, channel) -> new StringDataSslConnection(network, channel, bufferAllocator, sslContext, false));
  }

  /**
   * Create id based packet default asynchronous server network.
   */
  public static ServerNetwork<DefaultConnection> defaultServerNetwork(
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket, DefaultConnection> packetRegistry) {
    return defaultServerNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        new DefaultBufferAllocator(ServerNetworkConfig.DEFAULT_SERVER),
        packetRegistry);
  }

  /**
   * Create id based packet default asynchronous server network.
   */
  public static ServerNetwork<DefaultConnection> defaultServerNetwork(
      ServerNetworkConfig networkConfig,
      BufferAllocator bufferAllocator,
      ReadableNetworkPacketRegistry<DefaultReadableNetworkPacket, DefaultConnection> packetRegistry) {
    return serverNetwork(
        networkConfig,
        (network, channel) -> new DefaultConnection(network, channel, bufferAllocator, packetRegistry));
  }
}
