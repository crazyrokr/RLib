package com.ss.rlib.network.packet.impl;

import com.ss.rlib.logger.api.Logger;
import com.ss.rlib.logger.api.LoggerManager;
import com.ss.rlib.network.BufferAllocator;
import com.ss.rlib.network.Connection;
import com.ss.rlib.network.packet.PacketWriter;
import com.ss.rlib.network.packet.WritablePacket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author JavaSaBr
 */
@RequiredArgsConstructor
public abstract class AbstractPacketWriter<W extends WritablePacket, C extends Connection<?, W>> implements
    PacketWriter {

    private static final Logger LOGGER = LoggerManager.getLogger(AbstractPacketWriter.class);

    private final CompletionHandler<Integer, W> writeHandler = new CompletionHandler<>() {

        @Override
        public void completed(@NotNull Integer result, @NotNull W packet) {
            handleSuccessfulWriting(result, packet);
        }

        @Override
        public void failed(@NotNull Throwable exc, @NotNull W packet) {
            handleFailedWriting(exc, packet);
        }
    };

    protected final AtomicBoolean isWriting = new AtomicBoolean();

    protected final C connection;
    protected final AsynchronousSocketChannel channel;
    protected final BufferAllocator bufferAllocator;
    protected final ByteBuffer writeBuffer;
    protected final ByteBuffer encryptedBuffer;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected volatile ByteBuffer writeTempBuffer;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected volatile ByteBuffer encryptedTempBuffer;

    protected final Runnable updateActivityFunction;
    protected final Supplier<@Nullable W> nextWritePacketSupplier;

    public AbstractPacketWriter(
        @NotNull C connection,
        @NotNull AsynchronousSocketChannel channel,
        @NotNull BufferAllocator bufferAllocator,
        @NotNull Runnable updateActivityFunction,
        @NotNull Supplier<@Nullable W> nextWritePacketSupplier
    ) {
        this.connection = connection;
        this.channel = channel;
        this.bufferAllocator = bufferAllocator;
        this.writeBuffer = bufferAllocator.takeWriteBuffer();
        this.encryptedBuffer = bufferAllocator.takeWriteBuffer();
        this.updateActivityFunction = updateActivityFunction;
        this.nextWritePacketSupplier = nextWritePacketSupplier;
    }

    @Override
    public void writeNextPacket() {

        if (connection.isClosed() || !isWriting.compareAndSet(false, true)) {
            return;
        }

        var waitPacket = nextWritePacketSupplier.get();

        if (waitPacket == null) {
            isWriting.set(false);
            return;
        }

        var byteBuffer = serialize(waitPacket);

        if (byteBuffer.limit() != 0) {
            channel.write(byteBuffer, waitPacket, writeHandler);
        } else {
            isWriting.set(false);
        }

        completed(waitPacket);
    }

    protected @NotNull ByteBuffer serialize(@NotNull W packet) {

        var expectedLength = packet.getExpectedLength();
        var totalSize = getTotalSize(packet, expectedLength);

        // if the packet is too big to use a write buffer
        if (expectedLength != -1 && totalSize > writeBuffer.capacity()) {
            writeTempBuffer = bufferAllocator.takeBuffer(totalSize);
            encryptedTempBuffer = bufferAllocator.takeBuffer(totalSize);
            return serialize(packet, expectedLength, totalSize, writeTempBuffer, encryptedTempBuffer);
        } else {
            return serialize(packet, expectedLength, totalSize, writeBuffer, encryptedBuffer);
        }
    }

    /**
     * Get a total size of packet if it possible.
     *
     * @param packet         the packet.
     * @param expectedLength the expected size.
     * @return the total size or -1.
     */
    protected abstract int getTotalSize(@NotNull W packet, int expectedLength);

    /**
     * Serialize packet to byte buffer.
     *
     * @param packet          the packet to serialize.
     * @param expectedLength  the packet's expected size.
     * @param totalSize       the packet's total size.
     * @param buffer          the byte buffer.
     * @param encryptedBuffer the byte buffer to store encrypted data.
     * @return the buffer to write to channel.
     */
    protected @NotNull ByteBuffer serialize(
        @NotNull W packet,
        int expectedLength,
        int totalSize,
        @NotNull ByteBuffer buffer,
        @NotNull ByteBuffer encryptedBuffer
    ) {

        buffer.clear();

        if(!onBeforeWrite(packet, expectedLength, totalSize, buffer)) {
            return buffer.clear().limit(0);
        } else if (!onWrite(packet, expectedLength, totalSize, buffer)) {
            return buffer.clear().limit(0);
        }

        buffer.flip();

        if(!onAfterWrite(packet, expectedLength, totalSize, buffer)) {
            return buffer.clear().limit(0);
        }

        // FIXME need to rewrite in more flexible style
        /*var crypt = connection.getCrypt();
        var encrypted = crypt.encrypt(buffer, buffer.limit() - packetLengthHeaderSize, encryptedBuffer.clear());

        // nothing to encrypt
        if (encrypted == null) {
            return onResult(packet, buffer);
        }

        buffer.clear();
        buffer.position(packetLengthHeaderSize);
        buffer.put(encrypted);
        buffer.flip();*/

        return onResult(packet, expectedLength, totalSize, buffer);
    }

    /**
     * Handle a byte buffer before writing packet's data.
     *
     * @param packet         the packet.
     * @param expectedLength the packet's expected size.
     * @param totalSize      the packet's total size.
     * @param buffer         the buffer.
     * @return true if handling was successful.
     */
    protected boolean onBeforeWrite(@NotNull W packet, int expectedLength, int totalSize, @NotNull ByteBuffer buffer) {
        return true;
    }

    /**
     * Write a packet to byte buffer.
     *
     * @param packet         the packet.
     * @param expectedLength the packet's expected size.
     * @param totalSize      the packet's total size.
     * @param buffer         the buffer.
     * @return true if writing was successful.
     */
    protected boolean onWrite(@NotNull W packet, int expectedLength, int totalSize, @NotNull ByteBuffer buffer) {
        return packet.write(buffer);
    }

    /**
     * Handle a byte buffer after writing packet's data.
     *
     * @param packet         the packet.
     * @param expectedLength the packet's expected size.
     * @param totalSize      the packet's total size.
     * @param buffer         the buffer.
     * @return true if handling was successful.
     */
    protected boolean onAfterWrite(@NotNull W packet, int expectedLength, int totalSize, @NotNull ByteBuffer buffer) {
        return true;
    }

    /**
     * Handle a final result byte buffer.
     *
     * @param packet         the packet.
     * @param expectedLength the packet's expected size.
     * @param totalSize      the packet's total size.
     * @param buffer         the buffer.
     * @return the same byte buffer.
     */
    protected @NotNull ByteBuffer onResult(
        @NotNull W packet,
        int expectedLength,
        int totalSize,
        @NotNull ByteBuffer buffer
    ) {
        return buffer.position(0);
    }

    protected @NotNull ByteBuffer writeHeader(@NotNull ByteBuffer buffer, int position, int value, int headerSize) {

        switch (headerSize) {
            case 1:
                buffer.put(position, (byte) value);
                break;
            case 2:
                buffer.putShort(position, (short) value);
                break;
            case 4:
                buffer.putInt(position, value);
                break;
            default:
                throw new IllegalStateException("Wrong packet's header size: " + headerSize);
        }

        return buffer;
    }

    protected @NotNull ByteBuffer writeHeader(@NotNull ByteBuffer buffer, int value, int headerSize) {

        switch (headerSize) {
            case 1:
                buffer.put((byte) value);
                break;
            case 2:
                buffer.putShort((short) value);
                break;
            case 4:
                buffer.putInt(value);
                break;
            default:
                throw new IllegalStateException("Wrong packet's header size: " + headerSize);
        }

        return buffer;
    }

    /**
     * Handle a completed packet.
     *
     * @param packet the writable packet.
     */
    protected void completed(@NotNull W packet) {
    }

    /**
     * Handle successful wrote data.
     *
     * @param result the count of wrote bytes.
     * @param packet the sent packet.
     */
    protected void handleSuccessfulWriting(@NotNull Integer result, @NotNull W packet) {
        updateActivityFunction.run();

        if (result == -1) {
            connection.close();
            return;
        }

        var writeTempBuffer = this.writeTempBuffer;

        // if we have data in temp write buffer we need to use it
        if (writeTempBuffer != null) {

            if (writeTempBuffer.remaining() > 0) {
                channel.write(writeTempBuffer, packet, writeHandler);
                return;
            }
            // if all data from temp buffers was written then we can remove it
            else {
                clearTempBuffers();
            }

        } else {
            if (writeBuffer.remaining() > 0) {
                channel.write(writeBuffer, packet, writeHandler);
                return;
            }
        }

        if (isWriting.compareAndSet(true, false)) {
            writeNextPacket();
        }
    }

    /**
     * Handle the exception during writing the packet.
     *
     * @param exception the exception.
     * @param packet    the packet.
     */
    protected void handleFailedWriting(@NotNull Throwable exception, @NotNull W packet) {
        LOGGER.error(new RuntimeException("Failed writing packet: " + packet, exception));

        if (!connection.isClosed()) {
            if (isWriting.compareAndSet(true, false)) {
                writeNextPacket();
            }
        }
    }

    @Override
    public void close() {

        bufferAllocator
            .putWriteBuffer(writeBuffer)
            .putWriteBuffer(encryptedBuffer);

        clearTempBuffers();
    }

    protected void clearTempBuffers() {

        var encryptedTempBuffer = getEncryptedTempBuffer();
        var writeTempBuffer = getWriteTempBuffer();

        if (encryptedTempBuffer != null) {
            setEncryptedTempBuffer(null);
            bufferAllocator.putBuffer(encryptedTempBuffer);
        }

        if (writeTempBuffer != null) {
            setWriteTempBuffer(null);
            bufferAllocator.putBuffer(writeTempBuffer);
        }
    }
}
