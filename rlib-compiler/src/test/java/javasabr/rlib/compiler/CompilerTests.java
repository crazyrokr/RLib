package javasabr.rlib.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.net.URISyntaxException;
import java.util.Comparator;
import javasabr.rlib.collections.array.Array;
import javasabr.rlib.collections.array.ArrayCollectors;
import javasabr.rlib.common.util.ClassUtils;
import javasabr.rlib.logger.api.LoggerLevel;
import javasabr.rlib.logger.api.LoggerManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author JavaSaBr
 */
public class CompilerTests {

  static {
    LoggerManager.configureDefault(LoggerLevel.DEBUG, true);
  }

  @Test
  void compileTest()
      throws URISyntaxException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
      InstantiationException {

    // given:
    var javaSources = Array.of(
        getClass().getResource("/java/source/TestCompileDependency.java").toURI(),
        getClass().getResource("/java/source/TestCompileJavaSource.java").toURI(),
        getClass().getResource("/java/source/TestCompileRecord.java").toURI());

    // when:
    var compiler = CompilerFactory.newDefaultCompiler();
    var compiledClasses = compiler.compileByUrls(javaSources)
        .stream()
        .sorted(Comparator.comparing(Class::getName))
        .collect(ArrayCollectors.toArray(Class.class));

    // then:
    Assertions.assertEquals(3, compiledClasses.size());

    Assertions.assertEquals("TestCompileDependency", compiledClasses.get(0).getName());
    Assertions.assertEquals("TestCompileJavaSource", compiledClasses.get(1).getName());
    Assertions.assertEquals("TestCompileRecord", compiledClasses.get(2).getName());

    // when:
    var instance1 = ClassUtils.newInstance(compiledClasses.get(0));
    var method = instance1
        .getClass()
        .getMethod("calcInteger");
    var result = method.invoke(instance1);

    // then:
    Assertions.assertEquals(5, result);

    // when:
    var instance2 = ClassUtils.newInstance(compiledClasses.get(1));
    method = instance2
        .getClass()
        .getMethod("makeString");
    result = method.invoke(instance2);

    // then:
    Assertions.assertEquals("testString", result);

    // when:
    Record instance3 = (Record) ClassUtils
        .tryGetConstructor(compiledClasses.get(2), compiledClasses.get(0), String.class)
        .newInstance(instance1, "recordString");

    // then:
    RecordComponent[] recordComponents = instance3
        .getClass()
        .getRecordComponents();

    Assertions.assertEquals(instance1, recordComponents[0].getAccessor().invoke(instance3));
    Assertions.assertEquals("recordString", recordComponents[1].getAccessor().invoke(instance3));
  }
}
