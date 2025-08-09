package javasabr.rlib.geometry.bounding.impl;

import static java.lang.Math.abs;

import javasabr.rlib.geometry.Vector3f;
import javasabr.rlib.geometry.Vector3fBuffer;
import javasabr.rlib.geometry.bounding.Bounding;
import javasabr.rlib.geometry.bounding.BoundingType;
import javasabr.rlib.geometry.util.GeometryUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * @author JavaSaBr
 */
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BoundingSphere extends AbstractBounding {

  float radius, squareRadius;

  public BoundingSphere(Vector3f center, Vector3f offset, float radius) {
    super(center, offset);
    this.radius = radius;
    this.squareRadius = radius * radius;
  }

  @Override
  public boolean contains(float x, float y, float z) {

    float startX = calculateResultCenterX();
    float centerY = this.calculateResultCenterY();
    float centerZ = this.calculateResultCenterZ();

    return GeometryUtils.getSquareDistance(startX, centerY, centerZ, x, y, z) < squareRadius;
  }

  @Override
  public float calculateResultCenterZ() {
    return center.z() + offset.z();
  }

  @Override
  public float calculateResultCenterY() {
    return center.y() + offset.y();
  }

  @Override
  public float calculateResultCenterX() {
    return center.x() + offset.x();
  }

  @Override
  public BoundingType boundingType() {
    return BoundingType.SPHERE;
  }

  @Override
  public Vector3f calculateResultCenter(Vector3fBuffer buffer) {

    Vector3f vector = buffer
        .next()
        .set(center);

    if (offset.isZero()) {
      return vector;
    }

    return vector.addLocal(offset);
  }

  @Override
  public boolean intersects(Bounding bounding, Vector3fBuffer buffer) {
    switch (bounding.boundingType()) {
      case EMPTY: {
        return false;
      }
      case SPHERE: {

        BoundingSphere sphere = (BoundingSphere) bounding;
        Vector3f diff = this
            .calculateResultCenter(buffer).subtractLocal(sphere.calculateResultCenter(buffer));

        float rsum = radius() + sphere.radius();

        return diff.dot(diff) <= rsum * rsum;
      }
      case AXIS_ALIGNED_BOX: {

        AxisAlignedBoundingBox box = (AxisAlignedBoundingBox) bounding;

        Vector3f center = this.calculateResultCenter(buffer);
        Vector3f target = box.calculateResultCenter(buffer);

        return abs(target.x() - center.x()) < radius() + box.sizeX()
            && abs(target.y() - center.y()) < radius() + box.sizeY()
            && abs(target.z() - center.z()) < radius() + box.sizeZ();
      }
    }

    return false;
  }

  @Override
  public boolean intersects(Vector3f start, Vector3f direction, Vector3fBuffer buffer) {

    Vector3f diff = buffer
        .next()
        .set(start)
        .subtractLocal(this.calculateResultCenter(buffer));

    float a = start.dot(diff) - squareRadius;

    if (a <= 0.0) {
      return true;
    }

    float b = direction.dot(diff);

    return b < 0.0 && b * b >= a;

  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " [radius=" + radius + ", squareRadius=" + squareRadius + "]";
  }
}
