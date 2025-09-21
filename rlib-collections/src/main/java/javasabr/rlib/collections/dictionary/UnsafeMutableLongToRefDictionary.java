package javasabr.rlib.collections.dictionary;

public interface UnsafeMutableLongToRefDictionary<V, E extends LongToRefEntry<V>>
    extends MutableLongToRefDictionary<V>, UnsafeLongToRefDictionary<V, E> {

}
