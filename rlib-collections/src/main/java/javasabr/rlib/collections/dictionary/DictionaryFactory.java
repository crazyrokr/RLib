package javasabr.rlib.collections.dictionary;

import javasabr.rlib.collections.dictionary.impl.DefaultMutableHashBasedRefDictionary;
import javasabr.rlib.collections.dictionary.impl.StampedLockBasedMutableHashBasedRefDictionary;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DictionaryFactory {
  public static <K, V> MutableRefDictionary<K, V> mutableRefDictionary() {
    return new DefaultMutableHashBasedRefDictionary<>();
  }

  public static <K, V> MutableRefDictionary<K, V> mutableRefDictionary(
      Class<? super K> keyType,
      Class<? super V> valueType) {
    return new DefaultMutableHashBasedRefDictionary<>();
  }

  public static <K, V> LockableMutableRefDictionary<K, V> lockableRefDictionary() {
    return new StampedLockBasedMutableHashBasedRefDictionary<>();
  }

  public static <K, V> LockableMutableRefDictionary<K, V> lockableRefDictionary(
      Class<? super K> keyType,
      Class<? super V> valueType) {
    return new StampedLockBasedMutableHashBasedRefDictionary<>();
  }
}
