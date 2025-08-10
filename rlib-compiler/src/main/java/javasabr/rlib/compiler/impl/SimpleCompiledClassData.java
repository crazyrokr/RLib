package javasabr.rlib.compiler.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import javasabr.rlib.compiler.CompiledClassData;
import javasabr.rlib.compiler.Compiler;
import javax.tools.SimpleJavaFileObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class SimpleCompiledClassData extends SimpleJavaFileObject implements CompiledClassData {

  ByteArrayOutputStream outputStream;

  @Getter
  String name;

  public SimpleCompiledClassData(String name) {
    super(URI.create("byte:///" + name.replace('/', '.') + Compiler.CLASS_EXTENSION), Kind.CLASS);
    this.outputStream = new ByteArrayOutputStream();
    this.name = name;

  }

  @Override
  public byte[] byteCode() {
    return outputStream.toByteArray();
  }

  @Override
  public int size() {
    return outputStream.size();
  }

  @Override
  public OutputStream openOutputStream() {
    return outputStream;
  }
}
