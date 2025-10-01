package javasabr.rlib.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import javasabr.rlib.common.util.ObjectUtils;
import javasabr.rlib.common.util.StringUtils;
import javasabr.rlib.common.util.Utils;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerLevel;
import javasabr.rlib.logger.api.LoggerManager;
import javasabr.rlib.network.client.ClientNetwork;
import javasabr.rlib.network.impl.DefaultBufferAllocator;
import javasabr.rlib.network.impl.StringDataSslConnection;
import javasabr.rlib.network.packet.impl.AbstractSslNetworkPacketReader;
import javasabr.rlib.network.packet.impl.AbstractSslNetworkPacketWriter;
import javasabr.rlib.network.packet.impl.StringReadablePacket;
import javasabr.rlib.network.packet.impl.StringWritableNetworkPacket;
import javasabr.rlib.network.server.ServerNetwork;
import javasabr.rlib.network.util.NetworkUtils;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The tests of string based network.
 *
 * @author JavaSaBr
 */
public class StringSSLNetworkTest extends BaseNetworkTest {

  private static final Logger LOGGER = LoggerManager.getLogger(StringSSLNetworkTest.class);

  @Test
  @SneakyThrows
  void certificatesTest() {

    InputStream keystoreFile = StringSSLNetworkTest.class.getResourceAsStream("/ssl/rlib_test_cert.p12");
    SSLContext sslContext = NetworkUtils.createSslContext(keystoreFile, "test");
    SSLContext clientSSLContext = NetworkUtils.createAllTrustedClientSslContext();

    int serverPort = NetworkUtils.getAvailablePort(10000);

    SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
    ServerSocket serverSocket = serverSocketFactory.createServerSocket(serverPort);

    SSLSocketFactory clientSocketFactory = clientSSLContext.getSocketFactory();
    SSLSocket clientSocket = (SSLSocket) clientSocketFactory.createSocket("localhost", serverPort);

    var clientSocketOnServer = serverSocket.accept();

    new Thread(() -> Utils.unchecked(() -> {
      var clientOutStream = new PrintWriter(clientSocket.getOutputStream());
      clientOutStream.println("Hello SSL");
      clientOutStream.flush();
    })).start();

    Scanner serverIn = new Scanner(clientSocketOnServer.getInputStream());
    String receivedOnServer = serverIn.next() + " " + serverIn.next();

    Assertions.assertEquals("Hello SSL", receivedOnServer);
  }

  @Test
  @SneakyThrows
  void serverSSLNetworkTest() {

    InputStream keystoreFile = StringSSLNetworkTest.class.getResourceAsStream("/ssl/rlib_test_cert.p12");
    SSLContext sslContext = NetworkUtils.createSslContext(keystoreFile, "test");

    ServerNetwork<StringDataSslConnection> serverNetwork = NetworkFactory.stringDataSslServerNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        new DefaultBufferAllocator(ServerNetworkConfig.DEFAULT_CLIENT),
        sslContext);

    InetSocketAddress serverAddress = serverNetwork.start();

    serverNetwork
        .accepted()
        .flatMap(Connection::receivedEvents)
        .subscribe(event -> {
          var message = event.packet().data();
          LOGGER.info("Received from client: " + message);
          event.connection().send(new StringWritableNetworkPacket("Echo: " + message));
        });

    SSLContext clientSslContext = NetworkUtils.createAllTrustedClientSslContext();
    SSLSocketFactory sslSocketFactory = clientSslContext.getSocketFactory();
    SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverAddress.getHostName(), serverAddress.getPort());

    var buffer = ByteBuffer.allocate(1024);
    buffer.position(2);

    new StringWritableNetworkPacket("Hello SSL").write(buffer);

    buffer.putShort(0, (short) buffer.position());
    buffer.flip();

    var out = sslSocket.getOutputStream();
    out.write(buffer.array(), 0, buffer.limit());
    out.flush();

    buffer.clear();

    InputStream in = sslSocket.getInputStream();
    int readBytes = in.read(buffer.array());

    buffer
        .position(readBytes)
        .flip();
    short packetLength = buffer.getShort();

    StringReadablePacket response = new StringReadablePacket();
    response.read(buffer, packetLength - 2);

    LOGGER.info("Response: " + response.data());

    serverNetwork.shutdown();
  }

  @Test
  @SneakyThrows
  void clientSSLNetworkTest() {

    System.setProperty("javax.net.debug", "all");
    //LoggerManager.enable(AbstractSSLPacketWriter.class, LoggerLevel.DEBUG);
    //LoggerManager.enable(AbstractSSLPacketReader.class, LoggerLevel.DEBUG);

    InputStream keystoreFile = StringSSLNetworkTest.class.getResourceAsStream("/ssl/rlib_test_cert.p12");
    SSLContext sslContext = NetworkUtils.createSslContext(keystoreFile, "test");

    int serverPort = NetworkUtils.getAvailablePort(1000);

    SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
    ServerSocket serverSocket = serverSocketFactory.createServerSocket(serverPort);
    CountDownLatch counter = new CountDownLatch(1);

    SSLContext clientSslContext = NetworkUtils.createAllTrustedClientSslContext();
    ClientNetwork<StringDataSslConnection> clientNetwork = NetworkFactory.stringDataSslClientNetwork(
        NetworkConfig.DEFAULT_CLIENT,
        new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT),
        clientSslContext);

    clientNetwork
        .connectReactive(new InetSocketAddress("localhost", serverPort))
        .doOnNext(connection -> connection.send(new StringWritableNetworkPacket("Hello SSL")))
        .doOnError(Throwable::printStackTrace)
        .flatMapMany(Connection::receivedEvents)
        .subscribe(event -> {
          LOGGER.info("Received from server: " + event.packet().data());
          counter.countDown();
        });

    Socket acceptedClientSocket = serverSocket.accept();

    var buffer = ByteBuffer.allocate(512);

    InputStream clientIn = acceptedClientSocket.getInputStream();
    int readBytes = clientIn.read(buffer.array());

    buffer
        .position(readBytes)
        .flip();

    var dataLength = buffer.getShort();

    var receivedPacket = new StringReadablePacket();
    receivedPacket.read(buffer, dataLength);

    Assertions.assertEquals("Hello SSL", receivedPacket.data());

    LOGGER.info("Received from client: " + receivedPacket.data());

    buffer.clear();
    buffer.position(2);

    new StringWritableNetworkPacket("Echo: Hello SSL").write(buffer);

    buffer.putShort(0, (short) buffer.position());
    buffer.flip();

    OutputStream out = acceptedClientSocket.getOutputStream();
    out.write(buffer.array(), 0, buffer.limit());
    out.flush();

    buffer.clear();

    Assertions.assertTrue(
        counter.await(100_000, TimeUnit.MILLISECONDS),
        "Still wait for " + counter.getCount() + " packets...");

    clientNetwork.shutdown();
    serverSocket.close();
  }

  @Test
  @SneakyThrows
  void echoNetworkTest() {

    //System.setProperty("javax.net.debug", "all");
    //LoggerManager.enable(AbstractPacketWriter.class, LoggerLevel.DEBUG);
    //LoggerManager.enable(AbstractSSLPacketWriter.class, LoggerLevel.DEBUG);
    //LoggerManager.enable(AbstractSSLPacketReader.class, LoggerLevel.DEBUG);

    InputStream keystoreFile = StringSSLNetworkTest.class.getResourceAsStream("/ssl/rlib_test_cert.p12");
    SSLContext serverSSLContext = NetworkUtils.createSslContext(keystoreFile, "test");

    ServerNetwork<StringDataSslConnection> serverNetwork = NetworkFactory.stringDataSslServerNetwork(
        ServerNetworkConfig.DEFAULT_SERVER,
        new DefaultBufferAllocator(ServerNetworkConfig.DEFAULT_CLIENT),
        serverSSLContext);

    InetSocketAddress serverAddress = serverNetwork.start();
    int expectedReceivedPackets = 90;

    var counter = new CountDownLatch(expectedReceivedPackets);

    serverNetwork
        .accepted()
        .flatMap(Connection::receivedEvents)
        .subscribe(event -> {
          var message = event.packet().data();
          LOGGER.info("Received from client: " + message);
          event.connection().send(new StringWritableNetworkPacket("Echo: " + message));
        });

    SSLContext clientSslContext = NetworkUtils.createAllTrustedClientSslContext();
    ClientNetwork<StringDataSslConnection> clientNetwork = NetworkFactory.stringDataSslClientNetwork(
        NetworkConfig.DEFAULT_CLIENT,
        new DefaultBufferAllocator(NetworkConfig.DEFAULT_CLIENT),
        clientSslContext);

    clientNetwork
        .connectReactive(serverAddress)
        .doOnNext(connection -> IntStream
            .range(10, expectedReceivedPackets + 10)
            .forEach(length -> connection.send(newMessage(9, length))))
        .doOnError(Throwable::printStackTrace)
        .flatMapMany(Connection::receivedEvents)
        .subscribe(event -> {
          LOGGER.info("Received from server: " + event.packet().data());
          counter.countDown();
        });

    Assertions.assertTrue(
        counter.await(1000, TimeUnit.MILLISECONDS),
        "Still wait for " + counter.getCount() + " packets...");

    serverNetwork.shutdown();
    clientNetwork.shutdown();

    //LoggerManager.disable(AbstractSSLPacketWriter.class, LoggerLevel.DEBUG);
    //LoggerManager.disable(AbstractSSLPacketReader.class, LoggerLevel.DEBUG);
    //LoggerManager.disable(AbstractPacketWriter.class, LoggerLevel.DEBUG);
  }

  @Test
  void shouldReceiveManyPacketsFromSmallToBigSize() {

    //System.setProperty("javax.net.debug", "all");
    //LoggerManager.enable(AbstractPacketReader.class, LoggerLevel.DEBUG);
    //LoggerManager.enable(AbstractPacketWriter.class, LoggerLevel.DEBUG);
    LoggerManager.enable(AbstractSslNetworkPacketWriter.class, LoggerLevel.DEBUG);
    LoggerManager.enable(AbstractSslNetworkPacketReader.class, LoggerLevel.DEBUG);

    InputStream keystoreFile = StringSSLNetworkTest.class.getResourceAsStream("/ssl/rlib_test_cert.p12");
    SSLContext serverSSLContext = NetworkUtils.createSslContext(keystoreFile, "test");
    SSLContext clientSSLContext = NetworkUtils.createAllTrustedClientSslContext();

    int packetCount = 10;

    try (TestNetwork<StringDataSslConnection> testNetwork = buildStringSSLNetwork(serverSSLContext, clientSSLContext)) {
      var bufferSize = testNetwork.serverNetworkConfig.readBufferSize();
      var random = ThreadLocalRandom.current();

      StringDataSslConnection clientToServer = testNetwork.clientToServer;
      StringDataSslConnection serverToClient = testNetwork.serverToClient;

      var pendingPacketsOnServer = serverToClient
          .receivedPackets()
          .doOnNext(packet -> LOGGER.info("Received from client: " + packet.data()))
          .buffer(packetCount);

      var messages = IntStream
          .range(0, packetCount)
          .mapToObj(value -> {
            var length = value % 3 == 0 ? bufferSize : random.nextInt(0, bufferSize / 2 - 1);
            return StringUtils.generate(length);
          })
          .peek(message -> clientToServer.send(new StringWritableNetworkPacket(message)))
          .toList();

      List<? extends StringReadablePacket> receivedPackets =
          ObjectUtils.notNull(pendingPacketsOnServer.blockFirst(Duration.ofSeconds(5000)));

      Assertions.assertEquals(packetCount, receivedPackets.size(), "Didn't receive all packets");

      var wrongPacket = receivedPackets
          .stream()
          .filter(packet -> messages
              .stream()
              .noneMatch(message -> message.equals(packet.data())))
          .findFirst()
          .orElse(null);

      Assertions.assertNull(wrongPacket, () -> "Wrong received packet: " + wrongPacket);
    }
  }

  private static StringWritableNetworkPacket newMessage(int minMessageLength, int maxMessageLength) {
    return new StringWritableNetworkPacket(StringUtils.generate(minMessageLength, maxMessageLength));
  }
}
