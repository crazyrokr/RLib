package javasabr.rlib.network;

import javasabr.rlib.collections.array.Array;
import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.network.annotation.NetworkPacketDescription;
import javasabr.rlib.network.impl.DefaultConnection;
import javasabr.rlib.network.packet.IdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.impl.AbstractIdBasedReadableNetworkPacket;
import javasabr.rlib.network.packet.impl.DefaultReadableNetworkPacket;
import javasabr.rlib.network.packet.registry.impl.IdBasedReadableNetworkPacketRegistry;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author JavaSaBr
 */
public class IdBasedReadableNetworkPacketRegistryTest {

  @NoArgsConstructor
  @NetworkPacketDescription(id = 1)
  public static class Impl1 extends DefaultReadableNetworkPacket {}

  @NoArgsConstructor
  @NetworkPacketDescription(id = 2)
  public static class Impl2 extends DefaultReadableNetworkPacket {}

  @NoArgsConstructor
  @NetworkPacketDescription(id = 3)
  public static class Impl3 extends DefaultReadableNetworkPacket {}

  @NoArgsConstructor
  private static class PrivateBase extends AbstractIdBasedReadableNetworkPacket<PrivateBase> {}

  @NoArgsConstructor
  @NetworkPacketDescription(id = 1)
  private static class PrivateImpl1 extends PrivateBase {}

  @NoArgsConstructor
  @NetworkPacketDescription(id = 10)
  private static class PrivateImpl2 extends PrivateBase {}

  @NoArgsConstructor
  public static class PublicBase extends AbstractIdBasedReadableNetworkPacket<PublicBase> {}

  @NoArgsConstructor
  @NetworkPacketDescription(id = 1)
  public static class PublicImpl1 extends PublicBase {}

  @NoArgsConstructor
  @NetworkPacketDescription(id = 5)
  public static class PublicImpl2 extends PublicBase {}

  @Test
  void shouldBeCreated() {
    Assertions.assertDoesNotThrow(
        () -> new IdBasedReadableNetworkPacketRegistry<>(IdBasedReadableNetworkPacket.class));
  }

  @Test
  void shouldRegister3PacketsByArray() {

    var registry = new IdBasedReadableNetworkPacketRegistry<>(IdBasedReadableNetworkPacket.class)
        .register(Array.typed(Class.class, Impl1.class, Impl2.class, Impl3.class));

    Assertions.assertInstanceOf(Impl1.class, registry.resolvePrototypeById(1));
    Assertions.assertInstanceOf(Impl2.class, registry.resolvePrototypeById(2));
    Assertions.assertInstanceOf(Impl3.class, registry.resolvePrototypeById(3));
  }

  @Test
  void shouldRegister3PacketsByVarargs() {

    var registry = new IdBasedReadableNetworkPacketRegistry<>(IdBasedReadableNetworkPacket.class)
        .register(Impl1.class, Impl2.class, Impl3.class);

    Assertions.assertInstanceOf(Impl1.class, registry.resolvePrototypeById(1));
    Assertions.assertInstanceOf(Impl2.class, registry.resolvePrototypeById(2));
    Assertions.assertInstanceOf(Impl3.class, registry.resolvePrototypeById(3));
  }

  @Test
  void shouldRegister3PacketsBySingle() {

    var registry = new IdBasedReadableNetworkPacketRegistry<>(IdBasedReadableNetworkPacket.class)
        .register(Impl1.class)
        .register(Impl2.class)
        .register(Impl3.class);

    Assertions.assertInstanceOf(Impl1.class, registry.resolvePrototypeById(1));
    Assertions.assertInstanceOf(Impl2.class, registry.resolvePrototypeById(2));
    Assertions.assertInstanceOf(Impl3.class, registry.resolvePrototypeById(3));
  }

  @Test
  void shouldRegister2PrivatePacketsBySingle() {

    var registry = new IdBasedReadableNetworkPacketRegistry<>(PrivateBase.class)
        .register(PrivateImpl1.class, PrivateImpl1::new)
        .register(PrivateImpl2.class, PrivateImpl2::new);

    Assertions.assertInstanceOf(PrivateImpl1.class, registry.resolvePrototypeById(1));
    Assertions.assertInstanceOf(PrivateImpl2.class, registry.resolvePrototypeById(10));
  }

  @Test
  void shouldNotAcceptWrongTypes() {

    var array = Array.typed(
        Class.class,
        PrivateImpl1.class,
        PrivateImpl2.class,
        PublicImpl1.class,
        PublicImpl2.class);

    var registry = new IdBasedReadableNetworkPacketRegistry<>(PublicBase.class)
        .register(ClassUtils.<Array<Class<? extends PublicBase>>>unsafeNNCast(array));

    Assertions.assertInstanceOf(PublicImpl1.class, registry.resolvePrototypeById(1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> registry.resolvePrototypeById(2));
    Assertions.assertInstanceOf(PublicImpl2.class, registry.resolvePrototypeById(5));
  }
}
