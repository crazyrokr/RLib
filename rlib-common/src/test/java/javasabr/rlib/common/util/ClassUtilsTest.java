package javasabr.rlib.common.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ClassUtilsTest {

  @Test
  void shouldResolveCommonTypeWith2Args() {
    // when:
    Class<?> commonType = ClassUtils.commonType("val1", "val2");

    // then:
    Assertions.assertThat(commonType).isEqualTo(String.class);

    // when:
    commonType = ClassUtils.commonType("val1", 50);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Object.class);

    // when:
    commonType = ClassUtils.commonType(15D, 50);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Number.class);
  }

  @Test
  void shouldResolveCommonTypeWith3Args() {
    // when:
    Class<?> commonType = ClassUtils.commonType("val1", "val2", "val3");

    // then:
    Assertions.assertThat(commonType).isEqualTo(String.class);

    // when:
    commonType = ClassUtils.commonType("val1", 50, "val3");

    // then:
    Assertions.assertThat(commonType).isEqualTo(Object.class);

    // when:
    commonType = ClassUtils.commonType(15D, 50, 60F);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Number.class);

    // when:
    commonType = ClassUtils.commonType(
        new HashMap<>(),
        new TreeMap<>(),
        new ConcurrentHashMap<>());

    // then:
    Assertions.assertThat(commonType).isEqualTo(AbstractMap.class);
  }

  @Test
  void shouldResolveCommonTypeWith4Args() {
    // when:
    Class<?> commonType = ClassUtils.commonType("val1", "val2", "val3", "val4");

    // then:
    Assertions.assertThat(commonType).isEqualTo(String.class);

    // when:
    commonType = ClassUtils.commonType("val1", 50, "val3", 25D);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Object.class);

    // when:
    commonType = ClassUtils.commonType(15D, 50, 60F, 25);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Number.class);

    // when:
    commonType = ClassUtils.commonType(
        new HashMap<>(),
        new TreeMap<>(),
        new ConcurrentHashMap<>(),
        new HashMap<>());

    // then:
    Assertions.assertThat(commonType).isEqualTo(AbstractMap.class);
  }

  @Test
  void shouldResolveCommonTypeWith5Args() {
    // when:
    Class<?> commonType = ClassUtils.commonType("val1", "val2", "val3", "val4", "val5");

    // then:
    Assertions.assertThat(commonType).isEqualTo(String.class);

    // when:
    commonType = ClassUtils.commonType("val1", 50, "val3", 25D, 15);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Object.class);

    // when:
    commonType = ClassUtils.commonType(15D, 50, 60F, 25, 22D);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Number.class);

    // when:
    commonType = ClassUtils.commonType(
        new HashMap<>(),
        new TreeMap<>(),
        new ConcurrentHashMap<>(),
        new HashMap<>(),
        new TreeMap<>());

    // then:
    Assertions.assertThat(commonType).isEqualTo(AbstractMap.class);
  }

  @Test
  void shouldResolveCommonTypeWith6Args() {
    // when:
    Class<?> commonType = ClassUtils.commonType("val1", "val2", "val3", "val4", "val5", "val6");

    // then:
    Assertions.assertThat(commonType).isEqualTo(String.class);

    // when:
    commonType = ClassUtils.commonType("val1", 50, "val3", 25D, 15, "val6");

    // then:
    Assertions.assertThat(commonType).isEqualTo(Object.class);

    // when:
    commonType = ClassUtils.commonType(15D, 50, 60F, 25, 22D, 33);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Number.class);

    // when:
    commonType = ClassUtils.commonType(
        new HashMap<>(),
        new TreeMap<>(),
        new ConcurrentHashMap<>(),
        new HashMap<>(),
        new TreeMap<>(),
        new ConcurrentHashMap<>());

    // then:
    Assertions.assertThat(commonType).isEqualTo(AbstractMap.class);
  }

  @Test
  void shouldResolveCommonTypeWithVarargs() {
    // when:
    Class<?> commonType = ClassUtils.commonType("val1", "val2", "val3", "val4", "val5", "val6", "val7");

    // then:
    Assertions.assertThat(commonType).isEqualTo(String.class);

    // when:
    commonType = ClassUtils.commonType("val1", 50, "val3", 25D, 15, "val6", 13F);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Object.class);

    // when:
    commonType = ClassUtils.commonType(15D, 50, 60F, 25, 22D, 33, 77F);

    // then:
    Assertions.assertThat(commonType).isEqualTo(Number.class);

    // when:
    commonType = ClassUtils.commonType(
        new HashMap<>(),
        new TreeMap<>(),
        new ConcurrentHashMap<>(),
        new HashMap<>(),
        new TreeMap<>(),
        new ConcurrentHashMap<>(),
        new HashMap<>());

    // then:
    Assertions.assertThat(commonType).isEqualTo(AbstractMap.class);
  }
}
