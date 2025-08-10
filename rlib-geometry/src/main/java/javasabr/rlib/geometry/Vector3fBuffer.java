package javasabr.rlib.geometry;

/**
 * @author JavaSaBr
 */
public interface Vector3fBuffer {

  Vector3fBuffer NO_REUSE = new Vector3fBuffer() {

    @Override
    public Vector3f next() {
      return new Vector3f();
    }

    @Override
    public Vector3f next(Vector3f source) {
      return new Vector3f(source);
    }

    @Override
    public Vector3f next(float x, float y, float z) {
      return new Vector3f(x, y, z);
    }
  };

  /**
   * Take a next free vector.
   */
  Vector3f next();

  /**
   * Take a next free vector with copied values from the source vector.
   */
  default Vector3f next(Vector3f source) {
    return next().set(source);
  }

  /**
   * Take a next free vector with copied values.
   */
  default Vector3f next(float x, float y, float z) {
    return next().set(x, y, z);
  }
}
