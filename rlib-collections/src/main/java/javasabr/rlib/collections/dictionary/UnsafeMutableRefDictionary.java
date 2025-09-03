package javasabr.rlib.collections.dictionary;

public interface UnsafeMutableRefDictionary<K, V, E extends Entry<K, V>>
    extends MutableRefDictionary<K, V>, UnsafeRefDictionary<K, V, E> {

}
