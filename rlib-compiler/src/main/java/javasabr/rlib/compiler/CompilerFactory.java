package javasabr.rlib.compiler;

import javasabr.rlib.compiler.impl.JdkCompiler;
import javax.tools.ToolProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author JavaSaBr
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilerFactory {

  /**
   * Check availability compiler API in current runtime.
   */
  public static boolean isAvailableCompiler() {
    return ToolProvider.getSystemJavaCompiler() != null;
  }

  public static Compiler newDefaultCompiler() {
    return new JdkCompiler(true);
  }
}
