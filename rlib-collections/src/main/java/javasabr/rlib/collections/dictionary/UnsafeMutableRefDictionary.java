package javasabr.rlib.collections.dictionary;

public interface UnsafeMutableRefDictionary<K, V, E extends RefToRefEntry<K, V>>
    extends MutableRefDictionary<K, V>, UnsafeRefDictionary<K, V, E> {

}
