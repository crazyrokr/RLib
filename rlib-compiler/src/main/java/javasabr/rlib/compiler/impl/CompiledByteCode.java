package javasabr.rlib.compiler.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import javasabr.rlib.compiler.ByteCode;
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
public class CompiledByteCode extends SimpleJavaFileObject implements ByteCode {

  ByteArrayOutputStream outputStream;

  @Getter
  String name;

  public CompiledByteCode(String name) {
    super(URI.create("byte:///" + name.replace('/', '.') + Compiler.CLASS_EXTENSION), Kind.CLASS);
    this.outputStream = new ByteArrayOutputStream();
    this.name = name;

  }

  @Override
  public byte[] byteCode() {
    return outputStream.toByteArray();
  }

  @Override
  public OutputStream openOutputStream() {
    return outputStream;
  }
}
