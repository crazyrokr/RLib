package javasabr.rlib.collections.dictionary;

public interface LinkedHashEntry<K, V, N extends LinkedHashEntry<K, V, N>>
    extends LinkedEntry<N>, HashEntry, RefToRefEntry<K, V> {
}
