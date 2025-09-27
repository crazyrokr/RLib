package javasabr.rlib.functions;

/**
 * @author JavaSaBr
 */
@FunctionalInterface
public interface ByteFunction<R> {
  R apply(byte value);
}
