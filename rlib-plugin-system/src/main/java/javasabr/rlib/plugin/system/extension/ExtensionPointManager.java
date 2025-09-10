package javasabr.rlib.plugin.system.extension;

import javasabr.rlib.collections.dictionary.Dictionary;
import javasabr.rlib.collections.dictionary.DictionaryFactory;
import javasabr.rlib.collections.dictionary.LockableRefDictionary;
import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ExtensionPointManager {

  private static final Logger LOGGER = LoggerManager.getLogger(ExtensionPointManager.class);

  private static final ExtensionPointManager INSTANCE = new ExtensionPointManager();

  public static ExtensionPointManager getInstance() {
    return INSTANCE;
  }

  public static <T> ExtensionPoint<T> register(String id) {
    return getInstance().create(id);
  }

  LockableRefDictionary<String, ExtensionPoint<?>> extensionPoints;

  public ExtensionPointManager() {
    this.extensionPoints = DictionaryFactory.stampedLockBasedRefDictionary();
  }

  public <T> ExtensionPoint<T> create(String id) {
    long stamp = extensionPoints.writeLock();
    try {
      ExtensionPoint<?> exists = extensionPoints.get(id);
      if (exists != null) {
        LOGGER.warning(id, "Extension point:[%s] is already registered"::formatted);
        return ClassUtils.unsafeNNCast(exists);
      }
      var extensionPoint = new ExtensionPoint<T>();
      extensionPoints.put(id, extensionPoint);
      return extensionPoint;
    } finally {
      extensionPoints.writeUnlock(stamp);
    }
  }

  public <T> ExtensionPointManager addExtension(String id, Class<T> type, T extension) {
    getOrCreateExtensionPoint(id).register(extension);
    return this;
  }

  public <T> ExtensionPointManager addExtension(String id, T extension) {
    getOrCreateExtensionPoint(id).register(extension);
    return this;
  }

  public <T> ExtensionPointManager addExtension(String id, T... extensions) {
    getOrCreateExtensionPoint(id).register(extensions);
    return this;
  }

  public <T> ExtensionPoint<T> getOrCreateExtensionPoint(String id, Class<T> type) {
    return getOrCreateExtensionPoint(id);
  }

  public <T> ExtensionPoint<T> getOrCreateExtensionPoint(String id) {

    ExtensionPoint<?> extensionPoint = extensionPoints
        .operations()
        .getInReadLock(id, Dictionary::get);

    if (extensionPoint != null) {
      return ClassUtils.unsafeNNCast(extensionPoint);
    }

    try {
      return create(id);
    } catch (IllegalArgumentException e) {
      return getOrCreateExtensionPoint(id);
    }
  }
}
