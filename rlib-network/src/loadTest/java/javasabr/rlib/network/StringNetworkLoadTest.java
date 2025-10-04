package javasabr.rlib.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import javasabr.rlib.common.util.StringUtils;
import javasabr.rlib.common.util.ThreadUtils;
import javasabr.rlib.logger.api.LoggerLevel;
import javasabr.rlib.logger.api.LoggerManager;
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

@CustomLog
public class StringNetworkLoadTest {

  public static final int MAX_ITERATIONS = 10;
  public static final int MAX_SEND_DELAY = 60_000;

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
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    ClientNetwork<StringDataConnection> network = NetworkFactory
        .stringDataClientNetwork(networkConfig, clientAllocator);

    void connectAndSendMessages(
        InetSocketAddress serverAddress,
        int messages) {
      Thread thread = new Thread(() -> {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        ThreadUtils.sleep(random.nextInt(5000));
        StringDataConnection connection = network.connect(serverAddress);

        var tasks = new ArrayList<ScheduledFuture<?>>(messages);

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
          for (int m = 0; m < messages; m++) {
            int delay = random.nextInt(MAX_SEND_DELAY);
            ScheduledFuture<?> schedule = executor.schedule(
                () -> {
                  StringWritableNetworkPacket message = newMessage(10, 10240);
                  connection.send(message);
                }, delay, TimeUnit.MILLISECONDS);
            tasks.add(schedule);
          }

          for (ScheduledFuture<?> task : tasks) {
            try {
              task.get();
            } catch (InterruptedException | ExecutionException e) {
              throw new RuntimeException(e);
            }
          }

          tasks.clear();
        }
      });
      thread.setName(clientId);
      thread.setUncaughtExceptionHandler((t, e) -> log.error(e));
      thread.start();
    }

    @Override
    public void close() {
      network.shutdown();
    }
  }

  @Test
  @SneakyThrows
  void testServerWithMultiplyClients() {
    LoggerManager.enable(StringNetworkLoadTest.class, LoggerLevel.INFO);

    var serverConfig = SimpleServerNetworkConfig
        .builder()
        .threadGroupSize(10)
        .writeBufferSize(1024)
        .readBufferSize(1024)
        .pendingBufferSize(2048)
        .build();

    var serverAllocator = new DefaultBufferAllocator(serverConfig);
    ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    int clientCount = 100;
    int messagesPerIteration = 1_000;
    int expectedMessages = clientCount * messagesPerIteration * MAX_ITERATIONS;

    var finalWaiter = new CountDownLatch(1);
    var receivedClientPackersPerSecond = new LongAccumulator(Long::sum, 0);

    ServerNetwork<StringDataConnection> serverNetwork = NetworkFactory.stringDataServerNetwork(serverConfig, serverAllocator);
    InetSocketAddress serverAddress = serverNetwork.start();

    serverNetwork
        .accepted()
        .flatMap(Connection::receivedEvents)
        .doOnNext(receivedPacketEvent -> {
          //log.info(receivedPacketEvent, "Server received:[%s]"::formatted);
          receivedClientPackersPerSecond.accumulate(1);
          //counter.countDown();
        })
        .subscribe();

    initReceivedMessagesTracker(
        scheduledExecutor,
        receivedClientPackersPerSecond,
        finalWaiter);

    var clients = new ArrayList<TestClient>();

    for(int c = 0; c < clientCount; c++) {
      TestClient testClient = new TestClient();
      testClient.connectAndSendMessages(serverAddress, messagesPerIteration);
    }

    Assertions
        .assertTrue(finalWaiter.await(120_000, TimeUnit.MILLISECONDS),
            "Still not received [%s] -> [%s] messages".formatted(expectedMessages, finalWaiter.getCount()));

    for (TestClient client : clients) {
      client.close();
    }

    serverNetwork.shutdown();
  }

  private static void initReceivedMessagesTracker(
      ScheduledExecutorService scheduledExecutor,
      LongAccumulator receivedClientPackersPerSecond,
      CountDownLatch finalWaiter) {
    scheduledExecutor.scheduleAtFixedRate(() -> {
      long received = receivedClientPackersPerSecond.getThenReset();
      log.info(received, "Server receiving [%s] messages/sec"::formatted);
      if (received == 0) {
        finalWaiter.countDown();
      }
    }, 1, 1, TimeUnit.SECONDS);
  }

  private static StringWritableNetworkPacket newMessage(int minMessageLength, int maxMessageLength) {
    return new StringWritableNetworkPacket(StringUtils.generate(minMessageLength, maxMessageLength));
  }
}
