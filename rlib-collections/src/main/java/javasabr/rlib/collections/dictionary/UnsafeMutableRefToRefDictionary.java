package javasabr.rlib.collections.dictionary;

public interface UnsafeMutableRefToRefDictionary<K, V, E extends RefToRefEntry<K, V>>
    extends MutableRefToRefDictionary<K, V>, UnsafeRefToRefDictionary<K, V, E> {

}
