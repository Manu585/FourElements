package com.github.manu585.fourelements.core.geometry;

/**
 * Swept AABB collision detection utilities.
 * Provides methods to compute swept volumes and detect collisions between a
 * moving box and a static box.
 */
public final class SweptAABB {

  private SweptAABB() {
    throw new AssertionError("No instances");
  }

  /**
   * Creates the full swept volume — the union of the box at its start position
   * and the box at the end position after displacement. This is equivalent to
   * expanding the AABB in the direction of the displacement.
   *
   * @param box          the original bounding box
   * @param displacement the movement vector
   * @return the swept volume as an AABB
   */
  public static AABB sweep(AABB box, Vec3d displacement) {
    return box.expand(displacement);
  }

  /**
   * Tests whether a moving box collides with a static box during a displacement.
   * Uses the Minkowski sum approach: expand the static box by the moving box's
   * half-extents, then ray-cast from the moving box's center along the displacement.
   *
   * @param movingBox    the box that is moving
   * @param displacement the displacement vector of the moving box
   * @param staticBox    the stationary box to test against
   * @return true if the moving box intersects the static box during the displacement
   */
  public static boolean sweepIntersects(AABB movingBox, Vec3d displacement, AABB staticBox) {
    // Compute half-extents of the moving box
    double halfX = (movingBox.maxX() - movingBox.minX()) / 2.0;
    double halfY = (movingBox.maxY() - movingBox.minY()) / 2.0;
    double halfZ = (movingBox.maxZ() - movingBox.minZ()) / 2.0;

    // Expand static box by moving box half-extents (Minkowski sum)
    AABB expanded = new AABB(
            staticBox.minX() - halfX,
            staticBox.minY() - halfY,
            staticBox.minZ() - halfZ,
            staticBox.maxX() + halfX,
            staticBox.maxY() + halfY,
            staticBox.maxZ() + halfZ
    );

    // Ray-cast from center of moving box
    Vec3d origin = movingBox.center();

    double t = CollisionMath.computeSweptIntersectionT(
            origin, displacement, new Vec3d(0, 0, 0), expanded
    );

    return t >= 0.0 && t <= 1.0;
  }

}
