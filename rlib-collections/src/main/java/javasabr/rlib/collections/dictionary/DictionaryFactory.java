package javasabr.rlib.collections.dictionary;

import javasabr.rlib.collections.dictionary.impl.DefaultMutableHashBasedIntToRefDictionary;
import javasabr.rlib.collections.dictionary.impl.DefaultMutableHashBasedLongToRefDictionary;
import javasabr.rlib.collections.dictionary.impl.DefaultMutableHashBasedRefDictionary;
import javasabr.rlib.collections.dictionary.impl.StampedLockBasedHashBasedRefDictionary;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DictionaryFactory {
  public static <K, V> MutableRefDictionary<K, V> mutableRefDictionary() {
    return new DefaultMutableHashBasedRefDictionary<>();
  }

  public static <V> MutableIntToRefDictionary<V> mutableIntToRefDictionary() {
    return new DefaultMutableHashBasedIntToRefDictionary<>();
  }

  public static <V> MutableLongToRefDictionary<V> mutableLongToRefDictionary() {
    return new DefaultMutableHashBasedLongToRefDictionary<>();
  }

  public static <K, V> MutableRefDictionary<K, V> mutableRefDictionary(
      Class<? super K> keyType,
      Class<? super V> valueType) {
    return new DefaultMutableHashBasedRefDictionary<>();
  }

  public static <K, V> LockableRefDictionary<K, V> stampedLockBasedRefDictionary() {
    return new StampedLockBasedHashBasedRefDictionary<>();
  }

  public static <K, V> LockableRefDictionary<K, V> stampedLockBasedRefDictionary(
      Class<? super K> keyType,
      Class<? super V> valueType) {
    return new StampedLockBasedHashBasedRefDictionary<>();
  }
}
