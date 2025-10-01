package javasabr.rlib.network.packet.registry;

import javasabr.rlib.classpath.ClassPathScanner;
import javasabr.rlib.classpath.ClassPathScannerFactory;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.collections.array.ArrayCollectors;
import javasabr.rlib.network.annotation.NetworkPacketDescription;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.registry.impl.IdBasedReadableNetworkPacketRegistry;

/**
 * The interface to implement a registry of readable packets.
 *
 * @author JavaSaBr
 */
public interface ReadableNetworkPacketRegistry<R extends IdBasedReadableNetworkPacket<R>> {

  /**
   * Creates a new empty readable packet registry.
   */
  static ReadableNetworkPacketRegistry<?> empty() {
    return new IdBasedReadableNetworkPacketRegistry<>(IdBasedReadableNetworkPacket.class);
  }

  /**
   * Create a new empty readable packet registry.
   */
  static <T extends IdBasedReadableNetworkPacket<T>> ReadableNetworkPacketRegistry<T> empty(Class<T> type) {
    return new IdBasedReadableNetworkPacketRegistry<>(type);
  }

  /**
   * Creates a new class path scanning based readable packet registry.
   */
  static ReadableNetworkPacketRegistry<?> classPathBased() {

    var scanner = ClassPathScannerFactory.newDefaultScanner();
    scanner.useSystemClassPath(true);
    scanner.scan();

    return of(scanner);
  }

  /**
   * Creates a new class path scanning based readable packet registry by scanning the main class.
   */
  static ReadableNetworkPacketRegistry<?> classPathBased(Class<?> mainClass) {

    var scanner = ClassPathScannerFactory.newManifestScanner(mainClass);
    scanner.useSystemClassPath(false);
    scanner.scan();

    return of(scanner);
  }

  /**
   * Creates a new class path scanning based readable packet registry.
   */
  static ReadableNetworkPacketRegistry<?> of(ClassPathScanner scanner) {

    var result = scanner
        .findImplementations(IdBasedReadableNetworkPacket.class)
        .stream()
        .filter(type -> type.getAnnotation(NetworkPacketDescription.class) != null)
        .collect(ArrayCollectors.<Class<? extends IdBasedReadableNetworkPacket>>toArray(Class.class));

    var registry = new IdBasedReadableNetworkPacketRegistry<>(IdBasedReadableNetworkPacket.class);
    registry.register(result);

    return registry;
  }

  /**
   * Creates a new readable packet registry.
   */
  @SafeVarargs
  static <T extends IdBasedReadableNetworkPacket<T>> ReadableNetworkPacketRegistry<T> of(
      Class<T> type,
      Class<? extends T>... classes) {
    var registry = new IdBasedReadableNetworkPacketRegistry<>(type);
    registry.register(classes, classes.length);
    return registry;
  }

  /**
   * Creates a new readable packet registry.
   */
  static <T extends IdBasedReadableNetworkPacket<T>> ReadableNetworkPacketRegistry<T> of(
      Class<T> type,
      Array<Class<? extends T>> classes) {

    var registry = new IdBasedReadableNetworkPacketRegistry<>(type);
    registry.register(classes);

    return registry;
  }

  /**
   * Resolve a network packet prototype based on packet id.
   *
   * @param id the network packet id.
   * @return the resolve prototype.
   * @throws IllegalArgumentException if can't resolve a prototype by the id.
   */
  R resolvePrototypeById(int id);
}
