package com.ss.rlib.network.packet.impl;

import static com.ss.rlib.network.util.NetworkUtils.hexDump;
import com.ss.rlib.logger.api.Logger;
import com.ss.rlib.logger.api.LoggerManager;
import com.ss.rlib.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * The base implementation of {@link Packet}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractPacket implements Packet {

    protected static final Logger LOGGER = LoggerManager.getLogger(Packet.class);

    /**
     * Handle the exception.
     *
     * @param buffer    the data buffer.
     * @param exception the exception.
     */
    protected void handleException(@NotNull ByteBuffer buffer, @NotNull Exception exception) {
        LOGGER.warning(this, exception);

        if (buffer.isDirect()) {
            var array = new byte[buffer.limit()];
            buffer.get(array, 0, buffer.limit());
            LOGGER.warning("buffer: " + buffer + "\n" + hexDump(array, array.length));
        } else {
            LOGGER.warning("buffer: " + buffer + "\n" + hexDump(buffer.array(), buffer.limit()));
        }
    }

    @Override
    public @NotNull String getName() {
        return getClass().getName();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "name='" + getName() + '\'' + '}';
    }
}
