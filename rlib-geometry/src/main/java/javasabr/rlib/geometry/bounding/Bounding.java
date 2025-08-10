package javasabr.rlib.geometry.bounding;

import javasabr.rlib.geometry.Quaternion4f;
import javasabr.rlib.geometry.Ray3f;
import javasabr.rlib.geometry.Vector3f;
import javasabr.rlib.geometry.Vector3fBuffer;

/**
 * @author JavaSaBr
 */
public interface Bounding {

  /**
   * Return true if this bounding contains the point.
   */
  boolean contains(float x, float y, float z);

  /**
   * Return true if this bounding contains the point.
   */
  boolean contains(Vector3f point);

  /**
   * Get a distance from a center of a bounding to a point.
   */
  float distanceTo(Vector3f point);

  BoundingType boundingType();

  Vector3f center();

  void center(Vector3f center);

  Vector3f offset();

  /**
   * Get a result center of this bounding.
   */
  Vector3f calculateResultCenter(Vector3fBuffer buffer);

  /**
   * Get a result X coordinate of the center of this bounding.
   */
  float calculateResultCenterX();

  /**
   * Get a result Y coordinate of the center of this bounding.
   */
  float calculateResultCenterY();

  /**
   * Get a result Z coordinate of the center of this bounding.
   */
  float calculateResultCenterZ();

  /**
   * Check this bounding that it intersects with other bounding.
   */
  boolean intersects(Bounding bounding, Vector3fBuffer buffer);

  /**
   * Check this bounding that it intersects with a ray.
   */
  boolean intersects(Ray3f ray, Vector3fBuffer buffer);

  /**
   * Check this bounding that it intersects with a ray.
   */
  boolean intersects(Vector3f start, Vector3f direction, Vector3fBuffer buffer);

  /**
   * Update a rotation of a bounding.
   */
  void update(Quaternion4f rotation, Vector3fBuffer buffer);
}
