package com.ss.rlib.io.impl;

import com.ss.rlib.io.ReusableStream;
import com.ss.rlib.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * The implementation of reusable input stream.
 *
 * @author JavaSaBr
 */
public final class ReuseBytesInputStream extends InputStream implements ReusableStream {

    /**
     * The data buffer.
     */
    @NotNull
    protected byte buffer[];

    /**
     * The position.
     */
    protected int pos;

    /**
     * The count bytes.
     */
    protected int count;

    /**
     * Instantiates a new Reuse bytes input stream.
     */
    public ReuseBytesInputStream() {
        this.buffer = ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    /**
     * Instantiates a new Reuse bytes input stream.
     *
     * @param buffer the buffer
     */
    public ReuseBytesInputStream(@NotNull final byte buffer[]) {
        this.buffer = buffer;
        this.pos = 0;
        this.count = buffer.length;
    }

    /**
     * Instantiates a new Reuse bytes input stream.
     *
     * @param buffer the buffer
     * @param offset the offset
     * @param length the length
     */
    public ReuseBytesInputStream(@NotNull final byte buffer[], final int offset, final int length) {
        this.buffer = buffer;
        this.pos = offset;
        this.count = Math.min(offset + length, buffer.length);
    }

    @Override
    public void initFor(@NotNull final byte[] buffer, final int offset, final int length) {
        this.buffer = buffer;
        this.pos = offset;
        this.count = length;
    }

    @Override
    public synchronized int read() {
        return (pos < count) ? (buffer[pos++] & 0xff) : -1;
    }

    @Override
    public synchronized int read(@NotNull final byte buffer[], int offset, int length) {

        if (offset < 0 || length < 0 || length > buffer.length - offset) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= count) {
            return -1;
        }

        int available = count - pos;

        if (length > available) {
            length = available;
        }

        if (length <= 0) {
            return 0;
        }

        System.arraycopy(this.buffer, pos, buffer, offset, length);
        pos += length;

        return length;
    }

    @Override
    public synchronized int available() {
        return count - pos;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public synchronized void reset() throws IOException {
        this.pos = 0;
    }
}
