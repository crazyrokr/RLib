package javasabr.rlib.compiler.impl;

import javasabr.rlib.compiler.ByteCode;
import javasabr.rlib.common.util.Utils;
import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.common.util.array.ArrayFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class CompiledClassesClassLoader extends ClassLoader {

  Array<ByteCode> byteCode;

  public CompiledClassesClassLoader() {
    this.byteCode = ArrayFactory.newArray(ByteCode.class);
  }

  /**
   * Add a new compiled class.
   */
  public synchronized void addByteCode(ByteCode byteCode) {
    this.byteCode.add(byteCode);
  }

  @Override
  protected synchronized @Nullable Class<?> findClass(String name) {

    if (byteCode.isEmpty()) {
      return null;
    }

    for (ByteCode byteCode : this.byteCode) {
      if (name.equals(byteCode.name())) {
        byte[] content = byteCode.byteCode();
        return Utils.uncheckedGet(() -> defineClass(name, content, 0, content.length));
      }
    }

    return null;
  }
}
