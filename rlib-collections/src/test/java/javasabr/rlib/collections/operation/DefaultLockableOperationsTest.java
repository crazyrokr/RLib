package javasabr.rlib.collections.operation;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javasabr.rlib.collections.operation.impl.DefaultLockableOperations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultLockableOperationsTest {

  private static class TestLockableSource implements LockableSource {

    int readLocks;
    int writeLocks;

    @Override
    public long readLock() {
      readLocks++;
      return 0;
    }

    @Override
    public void readUnlock(long stamp) {
      readLocks--;
    }

    @Override
    public long tryOptimisticRead() {
      readLocks++;
      return 0;
    }

    @Override
    public boolean validateLock(long stamp) {
      return false;
    }

    @Override
    public long writeLock() {
      writeLocks++;
      return 0;
    }

    @Override
    public void writeUnlock(long stamp) {
      writeLocks--;
    }
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldGetInReadLock(DefaultLockableOperations<TestLockableSource> operations) {

    // when:
    var result = operations.getInReadLock(source -> {
      Assertions.assertEquals(0, source.writeLocks);
      Assertions.assertEquals(1, source.readLocks);
      return "test";
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("test", result);
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldGetInReadLock2(DefaultLockableOperations<TestLockableSource> operations) {

    // when:
    var result = operations.getInReadLock("_arg", (source, arg1) -> {
      Assertions.assertEquals(0, source.writeLocks);
      Assertions.assertEquals(1, source.readLocks);
      return "test" + arg1;
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("test_arg", result);
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldGetInReadLock3(DefaultLockableOperations<TestLockableSource> operations) {

    // when:
    var result = operations.getInReadLock(15, (source, arg1) -> {
      Assertions.assertEquals(0, source.writeLocks);
      Assertions.assertEquals(1, source.readLocks);
      return "test" + arg1;
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("test15", result);
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldGetInReadLock4(DefaultLockableOperations<TestLockableSource> operations) {

    // when:
    var result = operations.getInReadLock("arg1_", "_arg2", (source, arg1, arg2) -> {
      Assertions.assertEquals(0, source.writeLocks);
      Assertions.assertEquals(1, source.readLocks);
      return arg1 + "test" + arg2;
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("arg1_test_arg2", result);
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldGetInReadLock5(DefaultLockableOperations<TestLockableSource> operations) {

    // when:
    var result = operations.getInReadLock(true, (source, arg1) -> {
      Assertions.assertEquals(0, source.writeLocks);
      Assertions.assertEquals(1, source.readLocks);
      return "test" + arg1;
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("testtrue", result);
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldInReadLock(DefaultLockableOperations<TestLockableSource> operations) {

    // given:
    var capture = new AtomicReference<String>();

    // when:
    operations.inReadLock("test_arg", (source, arg1) -> {
      Assertions.assertEquals(0, source.writeLocks);
      Assertions.assertEquals(1, source.readLocks);
      capture.set("test_" + arg1);
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("test_test_arg", capture.get());
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldGetWriteLock(DefaultLockableOperations<TestLockableSource> operations) {

    // when:
    var result = operations.getInWriteLock("_arg", (source, arg1) -> {
      Assertions.assertEquals(1, source.writeLocks);
      Assertions.assertEquals(0, source.readLocks);
      return "test" + arg1;
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals("test_arg", result);
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldGetWriteLock2(DefaultLockableOperations<TestLockableSource> operations) {

    // when:
    var result = operations.getInWriteLock("arg1_", "_arg2", (source, arg1, arg2) -> {
      Assertions.assertEquals(1, source.writeLocks);
      Assertions.assertEquals(0, source.readLocks);
      return arg1 + "test" + arg2;
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals("arg1_test_arg2", result);
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldInWriteLock(DefaultLockableOperations<TestLockableSource> operations) {

    // given:
    var capture = new AtomicReference<String>();

    // when:
    operations.inWriteLock(source -> {
      Assertions.assertEquals(1, source.writeLocks);
      Assertions.assertEquals(0, source.readLocks);
      capture.set("test");
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("test", capture.get());
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldInWriteLock2(DefaultLockableOperations<TestLockableSource> operations) {

    // given:
    var capture = new AtomicReference<String>();

    // when:
    operations.inWriteLock("test_arg", (source, arg1) -> {
      Assertions.assertEquals(1, source.writeLocks);
      Assertions.assertEquals(0, source.readLocks);
      capture.set("test_" + arg1);
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("test_test_arg", capture.get());
  }

  @ParameterizedTest
  @MethodSource("generateOperations")
  void shouldInWriteLock3(DefaultLockableOperations<TestLockableSource> operations) {

    // given:
    var capture = new AtomicReference<String>();

    // when:
    operations.inWriteLock("arg1_", "_arg2", (source, arg1, arg2) -> {
      Assertions.assertEquals(1, source.writeLocks);
      Assertions.assertEquals(0, source.readLocks);
      capture.set(arg1 + "test" + arg2);
    });

    // then:
    Assertions.assertEquals(0, operations.source().writeLocks);
    Assertions.assertEquals(0, operations.source().readLocks);
    Assertions.assertEquals("arg1_test_arg2", capture.get());
  }

  private static Stream<Arguments> generateOperations() {
    return Stream.of(Arguments.of(new DefaultLockableOperations<>(new TestLockableSource())));
  }
}
