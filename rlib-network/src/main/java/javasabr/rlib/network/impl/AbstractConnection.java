package javasabr.rlib.network.impl;

import static javasabr.rlib.common.util.Utils.unchecked;

import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BiConsumer;
import javasabr.rlib.collections.array.ArrayFactory;
import javasabr.rlib.collections.array.MutableArray;
import javasabr.rlib.collections.deque.DequeFactory;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.UnsafeConnection;
import javasabr.rlib.network.packet.NetworkPacketReader;
import javasabr.rlib.network.packet.NetworkPacketWriter;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javasabr.rlib.network.packet.impl.WritablePacketWrapper;
import javasabr.rlib.network.util.NetworkUtils;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * The base implementation of {@link Connection}.
 *
 * @author JavaSaBr
 */
@CustomLog
@Accessors(fluent = true, chain = false)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractConnection<R extends ReadableNetworkPacket, W extends WritableNetworkPacket> implements
    UnsafeConnection<R, W> {

  private static class WritablePacketWithFeedback<W extends WritableNetworkPacket> extends
      WritablePacketWrapper<CompletableFuture<Boolean>, W> {

    public WritablePacketWithFeedback(CompletableFuture<Boolean> attachment, W packet) {
      super(attachment, packet);
    }
  }

  @Getter
  final String remoteAddress;

  final Network<? extends Connection<R, W>> network;
  final BufferAllocator bufferAllocator;
  final AsynchronousSocketChannel channel;
  final Deque<WritableNetworkPacket> pendingPackets;
  final StampedLock lock;

  final AtomicBoolean isWriting;
  final AtomicBoolean closed;

  final MutableArray<BiConsumer<? super Connection<R, W>, ? super R>> subscribers;

  final int maxPacketsByRead;

  @Getter
  volatile long lastActivity;

  public AbstractConnection(
      Network<? extends Connection<R, W>> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      int maxPacketsByRead) {
    this.bufferAllocator = bufferAllocator;
    this.maxPacketsByRead = maxPacketsByRead;
    this.lock = new StampedLock();
    this.channel = channel;
    this.pendingPackets = DequeFactory.arrayBasedBased(WritableNetworkPacket.class);
    this.network = network;
    this.isWriting = new AtomicBoolean(false);
    this.closed = new AtomicBoolean(false);
    this.subscribers = ArrayFactory.copyOnModifyArray(BiConsumer.class);
    this.remoteAddress = String.valueOf(NetworkUtils.getRemoteAddress(channel));
  }

  @Override
  public void onConnected() {}

  protected abstract NetworkPacketReader packetReader();

  protected abstract NetworkPacketWriter packetWriter();

  @Override
  public void onReceive(BiConsumer<? super Connection<R, W>, ? super R> consumer) {
    subscribers.add(consumer);
    packetReader().startRead();
  }

  @Override
  public Flux<ReceivedPacketEvent<? extends Connection<R, W>, ? extends R>> receivedEvents() {
    return Flux.create(this::registerFluxOnReceivedEvents);
  }

  @Override
  public Flux<? extends R> receivedPackets() {
    return Flux.create(this::registerFluxOnReceivedPackets);
  }

  protected void registerFluxOnReceivedEvents(
      FluxSink<ReceivedPacketEvent<? extends Connection<R, W>, ? extends R>> sink) {

    BiConsumer<Connection<R, W>, R> listener =
      (connection, packet) -> sink.next(new ReceivedPacketEvent<>(connection,
        packet));

    onReceive(listener);

    sink.onDispose(() -> subscribers.remove(listener));
  }

  protected void registerFluxOnReceivedPackets(FluxSink<? super R> sink) {
    BiConsumer<Connection<R, W>, R> listener = (connection, packet) -> sink.next(packet);
    onReceive(listener);
    sink.onDispose(() -> subscribers.remove(listener));
  }

  @Nullable
  protected WritableNetworkPacket nextPacketToWrite() {
    long stamp = lock.writeLock();
    try {
      return pendingPackets.poll();
    } finally {
      lock.unlockWrite(stamp);
    }
  }

  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      doClose();
    }
  }

  /**
   * Does the process of closing this connection.
   */
  protected void doClose() {
    if (channel.isOpen()) {
      unchecked(channel, AsynchronousChannel::close);
    }
    clearWaitPackets();
    packetReader().close();
    packetWriter().close();
  }

  /**
   * Update the time of last activity.
   */
  protected void updateLastActivity() {
    this.lastActivity = System.currentTimeMillis();
  }

  @Override
  public boolean closed() {
    return closed.get();
  }

  protected void serializedPacket(WritableNetworkPacket packet) {}

  protected void handleReceivedPacket(R packet) {
    log.debug(packet, remoteAddress, "Handle received packet:[%s] from:[%s]"::formatted);
    subscribers
        .iterations()
        .forEach(this, packet, BiConsumer::accept);
  }

  protected void handleSentPacket(WritableNetworkPacket packet, Boolean result) {
    if (packet instanceof WritablePacketWithFeedback) {
      ((WritablePacketWithFeedback<W>) packet)
          .getAttachment()
          .complete(result);
    }
  }

  @Override
  public final void send(W packet) {
    sendImpl(packet);
  }

  protected void sendImpl(WritableNetworkPacket packet) {

    if (closed()) {
      return;
    }

    long stamp = lock.writeLock();
    try {
      pendingPackets.add(packet);
    } finally {
      lock.unlockWrite(stamp);
    }

    packetWriter().tryToSendNextPacket();
  }

  protected void queueAtFirst(WritableNetworkPacket packet) {
    long stamp = lock.writeLock();
    try {
      pendingPackets.addFirst(packet);
    } finally {
      lock.unlockWrite(stamp);
    }
  }

  @Override
  public CompletableFuture<Boolean> sendWithFeedback(W packet) {

    var asyncResult = new CompletableFuture<Boolean>();

    sendImpl(new WritablePacketWithFeedback<>(asyncResult, packet));

    if (closed()) {
      return CompletableFuture.completedFuture(Boolean.FALSE);
    }

    return asyncResult;
  }

  /**
   * Clear waited packets.
   */
  protected void clearWaitPackets() {
    long stamp = lock.writeLock();
    try {
      doClearWaitPackets();
    } finally {
      lock.unlockWrite(stamp);
    }
  }

  protected void doClearWaitPackets() {

    for (var pendingPacket : pendingPackets) {
      handleSentPacket(pendingPacket, Boolean.FALSE);
    }

    pendingPackets.clear();
  }
}
