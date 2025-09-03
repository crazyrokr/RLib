package javasabr.rlib.collections.dictionary.impl;

public abstract class AbstractHashBasedDictionary<K, V> extends AbstractDictionary<K, V> {

  protected static int indexFor(int hash, int length) {
    return hash & length - 1;
  }

  protected static int hash(int hashcode) {
    hashcode ^= hashcode >>> 20 ^ hashcode >>> 12;
    return hashcode ^ hashcode >>> 7 ^ hashcode >>> 4;
  }
}
