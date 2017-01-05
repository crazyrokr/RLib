package rlib.util.array.impl;

import org.jetbrains.annotations.NotNull;

/**
 * The final version of the {@link FastArray}.
 *
 * @author JavaSaBr
 */
public final class FinalFastArray<E> extends FastArray<E> {

    public FinalFastArray(@NotNull final Class<E> type) {
        super(type);
    }

    public FinalFastArray(@NotNull final Class<E> type, final int size) {
        super(type, size);
    }
}
