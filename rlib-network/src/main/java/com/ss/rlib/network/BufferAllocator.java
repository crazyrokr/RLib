package com.ss.rlib.network;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * The interface to implement a buffer allocator for network things.
 *
 * @author JavaSaBr
 */
public interface BufferAllocator {

    /**
     * Get a new read buffer to use.
     *
     * @return the new buffer.
     */
    @NotNull ByteBuffer takeReadBuffer();

    /**
     * Get a new pending buffer to use.
     *
     * @return the new pending buffer.
     */
    @NotNull ByteBuffer takePendingBuffer();

    /**
     * Get a new write buffer to use.
     *
     * @return the new buffer.
     */
    @NotNull ByteBuffer takeWriteBuffer();

    /**
     * Get a new buffer with requested capacity.
     *
     * @param bufferSize the size of new buffer.
     * @return the new buffer.
     */
    @NotNull ByteBuffer takeBuffer(int bufferSize);

    /**
     * Store an old read buffer if need.
     *
     * @param buffer the old read buffer.
     */
    @NotNull BufferAllocator putReadBuffer(@NotNull ByteBuffer buffer);

    /**
     * Store an old pending buffer if need.
     *
     * @param buffer the old pending buffer.
     */
    @NotNull BufferAllocator putPendingBuffer(@NotNull ByteBuffer buffer);

    /**
     * Store an old write buffer if need.
     *
     * @param buffer the old write buffer.
     */
    @NotNull BufferAllocator putWriteBuffer(@NotNull ByteBuffer buffer);

    /**
     * Store an old byte buffer if need.
     *
     * @param buffer the old byte buffer.
     */

    @NotNull BufferAllocator putBuffer(@NotNull ByteBuffer buffer);
}
