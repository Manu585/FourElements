package com.github.manu585.fourelements.core.geometry;

/**
 * Low-level collision math utilities using the slab method for ray-AABB intersection.
 */
public final class CollisionMath {

  private static final double EPSILON = 1e-12;

  private CollisionMath() {
    throw new AssertionError("No instances");
  }

  /**
   * Computes the parametric t value in [0, 1] of the first intersection of a ray
   * starting at {@code origin} traveling in {@code direction} against a target AABB
   * that has been expanded by the given half-extents.
   * <p>
   * Uses the slab method for ray-AABB intersection.
   *
   * @param origin      the origin of the ray
   * @param direction   the direction (and magnitude) of the ray; t=1 represents the full displacement
   * @param halfExtents half-extents to expand the target AABB by (use Vec3d.ZERO if already expanded)
   * @param target      the target AABB
   * @return parametric t in [0, 1] of the first intersection, or -1 if no hit
   */
  public static double computeSweptIntersectionT(Vec3d origin, Vec3d direction, Vec3d halfExtents, AABB target) {
    double expandedMinX = target.minX() - halfExtents.x();
    double expandedMinY = target.minY() - halfExtents.y();
    double expandedMinZ = target.minZ() - halfExtents.z();
    double expandedMaxX = target.maxX() + halfExtents.x();
    double expandedMaxY = target.maxY() + halfExtents.y();
    double expandedMaxZ = target.maxZ() + halfExtents.z();

    double tMin = Double.NEGATIVE_INFINITY;
    double tMax = Double.POSITIVE_INFINITY;

    // X slab
    if (Math.abs(direction.x()) < EPSILON) {
      if (origin.x() < expandedMinX || origin.x() > expandedMaxX) {
        return -1;
      }
    } else {
      double invD = 1.0 / direction.x();
      double t1 = (expandedMinX - origin.x()) * invD;
      double t2 = (expandedMaxX - origin.x()) * invD;
      if (t1 > t2) {
        double tmp = t1;
        t1 = t2;
        t2 = tmp;
      }
      tMin = Math.max(tMin, t1);
      tMax = Math.min(tMax, t2);
      if (tMin > tMax) {
        return -1;
      }
    }

    // Y slab
    if (Math.abs(direction.y()) < EPSILON) {
      if (origin.y() < expandedMinY || origin.y() > expandedMaxY) {
        return -1;
      }
    } else {
      double invD = 1.0 / direction.y();
      double t1 = (expandedMinY - origin.y()) * invD;
      double t2 = (expandedMaxY - origin.y()) * invD;
      if (t1 > t2) {
        double tmp = t1;
        t1 = t2;
        t2 = tmp;
      }
      tMin = Math.max(tMin, t1);
      tMax = Math.min(tMax, t2);
      if (tMin > tMax) {
        return -1;
      }
    }

    // Z slab
    if (Math.abs(direction.z()) < EPSILON) {
      if (origin.z() < expandedMinZ || origin.z() > expandedMaxZ) {
        return -1;
      }
    } else {
      double invD = 1.0 / direction.z();
      double t1 = (expandedMinZ - origin.z()) * invD;
      double t2 = (expandedMaxZ - origin.z()) * invD;
      if (t1 > t2) {
        double tmp = t1;
        t1 = t2;
        t2 = tmp;
      }
      tMin = Math.max(tMin, t1);
      tMax = Math.min(tMax, t2);
      if (tMin > tMax) {
        return -1;
      }
    }

    // The first intersection is at tMin; it must be within [0, 1]
    if (tMin >= 0.0 && tMin <= 1.0) {
      return tMin;
    }
    // If tMin is negative but tMax is in range, the origin is inside the box
    if (tMin < 0.0 && tMax >= 0.0 && tMax <= 1.0) {
      return 0.0;
    }
    // If the entire segment is inside the box
    if (tMin < 0.0 && tMax > 1.0) {
      return 0.0;
    }

    return -1;
  }

  /**
   * Tests whether a ray intersects an AABB. A simpler boolean version that checks
   * for any intersection along the ray (t >= 0, unbounded).
   *
   * @param origin    the origin of the ray
   * @param direction the direction of the ray
   * @param box       the AABB to test against
   * @return true if the ray intersects the box
   */
  public static boolean rayIntersectsAABB(Vec3d origin, Vec3d direction, AABB box) {
    double tMin = Double.NEGATIVE_INFINITY;
    double tMax = Double.POSITIVE_INFINITY;

    // X slab
    if (Math.abs(direction.x()) < EPSILON) {
      if (origin.x() < box.minX() || origin.x() > box.maxX()) {
        return false;
      }
    } else {
      double invD = 1.0 / direction.x();
      double t1 = (box.minX() - origin.x()) * invD;
      double t2 = (box.maxX() - origin.x()) * invD;
      if (t1 > t2) {
        double tmp = t1;
        t1 = t2;
        t2 = tmp;
      }
      tMin = Math.max(tMin, t1);
      tMax = Math.min(tMax, t2);
      if (tMin > tMax) {
        return false;
      }
    }

    // Y slab
    if (Math.abs(direction.y()) < EPSILON) {
      if (origin.y() < box.minY() || origin.y() > box.maxY()) {
        return false;
      }
    } else {
      double invD = 1.0 / direction.y();
      double t1 = (box.minY() - origin.y()) * invD;
      double t2 = (box.maxY() - origin.y()) * invD;
      if (t1 > t2) {
        double tmp = t1;
        t1 = t2;
        t2 = tmp;
      }
      tMin = Math.max(tMin, t1);
      tMax = Math.min(tMax, t2);
      if (tMin > tMax) {
        return false;
      }
    }

    // Z slab
    if (Math.abs(direction.z()) < EPSILON) {
      if (origin.z() < box.minZ() || origin.z() > box.maxZ()) {
        return false;
      }
    } else {
      double invD = 1.0 / direction.z();
      double t1 = (box.minZ() - origin.z()) * invD;
      double t2 = (box.maxZ() - origin.z()) * invD;
      if (t1 > t2) {
        double tmp = t1;
        t1 = t2;
        t2 = tmp;
      }
      tMin = Math.max(tMin, t1);
      tMax = Math.min(tMax, t2);
      if (tMin > tMax) {
        return false;
      }
    }

    // The ray intersects if the exit point is at t >= 0
    return tMax >= 0.0;
  }

}
