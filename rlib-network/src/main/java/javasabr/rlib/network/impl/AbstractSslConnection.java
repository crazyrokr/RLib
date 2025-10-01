package javasabr.rlib.network.impl;

import java.nio.channels.AsynchronousSocketChannel;
import javasabr.rlib.network.BufferAllocator;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.Network;
import javasabr.rlib.network.packet.ReadableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractSslConnection<R extends ReadableNetworkPacket, W extends WritableNetworkPacket>
    extends AbstractConnection<R, W> {

  final SSLEngine sslEngine;

  public AbstractSslConnection(
      Network<? extends Connection<R, W>> network,
      AsynchronousSocketChannel channel,
      BufferAllocator bufferAllocator,
      SSLContext sslContext,
      int maxPacketsByRead,
      boolean clientMode) {
    super(network, channel, bufferAllocator, maxPacketsByRead);
    this.sslEngine = sslContext.createSSLEngine();
    this.sslEngine.setUseClientMode(clientMode);
    try {
      this.sslEngine.beginHandshake();
    } catch (SSLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void sendImpl(WritableNetworkPacket packet) {
    super.sendImpl(packet);
    packetReader().startRead();
  }
}
