package com.ss.rlib.logging;

import org.jetbrains.annotations.NotNull;

/**
 * The listener for listening logger events.
 *
 * @author JavaSaBr
 */
public interface LoggerListener {

    /**
     * Println.
     *
     * @param text the new text.
     */
    void println(@NotNull String text);

    /**
     * Flushes last data.
     */
    default void flush() {
    }
}
