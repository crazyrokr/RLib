package com.ss.rlib.logger.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement a logger.
 *
 * @author JavaSaBr
 */
public interface Logger {

    @FunctionalInterface
    interface Factory {

        @NotNull String make();
    }

    @FunctionalInterface
    interface SinFactory<F> {

        @NotNull String make(F first);
    }

    @FunctionalInterface
    interface IntSinFactory {

        @NotNull String make(int val);
    }

    @FunctionalInterface
    interface BiFactory<F, S> {

        @NotNull String make(F first, S second);
    }

    @FunctionalInterface
    interface ObjIntFactory<F> {

        @NotNull String make(F first, int second);
    }

    @FunctionalInterface
    interface IntBiFactory {

        @NotNull String make(int first, int second);
    }

    @FunctionalInterface
    interface TriFactory<F, S, T> {

        @NotNull String make(F first, S second, T third);
    }

    /**
     * Print the debug message.
     *
     * @param owner   the owner of the message.
     * @param message the message.
     */
    @Deprecated(forRemoval = true)
    default void debug(@NotNull Object owner, @NotNull String message) {
        print(LoggerLevel.DEBUG, owner, message);
    }

    /**
     * Print the debug message.
     *
     * @param owner          the owner of the message.
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     * @param <T>            the argument's type.
     */
    @Deprecated(forRemoval = true)
    default <T> void debug(@NotNull Object owner, @Nullable T arg, @NotNull Logger.SinFactory<T> messageFactory) {
        print(LoggerLevel.DEBUG, owner, arg, messageFactory);
    }

    /**
     * Print the debug message.
     *
     * @param owner          the owner of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     */
    @Deprecated(forRemoval = true)
    default <F, S> void debug(
        @NotNull Object owner,
        @Nullable F first,
        @Nullable S second,
        @NotNull Logger.BiFactory<F, S> messageFactory
    ) {
        print(LoggerLevel.DEBUG, owner, first, second, messageFactory);
    }


    /**
     * Print the debug message.
     *
     * @param owner          the owner of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param third          the third arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     * @param <T>            the third argument's type.
     */
    @Deprecated(forRemoval = true)
    default <F, S, T> void debug(
        @NotNull Object owner,
        @Nullable F first,
        @Nullable S second,
        @Nullable T third,
        @NotNull Logger.TriFactory<F, S, T> messageFactory
    ) {
        print(LoggerLevel.DEBUG, owner, first, second, third, messageFactory);
    }

    /**
     * Print the debug message.
     *
     * @param message the message.
     */
    default void debug(@NotNull String message) {
        print(LoggerLevel.DEBUG, message);
    }

    /**
     * Print a build debug message.
     *
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     */
    default void debug(int arg, @NotNull Logger.IntSinFactory messageFactory) {
        print(LoggerLevel.DEBUG, arg, messageFactory);
    }

    /**
     * Print a build debug message.
     *
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     * @param <T>            the argument's type.
     */
    default <T> void debug(@NotNull T arg, @NotNull Logger.SinFactory<@NotNull T> messageFactory) {
        print(LoggerLevel.DEBUG, arg, messageFactory);
    }

    /**
     * Print a build debug message.
     *
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     */
    default <F, S> void debug(
        @NotNull F first,
        @NotNull S second,
        @NotNull Logger.BiFactory<@NotNull F, @NotNull S> messageFactory
    ) {
        print(LoggerLevel.DEBUG, first, second, messageFactory);
    }

    /**
     * Print a build debug message.
     *
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     */
    default <F> void debug(
        @NotNull F first,
        int second,
        @NotNull Logger.ObjIntFactory<@NotNull F> messageFactory
    ) {
        print(LoggerLevel.DEBUG, first, second, messageFactory);
    }

    /**
     * Print a build debug message.
     *
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     */
    default void debug(int first, int second, @NotNull Logger.IntBiFactory messageFactory) {
        print(LoggerLevel.DEBUG, first, second, messageFactory);
    }

    /**
     * Print a build debug message.
     *
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param third          the third arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     * @param <T>            the third argument's type.
     */
    default <F, S, T> void debug(
        @NotNull F first,
        @NotNull S second,
        @NotNull T third,
        @NotNull Logger.TriFactory<@NotNull F, @NotNull S, @NotNull T> messageFactory
    ) {
        print(LoggerLevel.DEBUG, first, second, third, messageFactory);
    }

    /**
     * Print the error message.
     *
     * @param owner   the owner of the message.
     * @param message the message.
     */
    @Deprecated(forRemoval = true)
    default void error(@NotNull Object owner, @NotNull String message) {
        print(LoggerLevel.ERROR, owner, message);
    }

    /**
     * Print the error message.
     *
     * @param owner     the owner of the message.
     * @param exception the exception.
     */
    @Deprecated(forRemoval = true)
    default void error(@NotNull Object owner, @NotNull Throwable exception) {
        print(LoggerLevel.ERROR, owner, exception);
    }

    /**
     * Print a build error message.
     *
     * @param message the message.
     */
    default void error(@NotNull String message) {
        print(LoggerLevel.ERROR, message);
    }

    /**
     * Print a build error message.
     *
     * @param exception the exception.
     */
    default void error(@NotNull Throwable exception) {
        print(LoggerLevel.ERROR, exception);
    }

    /**
     * Print the information message.
     *
     * @param owner   the owner of the message.
     * @param message the message.
     */
    @Deprecated(forRemoval = true)
    default void info(@NotNull Object owner, @NotNull String message) {
        print(LoggerLevel.INFO, owner, message);
    }

    /**
     * Print the information message.
     *
     * @param owner          the owner of the message.
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     * @param <T>            the argument's type.
     */
    @Deprecated(forRemoval = true)
    default <T> void info(
        @NotNull Object owner,
        @Nullable T arg,
        @NotNull Logger.SinFactory<T> messageFactory
    ) {
        print(LoggerLevel.INFO, owner, arg, messageFactory);
    }

    /**
     * Print the information message.
     *
     * @param owner          the owner of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     */
    @Deprecated(forRemoval = true)
    default <F, S> void info(
        @NotNull Object owner,
        @Nullable F first,
        @Nullable S second,
        @NotNull Logger.BiFactory<F, S> messageFactory
    ) {
        print(LoggerLevel.INFO, owner, first, second, messageFactory);
    }

    /**
     * Print the information message.
     *
     * @param owner          the owner of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param third          the third arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     * @param <T>            the third argument's type.
     */
    @Deprecated(forRemoval = true)
    default <F, S, T> void info(
        @NotNull Object owner,
        @Nullable F first,
        @Nullable S second,
        @Nullable T third,
        @NotNull Logger.TriFactory<F, S, T> messageFactory
    ) {
        print(LoggerLevel.INFO, owner, first, second, third, messageFactory);
    }

    /**
     * Print a build information message.
     *
     * @param message the message.
     */
    default void info(@NotNull String message) {
        print(LoggerLevel.INFO, message);
    }

    /**
     * Print a build information message.
     *
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     * @param <T>            the argument's type.
     */
    default <T> void info(@NotNull T arg, @NotNull Logger.SinFactory<@NotNull T> messageFactory) {
        print(LoggerLevel.INFO, arg, messageFactory);
    }

    /**
     * Print a build information message.
     *
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     */
    default <F, S> void info(
        @NotNull F first,
        @NotNull S second,
        @NotNull Logger.BiFactory<@NotNull F, @NotNull S> messageFactory
    ) {
        print(LoggerLevel.INFO, first, second, messageFactory);
    }


    /**
     * Print a build information message.
     *
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param third          the third arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     * @param <T>            the third argument's type.
     */
    default <F, S, T> void info(
        @NotNull F first,
        @NotNull S second,
        @NotNull T third,
        @NotNull Logger.TriFactory<@NotNull F, @NotNull S, @NotNull T> messageFactory
    ) {
        print(LoggerLevel.INFO, first, second, third, messageFactory);
    }

    /**
     * Check of enabling the logger level.
     *
     * @param level the logger level.
     * @return true if the level is enabled.
     */
    default boolean isEnabled(@NotNull LoggerLevel level) {
        return level.isEnabled();
    }

    /**
     * Override the enabling status of the logger level.
     *
     * @param level   the logger level.
     * @param enabled true if need to be enabled.
     * @return true if the status was changed.
     */
    default boolean setEnabled(@NotNull LoggerLevel level, boolean enabled) {
        return false;
    }

    /**
     * Remove overriding of enabling status if the logger level.
     *
     * @param level the logger level.
     * @return true if the status was changed.
     */
    default boolean applyDefault(@NotNull LoggerLevel level) {
        return false;
    }

    /**
     * Print the warning message.
     *
     * @param owner   the owner of the message.
     * @param message the message.
     */
    default void warning(@NotNull Object owner, @NotNull String message) {
        print(LoggerLevel.WARNING, owner, message);
    }

    /**
     * Print the warning message.
     *
     * @param owner     the owner of the message,
     * @param exception the exception.
     */
    default void warning(@NotNull Object owner, @NotNull Throwable exception) {
        print(LoggerLevel.WARNING, owner, exception);
    }

    /**
     * Print the warning message.
     *
     * @param message the message.
     */
    default void warning(@NotNull String message) {
        print(LoggerLevel.WARNING, message);
    }

    /**
     * Print the warning message.
     *
     * @param exception the exception.
     */
    default void warning(@NotNull Throwable exception) {
        print(LoggerLevel.WARNING, exception);
    }

    /**
     * Print a warning debug message.
     *
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     */
    default <F, S> void warning(
        @NotNull F first,
        @NotNull S second,
        @NotNull Logger.BiFactory<@NotNull F, @NotNull S> messageFactory
    ) {
        print(LoggerLevel.WARNING, first, second, messageFactory);
    }

    /**
     * Print the message.
     *
     * @param level   the level of the message.
     * @param owner   the owner of the message.
     * @param message the message.
     */
    @Deprecated(forRemoval = true)
    void print(@NotNull LoggerLevel level, @NotNull Object owner, @NotNull String message);

    /**
     * Print the message.
     *
     * @param level   the level of the message.
     * @param message the message.
     */
    void print(@NotNull LoggerLevel level, @NotNull String message);

    /**
     * Print the message.
     *
     * @param level     the level of the message.
     * @param owner     the owner of the message.
     * @param exception the exception.
     */
    @Deprecated(forRemoval = true)
    void print(@NotNull LoggerLevel level, @NotNull Object owner, @NotNull Throwable exception);

    /**
     * Print the message.
     *
     * @param level     the level of the message.
     * @param exception the exception.
     */
    void print(@NotNull LoggerLevel level, @NotNull Throwable exception);

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param owner          the owner of the message.
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     * @param <T>            the argument's type.
     */
    @Deprecated(forRemoval = true)
    default <T> void print(
        @NotNull LoggerLevel level,
        @NotNull Object owner,
        @Nullable T arg,
        @NotNull Logger.SinFactory<T> messageFactory
    ) {
        if (isEnabled(level)) {
            print(level, owner, messageFactory.make(arg));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param owner          the owner of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     */
    @Deprecated(forRemoval = true)
    default <F, S> void print(
        @NotNull LoggerLevel level,
        @NotNull Object owner,
        @Nullable F first,
        @Nullable S second,
        @NotNull Logger.BiFactory<F, S> messageFactory
    ) {
        if (isEnabled(level)) {
            print(level, owner, messageFactory.make(first, second));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param owner          the owner of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param third          the third arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     * @param <T>            the third argument's type.
     */
    @Deprecated(forRemoval = true)
    default <F, S, T> void print(
        @NotNull LoggerLevel level,
        @NotNull Object owner,
        @Nullable F first,
        @Nullable S second,
        @Nullable T third,
        @NotNull Logger.TriFactory<F, S, T> messageFactory
    ) {
        if (isEnabled(level)) {
            print(level, owner, messageFactory.make(first, second, third));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     * @param <T>            the argument's type.
     */
    default <T> void print(
        @NotNull LoggerLevel level,
        @NotNull T arg,
        @NotNull Logger.SinFactory<@NotNull T> messageFactory
    ) {
        if (isEnabled(level)) {
            print(level, messageFactory.make(arg));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param arg            the arg for the message factory.
     * @param messageFactory the message factory.
     */
    default void print(@NotNull LoggerLevel level, int arg, @NotNull Logger.IntSinFactory messageFactory) {
        if (isEnabled(level)) {
            print(level, messageFactory.make(arg));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     */
    default <F, S> void print(
        @NotNull LoggerLevel level,
        @NotNull F first,
        @NotNull S second,
        @NotNull Logger.BiFactory<@NotNull F, @NotNull S> messageFactory
    ) {
        if (isEnabled(level)) {
            print(level, messageFactory.make(first, second));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     */
    default <F> void print(
        @NotNull LoggerLevel level,
        @NotNull F first,
        int second,
        @NotNull Logger.ObjIntFactory<@NotNull F> messageFactory
    ) {
        if (isEnabled(level)) {
            print(level, messageFactory.make(first, second));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param messageFactory the message factory.
     */
    default void print(
        @NotNull LoggerLevel level,
        int first,
        int second,
        @NotNull Logger.IntBiFactory messageFactory
    ) {
        if (isEnabled(level)) {
            print(level, messageFactory.make(first, second));
        }
    }

    /**
     * Print a build message.
     *
     * @param level          the level of the message.
     * @param first          the first arg for the message factory.
     * @param second         the second arg for the message factory.
     * @param third          the third arg for the message factory.
     * @param messageFactory the message factory.
     * @param <F>            the first argument's type.
     * @param <S>            the second argument's type.
     * @param <T>            the third argument's type.
     */
    default <F, S, T> void print(
        @NotNull LoggerLevel level,
        @NotNull F first,
        @NotNull S second,
        @NotNull T third,
        @NotNull Logger.TriFactory<@NotNull F, @NotNull S, @NotNull T> messageFactory
    ) {

        if (isEnabled(level)) {
            print(level, messageFactory.make(first, second, third));
        }
    }
}
