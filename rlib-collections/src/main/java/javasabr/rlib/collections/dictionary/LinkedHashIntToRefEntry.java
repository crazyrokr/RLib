package javasabr.rlib.collections.dictionary;

public interface LinkedHashIntToRefEntry<V, N extends LinkedHashIntToRefEntry<V, N>>
    extends LinkedEntry<N>, HashEntry, IntToRefEntry<V> {
}
