package javasabr.rlib.collections.dictionary;

public interface UnsafeMutableIntToRefDictionary<V, E extends IntToRefEntry<V>>
    extends MutableIntToRefDictionary<V>, UnsafeIntToRefDictionary<V, E> {

}
