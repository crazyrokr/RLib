package javasabr.rlib.compiler;

/**
 * @author JavaSaBr
 */
public interface CompiledClassData {

  byte[] byteCode();

  int size();

  String name();
}
