package javasabr.rlib.geometry.bounding;

import javasabr.rlib.geometry.Vector3f;
import javasabr.rlib.geometry.bounding.impl.AbstractBounding;
import javasabr.rlib.geometry.bounding.impl.AxisAlignedBoundingBox;
import javasabr.rlib.geometry.bounding.impl.BoundingSphere;
import org.jspecify.annotations.NullMarked;

/**
 * @author JavaSaBr
 */
@NullMarked
public final class BoundingFactory {

  public static Bounding newBoundingBox(
      Vector3f center,
      Vector3f offset,
      float sizeX,
      float sizeY,
      float sizeZ) {
    return new AxisAlignedBoundingBox(center, offset, sizeX, sizeY, sizeZ);
  }

  public static Bounding newBoundingEmpty() {
    return new AbstractBounding(new Vector3f(), new Vector3f()) {};
  }

  public static Bounding newBoundingSphere(Vector3f center, Vector3f offset, float radius) {
    return new BoundingSphere(center, offset, radius);
  }

  private BoundingFactory() {
    throw new IllegalArgumentException();
  }
}
