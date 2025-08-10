package javasabr.rlib.compiler.impl;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import javasabr.rlib.common.util.FileUtils;
import javasabr.rlib.compiler.Compiler;
import javasabr.rlib.common.util.array.Array;
import javasabr.rlib.common.util.array.ArrayCollectors;
import javasabr.rlib.logger.api.Logger;
import javasabr.rlib.logger.api.LoggerManager;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class JdkCompiler implements Compiler {

  private static final Logger LOGGER = LoggerManager.getLogger(JdkCompiler.class);

  private static final Class<?>[] EMPTY_CLASSES = new Class[0];

  CompileEventListener listener;
  JavaCompiler compiler;
  CompiledClassesClassLoader loader;
  CompiledJavaFileManager fileManager;

  boolean showDiagnostic;

  public JdkCompiler(boolean showDiagnostic) {
    this.compiler = ToolProvider.getSystemJavaCompiler();
    this.listener = new CompileEventListener();
    this.loader = new CompiledClassesClassLoader();

    StandardJavaFileManager standardJavaFileManager = compiler
        .getStandardFileManager(listener, null, null);

    this.fileManager = new CompiledJavaFileManager(standardJavaFileManager, loader);
    this.showDiagnostic = showDiagnostic;
  }

  @Override
  public Array<Class<?>> compileByUrls(Array<URI> urls) {
    return compile(
        Array.empty(),
        urls
            .stream()
            .map(JavaFileSource::new)
            .collect(ArrayCollectors.toArray(JavaFileObject.class)));
  }

  @Override
  public Array<Class<?>> compileFiles(Array<Path> files) {
    return compile(
        Array.empty(),
        files
            .stream()
            .map(JavaFileSource::new)
            .collect(ArrayCollectors.toArray(JavaFileObject.class)));
  }

  @Override
  public Array<Class<?>> compileDirectories(Array<Path> directories) {

    Array<Path> files = Array.ofType(Path.class);

    for (Path directory : directories) {
      if (!Files.exists(directory) || !Files.isDirectory(directory)) {
        continue;
      }

      FileUtils.addFilesTo(files, directory, false, Compiler.SOURCE_EXTENSION);
    }

    if (files.isEmpty()) {
      return Array.empty();
    }

    return compile(
        Array.empty(),
        files
            .stream()
            .map(JavaFileSource::new)
            .collect(ArrayCollectors.toArray(JavaFileObject.class)));
  }

  protected synchronized Array<Class<?>> compile(
      @Nullable Array<String> options,
      Array<JavaFileObject> sources) {

    LOGGER.debug(sources.size(), "Start compiling [%s] source files..."::formatted);

    JavaCompiler compiler = compiler();
    CompiledJavaFileManager fileManager = fileManager();
    CompileEventListener listener = listener();
    CompiledClassesClassLoader loader = loader();
    try {

      CompilationTask task = compiler.getTask(null, fileManager, listener, options, null, sources);
      task.call();

      Array<Diagnostic<? extends JavaFileObject>> diagnostics = listener.diagnostics();
      if (showDiagnostic() && !diagnostics.isEmpty()) {
        LOGGER.warning("Compilation messages:");
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
          LOGGER.warning(String.valueOf(diagnostic));
        }
      }

      Array<Class<?>> result = Array.ofType(Class.class);
      String[] classNames = fileManager.classNames();

      LOGGER.debug(classNames.length, "Got [%s] compiled class names"::formatted);

      for (String className : classNames) {
        LOGGER.debug(className, "Try to load class:[%s]"::formatted);
        try {
          Class<?> klass = Class.forName(className, false, loader);
          result.add(klass);
        } catch (ClassNotFoundException e) {
          LOGGER.warning(e);
        }
      }

      return result;
    } finally {
      listener.clear();
      fileManager.clear();
    }
  }
}
