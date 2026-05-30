package com.github.manu585.fourelements.core.geometry;

/**
 * An immutable axis-aligned bounding box defined by its minimum and maximum corners.
 *
 * @param minX minimum x coordinate
 * @param minY minimum y coordinate
 * @param minZ minimum z coordinate
 * @param maxX maximum x coordinate
 * @param maxY maximum y coordinate
 * @param maxZ maximum z coordinate
 */
public record AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {

  /**
   * Creates an AABB from two corner points. The min and max components are
   * automatically determined.
   *
   * @param min one corner
   * @param max the opposite corner
   * @return a new AABB
   */
  public static AABB of(Vec3d min, Vec3d max) {
    return new AABB(
            Math.min(min.x(), max.x()),
            Math.min(min.y(), max.y()),
            Math.min(min.z(), max.z()),
            Math.max(min.x(), max.x()),
            Math.max(min.y(), max.y()),
            Math.max(min.z(), max.z())
    );
  }

  /**
   * Tests whether a point is inside this bounding box (inclusive on all bounds).
   *
   * @param point the point to test
   * @return true if the point is inside or on the boundary
   */
  public boolean contains(Vec3d point) {
    return point.x() >= minX && point.x() <= maxX
            && point.y() >= minY && point.y() <= maxY
            && point.z() >= minZ && point.z() <= maxZ;
  }

  /**
   * Tests whether this bounding box overlaps with another.
   *
   * @param other the other bounding box
   * @return true if the boxes overlap
   */
  public boolean intersects(AABB other) {
    return this.minX <= other.maxX && this.maxX >= other.minX
            && this.minY <= other.maxY && this.maxY >= other.minY
            && this.minZ <= other.maxZ && this.maxZ >= other.minZ;
  }

  /**
   * Expands this bounding box by the given amount on all six sides.
   *
   * @param amount the amount to expand by (can be negative to shrink)
   * @return a new expanded AABB
   */
  public AABB expand(double amount) {
    return new AABB(
            minX - amount, minY - amount, minZ - amount,
            maxX + amount, maxY + amount, maxZ + amount
    );
  }

  /**
   * Expands this bounding box by different amounts on each axis.
   *
   * @param x the amount to expand on the x axis (can be negative to shrink)
   * @param y the amount to expand on the y axis (can be negative to shrink)
   * @param z the amount to expand on the z axis (can be negative to shrink)
   * @return a new expanded AABB
   */
  public AABB expand(double x, double y, double z) {
    return new AABB(
            minX - x, minY - y, minZ - z,
            maxX + x, maxY + y, maxZ + z
    );
  }

  /**
   * Expands this bounding box in the direction of movement to create a swept volume.
   * Positive direction components expand the maximum bound; negative components expand
   * the minimum bound.
   *
   * @param direction the direction of movement
   * @return a new AABB expanded in the direction of movement
   */
  public AABB expand(Vec3d direction) {
    double newMinX = direction.x() < 0 ? minX + direction.x() : minX;
    double newMinY = direction.y() < 0 ? minY + direction.y() : minY;
    double newMinZ = direction.z() < 0 ? minZ + direction.z() : minZ;
    double newMaxX = direction.x() > 0 ? maxX + direction.x() : maxX;
    double newMaxY = direction.y() > 0 ? maxY + direction.y() : maxY;
    double newMaxZ = direction.z() > 0 ? maxZ + direction.z() : maxZ;
    return new AABB(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
  }

  /**
   * Returns the center point of this bounding box.
   *
   * @return the center as a Vec3d
   */
  public Vec3d center() {
    return new Vec3d(
            (minX + maxX) / 2.0,
            (minY + maxY) / 2.0,
            (minZ + maxZ) / 2.0);
  }

}
