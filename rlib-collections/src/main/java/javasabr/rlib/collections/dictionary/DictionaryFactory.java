package javasabr.rlib.collections.dictionary;

import javasabr.rlib.collections.dictionary.impl.DefaultMutableRefHashDictionary;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DictionaryFactory {
  public static <K, V> MutableRefDictionary<K, V> mutableRefDictionary() {
    return new DefaultMutableRefHashDictionary<>();
  }
}
