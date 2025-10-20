package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.functions.ObjBoolConsumer;
import javasabr.rlib.network.UnsafeConnection;
import javasabr.rlib.network.packet.IdBasedWritableNetworkPacket;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class IdBasedNetworkPacketWriter<
    W extends IdBasedWritableNetworkPacket<C>,
    C extends UnsafeConnection<C>> extends DefaultNetworkPacketWriter<W, C> {

  final int packetIdHeaderSize;

  public IdBasedNetworkPacketWriter(
      C connection,
      Runnable updateActivityFunction,
      Supplier<@Nullable WritableNetworkPacket<C>> packetProvider,
      Consumer<WritableNetworkPacket<C>> serializedToChannelPacketHandler,
      ObjBoolConsumer<WritableNetworkPacket<C>> sentPacketHandler,
      int packetLengthHeaderSize,
      int packetIdHeaderSize) {
    super(
        connection,
        updateActivityFunction,
        packetProvider,
        serializedToChannelPacketHandler,
        sentPacketHandler,
        packetLengthHeaderSize);
    this.packetIdHeaderSize = packetIdHeaderSize;
  }

  @Override
  protected boolean doSerialize(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer writeBuffer) {
    writeHeader(writeBuffer, packet.packetId(), packetIdHeaderSize);
    return super.doSerialize(packet, expectedLength, totalSize, writeBuffer);
  }
}
