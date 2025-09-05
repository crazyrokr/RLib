package javasabr.rlib.geometry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Ray3f {

  Vector3f start, direction;

  /**
   * Construct empty ray in zero point and zero direction.
   */
  public Ray3f() {
    this(new Vector3f(), new Vector3f());
  }

  public final void direction(Vector3f direction) {
    this.direction.set(direction);
  }

  public final void start(Vector3f start) {
    this.start.set(start);
  }

  @Override
  public String toString() {
    return "Ray3f{" + "start=" + start + ", direction=" + direction + '}';
  }
}
