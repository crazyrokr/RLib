package rlib.function;

import org.jetbrains.annotations.Nullable;

/**
 * The function-consumer.
 *
 * @author JavaSaBr
 */
@FunctionalInterface
public interface IntObjectConsumer<T> {

    void accept(int first, @Nullable T second);
}
