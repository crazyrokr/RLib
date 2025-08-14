package javasabr.rlib.collections.array;

import static java.util.Collections.unmodifiableSet;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArrayCollectors {

  private static Set<Characteristics> CH_ID =
      unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

  private static Set<Characteristics> CH_ID_CONC = unmodifiableSet(EnumSet.of(
      Characteristics.IDENTITY_FINISH,
      Characteristics.CONCURRENT));

  private static <T, M extends MutableArray<T>> Collector<T, M, Array<T>> collector(
      Class<? super T> type,
      Function<Class<? super T>, M> arrayFactory) {
    return new Collector<>() {

      private final Supplier<M> supplier = () -> arrayFactory.apply(type);

      @Override
      public Supplier<M> supplier() {
        return supplier;
      }

      @Override
      public BiConsumer<M, T> accumulator() {
        return MutableArray::add;
      }

      @Override
      public BinaryOperator<M> combiner() {
        return (source, toAdd) -> {
          source.addAll(toAdd);
          return source;
        };
      }

      @Override
      public Function<M, Array<T>> finisher() {
        return Array::copyOf;
      }

      @Override
      public Set<Characteristics> characteristics() {
        return CH_ID;
      }
    };
  }

  private static <T, A extends MutableArray<T>> Collector<T, A, A> mutableCollector(
      Class<? super T> type,
      Function<Class<? super T>, A> arrayFactory) {
    return new Collector<>() {

      private final Supplier<A> supplier = () -> arrayFactory.apply(type);

      @Override
      public Supplier<A> supplier() {
        return supplier;
      }

      @Override
      public BiConsumer<A, T> accumulator() {
        return MutableArray::add;
      }

      @Override
      public BinaryOperator<A> combiner() {
        return (source, toAdd) -> {
          source.addAll(toAdd);
          return source;
        };
      }

      @Override
      public Function<A, A> finisher() {
        return array -> array;
      }

      @Override
      public Set<Characteristics> characteristics() {
        return CH_ID;
      }
    };
  }

  public static <T> Collector<T, MutableArray<T>, MutableArray<T>> toMutableArray(Class<? super T> type) {
    return mutableCollector(type, ArrayFactory::mutableArray);
  }

  public static <T> Collector<T, MutableArray<T>, Array<T>> toArray(Class<? super T> type) {
    return collector(type, ArrayFactory::mutableArray);
  }
}
