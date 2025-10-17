package javasabr.rlib.network.packet.registry;

import javasabr.rlib.classpath.ClassPathScanner;
import javasabr.rlib.classpath.ClassPathScannerFactory;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.collections.array.ArrayCollectors;
import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.network.Connection;
import javasabr.rlib.network.annotation.NetworkPacketDescription;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.registry.impl.IdBasedReadableNetworkPacketRegistry;

/**
 * The interface to implement a registry of readable packets.
 *
 * @author JavaSaBr
 */
public interface ReadableNetworkPacketRegistry<R extends IdBasedReadableNetworkPacket<C>, C extends Connection<C>> {

  /**
   * Creates a new empty readable packet registry.
   */
  static ReadableNetworkPacketRegistry<?, ?> empty() {
    return new IdBasedReadableNetworkPacketRegistry<>(IdBasedReadableNetworkPacket.class);
  }

  /**
   * Create a new empty readable packet registry.
   */
  static <
      R extends IdBasedReadableNetworkPacket<C>,
      C extends Connection<C>> ReadableNetworkPacketRegistry<R, C> empty(Class<R> type) {
    return new IdBasedReadableNetworkPacketRegistry<>(type);
  }

  /**
   * Creates a new class path scanning based readable packet registry.
   */
  static <
      R extends IdBasedReadableNetworkPacket<C>,
      C extends Connection<C>> ReadableNetworkPacketRegistry<R, C> classPathBased(Class<R> baseType) {
    var scanner = ClassPathScannerFactory.newDefaultScanner();
    scanner.useSystemClassPath(true);
    scanner.scan();
    return scannerBased(baseType, scanner);
  }

  /**
   * Creates a new class path scanning based readable packet registry by scanning the main class.
   */
  static <
      R extends IdBasedReadableNetworkPacket<C>,
      C extends Connection<C>> ReadableNetworkPacketRegistry<R, C> classPathBased(
          Class<R> baseType,
          Class<?> mainClass) {
    var scanner = ClassPathScannerFactory.newManifestScanner(mainClass);
    scanner.useSystemClassPath(false);
    scanner.scan();
    return scannerBased(baseType, scanner);
  }

  /**
   * Creates a new class path scanning based readable packet registry.
   */
  static <
      R extends IdBasedReadableNetworkPacket<C>,
      C extends Connection<C>> ReadableNetworkPacketRegistry<R, C> scannerBased(
          Class<R> baseType,
          ClassPathScanner scanner) {
    Array<Class<? extends R>> result = scanner
        .findImplementations(IdBasedReadableNetworkPacket.class)
        .stream()
        .filter(type -> type.getAnnotation(NetworkPacketDescription.class) != null)
        .map(ClassUtils::<Class<R>>unsafeNNCast)
        .collect(ArrayCollectors.toArray(Class.class));
    return new IdBasedReadableNetworkPacketRegistry<>(baseType)
        .register(result);
  }

  /**
   * Creates a new readable packet registry.
   */
  @SafeVarargs
  static <
      R extends IdBasedReadableNetworkPacket<C>,
      C extends Connection<C>> ReadableNetworkPacketRegistry<R, C> of(
      Class<R> type,
      Class<? extends R>... classes) {
    return new IdBasedReadableNetworkPacketRegistry<>(type)
        .register(classes, classes.length);
  }

  @SafeVarargs
  static <
      R extends IdBasedReadableNetworkPacket<C>,
      C extends Connection<C>> ReadableNetworkPacketRegistry<R, C> of(
      Class<?> type,
      Class<C> connectionType,
      Class<? extends R>... classes) {
    return new IdBasedReadableNetworkPacketRegistry<>(ClassUtils.<Class<R>>unsafeCast(type))
        .register(classes, classes.length);
  }

  /**
   * Creates a new readable packet registry.
   */
  static <
      R extends IdBasedReadableNetworkPacket<C>,
      C extends Connection<C>> ReadableNetworkPacketRegistry<R, C> of(
      Class<R> type,
      Array<Class<? extends R>> classes) {
    return new IdBasedReadableNetworkPacketRegistry<>(type)
        .register(classes);
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
