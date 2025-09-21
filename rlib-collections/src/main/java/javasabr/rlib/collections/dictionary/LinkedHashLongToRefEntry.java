package javasabr.rlib.collections.dictionary;

public interface LinkedHashLongToRefEntry<V, N extends LinkedHashLongToRefEntry<V, N>>
    extends LinkedEntry<N>, HashEntry, LongToRefEntry<V> {
}
