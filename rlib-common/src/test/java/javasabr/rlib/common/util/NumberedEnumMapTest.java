package javasabr.rlib.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

class NumberedEnumMapTest {

  public enum TestEnum implements NumberedEnum<@NonNull TestEnum> {
    CONSTANT1(4),
    CONSTANT2(13),
    CONSTANT3(9),
    CONSTANT4(25),
    CONSTANT5(1);

    private static final NumberedEnumMap<TestEnum> MAP = new NumberedEnumMap<>(TestEnum.class);

    private final int number;

    TestEnum(int number) {
      this.number = number;
    }

    @Override
    public int number() {
      return number;
    }
  }

  @Test
  void shouldResolveEnumByNumber() {
    // when\then:
    assertThat(TestEnum.MAP.resolve(4)).isEqualTo(TestEnum.CONSTANT1);
    assertThat(TestEnum.MAP.resolve(13)).isEqualTo(TestEnum.CONSTANT2);
    assertThat(TestEnum.MAP.resolve(9)).isEqualTo(TestEnum.CONSTANT3);
    assertThat(TestEnum.MAP.resolve(25)).isEqualTo(TestEnum.CONSTANT4);
    assertThat(TestEnum.MAP.resolve(900)).isNull();
    assertThat(TestEnum.MAP.resolve(-50)).isNull();
    assertThat(TestEnum.MAP.resolve(24)).isNull();
  }

  @Test
  void shouldRequireEnumByNumber() {
    // when\then:
    assertThat(TestEnum.MAP.require(4)).isEqualTo(TestEnum.CONSTANT1);
    assertThat(TestEnum.MAP.require(13)).isEqualTo(TestEnum.CONSTANT2);
    assertThat(TestEnum.MAP.require(9)).isEqualTo(TestEnum.CONSTANT3);
    assertThat(TestEnum.MAP.require(25)).isEqualTo(TestEnum.CONSTANT4);
    assertThatThrownBy(() -> TestEnum.MAP.require(900))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> TestEnum.MAP.require(-50))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> TestEnum.MAP.require(24))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
