package javasabr.rlib.common.util.dictionary;

import org.jspecify.annotations.NullMarked;

/**
 * The interface with methods for supporting threadsafe for the {@link IntegerDictionary}.
 *
 * @param <V> the value's type.
 * @author JavaSaBr
 */
@NullMarked
public interface ConcurrentIntegerDictionary<V> extends IntegerDictionary<V>, ConcurrentDictionary<IntKey, V> {

}
