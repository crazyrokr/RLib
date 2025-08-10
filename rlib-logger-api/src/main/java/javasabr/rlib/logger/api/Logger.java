package javasabr.rlib.logger.api;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

/**
 * @author JavaSaBr
 */
@NullUnmarked
public interface Logger {

  @FunctionalInterface
  interface Factory {

    @NonNull
    String make();
  }

  @FunctionalInterface
  interface N1Factory<F> {

    @NonNull
    String make(F arg1);
  }

  @FunctionalInterface
  interface IntN1Factory {

    @NonNull
    String make(int arg1);
  }

  @FunctionalInterface
  interface N2Factory<F, S> {

    @NonNull
    String make(F arg1, S arg2);
  }

  @FunctionalInterface
  interface N1IntFactory<F> {

    @NonNull
    String make(F arg1, int arg2);
  }

  @FunctionalInterface
  interface Int2Factory {

    @NonNull
    String make(int arg1, int arg2);
  }

  @FunctionalInterface
  interface N3Factory<F, S, T> {

    @NonNull
    String make(F arg1, S arg2, T arg3);
  }

  @FunctionalInterface
  interface N4Factory<F, S, T, FO> {

    @NonNull
    String make(F arg1, S arg2, T arg3, FO arg4);
  }

  /**
   * Print the debug message.
   *
   * @param message the message.
   */
  default void debug(@NonNull String message) {
    print(LoggerLevel.DEBUG, message);
  }

  default void debug(int arg1, @NonNull IntN1Factory factory) {
    print(LoggerLevel.DEBUG, arg1, factory);
  }

  default <T> void debug(T arg1, @NonNull N1Factory<T> factory) {
    print(LoggerLevel.DEBUG, arg1, factory);
  }

  default <F, S> void debug(F arg1, S arg2, @NonNull N2Factory<F, S> factory) {
    print(LoggerLevel.DEBUG, arg1, arg2, factory);
  }

  default <F> void debug(F arg1, int arg2, @NonNull N1IntFactory<F> factory) {
    print(LoggerLevel.DEBUG, arg1, arg2, factory);
  }

  default void debug(int arg1, int arg2, @NonNull Int2Factory factory) {
    print(LoggerLevel.DEBUG, arg1, arg2, factory);
  }

  default <F, S, T> void debug(F arg1, S arg2, T arg3, @NonNull N3Factory<F, S, T> factory) {
    print(LoggerLevel.DEBUG, arg1, arg2, arg3, factory);
  }

  default void error(@NonNull String message) {
    print(LoggerLevel.ERROR, message);
  }

  default void error(@NonNull Throwable exception) {
    print(LoggerLevel.ERROR, exception);
  }

  default void info(@NonNull String message) {
    print(LoggerLevel.INFO, message);
  }

  default <T> void info(T arg1, @NonNull N1Factory<T> factory) {
    print(LoggerLevel.INFO, arg1, factory);
  }

  default <F, S> void info(F arg1, S arg2, @NonNull N2Factory<F, S> factory) {
    print(LoggerLevel.INFO, arg1, arg2, factory);
  }

  default <F, S, T> void info(F arg1, S arg2, T arg3, @NonNull N3Factory<F, S, T> factory) {
    print(LoggerLevel.INFO, arg1, arg2, arg3, factory);
  }

  /**
   * Check of enabling the logger level.
   */
  default boolean enabled(@NonNull LoggerLevel level) {
    return level.isEnabled();
  }

  /**
   * Override the enabling status of the logger level.
   */
  default boolean overrideEnabled(@NonNull LoggerLevel level, boolean enabled) {
    return false;
  }

  /**
   * Remove overriding of enabling status if the logger level.
   */
  default boolean resetToDefault(@NonNull LoggerLevel level) {
    return false;
  }

  default void warning(@NonNull String message) {
    print(LoggerLevel.WARNING, message);
  }

  default void warning(@NonNull Throwable exception) {
    print(LoggerLevel.WARNING, exception);
  }

  default <A> void warning(A arg1, @NonNull N1Factory<A> factory) {
    print(LoggerLevel.WARNING, arg1, factory);
  }

  default <F, S> void warning(F arg1, S arg2, @NonNull N2Factory<F, S> factory) {
    print(LoggerLevel.WARNING, arg1, arg2, factory);
  }

  default <F, S, T, FO> void warning(
      F arg1,
      S arg2,
      T arg3,
      FO arg4,
      @NonNull N4Factory<F, S, T, FO> factory) {
    print(LoggerLevel.WARNING, arg1, arg2, arg3, arg4, factory);
  }

  void print(@NonNull LoggerLevel level, @NonNull String message);

  void print(@NonNull LoggerLevel level, @NonNull Throwable exception);

  default <T> void print(@NonNull LoggerLevel level, T arg1, @NonNull N1Factory<T> factory) {
    if (enabled(level)) {
      print(level, factory.make(arg1));
    }
  }

  default void print(@NonNull LoggerLevel level, int arg1, @NonNull IntN1Factory factory) {
    if (enabled(level)) {
      print(level, factory.make(arg1));
    }
  }

  default <F, S> void print(
      @NonNull LoggerLevel level,
      F arg1,
      S arg2,
      @NonNull N2Factory<F, S> factory) {
    if (enabled(level)) {
      print(level, factory.make(arg1, arg2));
    }
  }

  default <F> void print(
      @NonNull LoggerLevel level,
      F arg1,
      int arg2,
      @NonNull N1IntFactory<F> factory) {
    if (enabled(level)) {
      print(level, factory.make(arg1, arg2));
    }
  }

  default void print(
      @NonNull LoggerLevel level,
      int arg1,
      int arg2,
      @NonNull Int2Factory factory) {
    if (enabled(level)) {
      print(level, factory.make(arg1, arg2));
    }
  }

  default <F, S, T> void print(
      @NonNull LoggerLevel level,
      F arg1,
      S arg2,
      T arg3,
      @NonNull N3Factory<F, S, T> factory) {
    if (enabled(level)) {
      print(level, factory.make(arg1, arg2, arg3));
    }
  }

  default <F, S, T, FO> void print(
      @NonNull LoggerLevel level,
      F arg1,
      S arg2,
      T arg3,
      FO arg4,
      @NonNull N4Factory<F, S, T, FO> factory) {
    if (enabled(level)) {
      print(level, factory.make(arg1, arg2, arg3, arg4));
    }
  }
}
