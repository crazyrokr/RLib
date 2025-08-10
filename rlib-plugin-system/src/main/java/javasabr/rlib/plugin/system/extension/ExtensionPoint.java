package javasabr.rlib.plugin.system.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javasabr.rlib.common.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ExtensionPoint<T> implements Iterable<T> {

  protected record State<T>(List<T> extensions, Object[] array) {

    public static <T> State<T> empty() {
      return new State<>(List.of(), ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    /**
     * Append the additional extensions to the current state as new state.
     *
     * @param additionalExtensions the additional extension.
     * @return the new state.
     */
    @SafeVarargs
    public final State<T> append(T... additionalExtensions) {

      List<T> result = new ArrayList<>(extensions);
      result.addAll(List.of(additionalExtensions));

      boolean canBeSorted = result
          .stream()
          .allMatch(Comparable.class::isInstance);

      if (canBeSorted) {
        result.sort((first, second) -> ((Comparable<T>) first).compareTo(second));
      }

      return new State<>(List.copyOf(result), result.toArray());
    }
  }

  AtomicReference<State<T>> state;

  public ExtensionPoint() {
    this.state = new AtomicReference<>(State.empty());
  }

  public ExtensionPoint<T> register(T extension) {

    State<T> currentState = state.get();
    State<T> newState = currentState.append(extension);

    while (!state.compareAndSet(currentState, newState)) {
      currentState = state.get();
      newState = currentState.append(extension);
    }

    return this;
  }

  @SafeVarargs
  public final ExtensionPoint<T> register(T... extensions) {

    State<T> currentState = state.get();
    State<T> newState = currentState.append(extensions);

    while (!state.compareAndSet(currentState, newState)) {
      currentState = state.get();
      newState = currentState.append(extensions);
    }

    return this;
  }

  public List<T> extensions() {
    return state.get().extensions;
  }

  @Override
  public void forEach(Consumer<? super T> consumer) {
    Object[] array = state.get().array;
    for (Object obj : array) {
      consumer.accept((T) obj);
    }
  }

  @Override
  public Iterator<T> iterator() {
    return state.get().extensions.iterator();
  }

  public Stream<T> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public Spliterator<T> spliterator() {
    return Spliterators.spliterator(state.get().array, 0);
  }
}
