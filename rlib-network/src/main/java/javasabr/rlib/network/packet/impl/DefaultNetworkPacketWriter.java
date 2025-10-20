package javasabr.rlib.network.packet.impl;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javasabr.rlib.functions.ObjBoolConsumer;
import javasabr.rlib.network.UnsafeConnection;
import javasabr.rlib.network.packet.WritableNetworkPacket;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultNetworkPacketWriter<
    W extends WritableNetworkPacket<C>,
    C extends UnsafeConnection<C>> extends AbstractNetworkPacketWriter<W, C> {

  final int packetLengthHeaderSize;

  public DefaultNetworkPacketWriter(
      C connection,
      Runnable updateActivityFunction,
      Supplier<@Nullable WritableNetworkPacket<C>> packetProvider,
      Consumer<WritableNetworkPacket<C>> serializedToChannelPacketHandler,
      ObjBoolConsumer<WritableNetworkPacket<C>> sentPacketHandler,
      int packetLengthHeaderSize) {
    super(
        connection,
        updateActivityFunction,
        packetProvider,
        serializedToChannelPacketHandler,
        sentPacketHandler);
    this.packetLengthHeaderSize = packetLengthHeaderSize;
  }

  @Override
  protected int totalSize(WritableNetworkPacket<C> packet, int expectedLength) {
    return expectedLength + packetLengthHeaderSize;
  }

  @Override
  protected boolean onBeforeSerialize(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer writeBuffer) {
    writeBuffer
        .clear()
        .position(packetLengthHeaderSize);
    return true;
  }

  @Override
  protected ByteBuffer onSerializeResult(
      W packet,
      int expectedLength,
      int totalSize,
      ByteBuffer writeBuffer) {
    return writePacketLength(writeBuffer, writeBuffer.limit())
        .position(0);
  }

  protected ByteBuffer writePacketLength(ByteBuffer buffer, int packetLength) {
    return writeHeader(buffer, 0, packetLength, packetLengthHeaderSize);
  }
}
