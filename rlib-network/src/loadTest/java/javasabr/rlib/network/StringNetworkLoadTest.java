package javasabr.rlib.network;

import static javasabr.rlib.network.ServerNetworkConfig.DEFAULT_SERVER;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javasabr.rlib.common.util.StringUtils;
import javasabr.rlib.network.ServerNetworkConfig.SimpleServerNetworkConfig;
import javasabr.rlib.network.client.ClientNetwork;
import javasabr.rlib.network.impl.DefaultBufferAllocator;
import javasabr.rlib.network.impl.StringDataConnection;
import javasabr.rlib.network.packet.impl.StringWritableNetworkPacket;
import javasabr.rlib.network.server.ServerNetwork;
import lombok.CustomLog;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@CustomLog
public class StringNetworkLoadTest {

  private static class TestClient implements AutoCloseable {

    private static final AtomicInteger ID_FACTORY = new AtomicInteger();

    String clientId = "Client_%s".formatted(ID_FACTORY.incrementAndGet());
    NetworkConfig networkConfig = NetworkConfig.SimpleNetworkConfig.builder()
        .groupName(clientId)
        .writeBufferSize(256)
        .readBufferSize(256)
        .pendingBufferSize(512)
        .build();

    BufferAllocator clientAllocator = new DefaultBufferAllocator(networkConfig);
    ClientNetwork<StringDataConnection> network = NetworkFactory
        .stringDataClientNetwork(networkConfig, clientAllocator);

    void connectAndSendMessages(InetSocketAddress serverAddress, int messages, CountDownLatch ) {

    }

    @Override
    public void close() throws Exception {
      network.shutdown();
    }
  }

  @Test
  @SneakyThrows
  void testServerWithMultiplyClients() {
    //LoggerManager.enable(AbstractPacketWriter.class, LoggerLevel.DEBUG);

    var serverConfig = SimpleServerNetworkConfig
        .builder()
        .threadGroupSize(10)
        .build();

    var serverAllocator = new DefaultBufferAllocator(serverConfig);
    var clientAllocator = new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT);

    int clientCount = 100;
    int packetsPerClient = 100;
    int minMessageLength = 10;
    int maxMessageLength = (int) (DEFAULT_SERVER.readBufferSize() * 1.5);

    var counter = new CountDownLatch(clientCount * packetsPerClient);
    var sentPacketsToServer = new AtomicInteger();
    var receivedPacketsOnServer = new AtomicInteger();
    var receivedPacketsOnClients = new AtomicInteger();

    ServerNetwork<StringDataConnection> serverNetwork = NetworkFactory.stringDataServerNetwork(serverConfig, serverAllocator);
    InetSocketAddress serverAddress = serverNetwork.start();

    serverNetwork
        .accepted()
        .flatMap(Connection::receivedEvents)
        .doOnNext(event -> receivedPacketsOnServer.incrementAndGet())
        .subscribe(event -> event.connection().send(newMessage(minMessageLength, maxMessageLength)));

    Flux
        .fromStream(IntStream
            .range(0, clientCount)
            .mapToObj(value -> NetworkFactory.stringDataClientNetwork(NetworkConfig.DEFAULT_CLIENT, clientAllocator)))
        .doOnDiscard(ClientNetwork.class, Network::shutdown)
        .flatMap(client -> client.connectReactive(serverAddress))
        .flatMap(connection -> {

          var receivedEvents = connection.receivedEvents();

          for (int i = 0; i < packetsPerClient; i++) {
            connection.send(newMessage(minMessageLength, maxMessageLength));
            sentPacketsToServer.incrementAndGet();
          }

          return receivedEvents;
        })
        .subscribe(event -> {
          receivedPacketsOnClients.incrementAndGet();
          counter.countDown();
        });

    Assertions
        .assertTrue(
        counter.await(10000, TimeUnit.MILLISECONDS),
        "Still wait for " + counter.getCount() + " packets... " + "Sent packets to server: " + sentPacketsToServer
            + ", " + "Received packets on server: " + receivedPacketsOnServer + ", " + "Received packets on clients: "
            + receivedPacketsOnClients);

    serverNetwork.shutdown();
  }

  private static StringWritableNetworkPacket newMessage(int minMessageLength, int maxMessageLength) {
    return new StringWritableNetworkPacket(StringUtils.generate(minMessageLength, maxMessageLength));
  }
}
