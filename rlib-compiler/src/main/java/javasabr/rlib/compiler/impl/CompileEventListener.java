package javasabr.rlib.compiler.impl;

import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.common.util.array.ArrayFactory;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CompileEventListener implements DiagnosticListener<JavaFileObject> {

  Array<Diagnostic<? extends JavaFileObject>> diagnostics;

  public CompileEventListener() {
    this.diagnostics = Array.ofType(Diagnostic.class);
  }

  public void clear() {
    diagnostics.clear();
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    diagnostics.add(diagnostic);
  }
}
