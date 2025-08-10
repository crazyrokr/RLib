package javasabr.rlib.compiler;

import java.net.URI;
import java.nio.file.Path;
import javasabr.rlib.common.util.array.Array;

/**
 * @author JavaSaBr
 */
public interface Compiler {

  String SOURCE_EXTENSION = ".java";
  String CLASS_EXTENSION = ".class";

  Array<Class<?>> compileByUrls(Array<URI> urls);
  Array<Class<?>> compileFiles(Array<Path> files);
  Array<Class<?>> compileDirectories(Array<Path> directories);
}
