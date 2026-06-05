package com.github.manu585.fourelements.core.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CollisionMathTest {

  private static final double DELTA = 1e-6;

  // --- computeSweptIntersectionT: hitting case ---

  @Test
  void computeSweptIntersectionT_rayHitsBox_returnsTInRange() {
    // Ray starts at (-5, 0.5, 0.5), direction is (10, 0, 0)
    // Box is at (2, 0, 0) to (4, 1, 1)
    // The ray should enter the box at t = (2 - (-5)) / 10 = 0.7
    Vec3d origin = new Vec3d(-5, 0.5, 0.5);
    Vec3d direction = new Vec3d(10, 0, 0);
    AABB target = new AABB(2, 0, 0, 4, 1, 1);

    double t = CollisionMath.computeSweptIntersectionT(origin, direction, Vec3d.ZERO, target);

    assertTrue(t >= 0.0 && t <= 1.0, "t should be in [0, 1], got: " + t);
    assertEquals(0.7, t, DELTA);
  }

  @Test
  void computeSweptIntersectionT_rayStartsInsideBox_returnsZero() {
    Vec3d origin = new Vec3d(3, 0.5, 0.5);
    Vec3d direction = new Vec3d(10, 0, 0);
    AABB target = new AABB(2, 0, 0, 4, 1, 1);

    double t = CollisionMath.computeSweptIntersectionT(origin, direction, Vec3d.ZERO, target);

    assertEquals(0.0, t, DELTA, "Should return 0.0 when starting inside the box");
  }

  // --- computeSweptIntersectionT: missing case ---

  @Test
  void computeSweptIntersectionT_rayMissesBox_returnsNegativeOne() {
    // Ray moves along X but the box is offset on Y
    Vec3d origin = new Vec3d(-5, 5, 0.5);
    Vec3d direction = new Vec3d(10, 0, 0);
    AABB target = new AABB(2, 0, 0, 4, 1, 1);

    double t = CollisionMath.computeSweptIntersectionT(origin, direction, Vec3d.ZERO, target);

    assertEquals(-1.0, t, DELTA);
  }

  @Test
  void computeSweptIntersectionT_boxBehindRay_returnsNegativeOne() {
    // Ray at origin moving right, box is to the left
    Vec3d origin = new Vec3d(5, 0.5, 0.5);
    Vec3d direction = new Vec3d(10, 0, 0);
    AABB target = new AABB(-4, 0, 0, -2, 1, 1);

    double t = CollisionMath.computeSweptIntersectionT(origin, direction, Vec3d.ZERO, target);

    assertEquals(-1.0, t, DELTA);
  }

  @Test
  void computeSweptIntersectionT_boxTooFarAway_returnsNegativeOne() {
    // Box is at x=100..102, ray direction length is 10 so t would be > 1
    Vec3d origin = new Vec3d(0, 0.5, 0.5);
    Vec3d direction = new Vec3d(10, 0, 0);
    AABB target = new AABB(100, 0, 0, 102, 1, 1);

    double t = CollisionMath.computeSweptIntersectionT(origin, direction, Vec3d.ZERO, target);

    assertEquals(-1.0, t, DELTA);
  }

  @Test
  void computeSweptIntersectionT_withHalfExtents_expandsTarget() {
    // Without half-extents, the ray would miss. With half-extents of 0.5 on each axis,
    // the box expands enough to be hit.
    Vec3d origin = new Vec3d(-5, 1.2, 0.5);
    Vec3d direction = new Vec3d(10, 0, 0);
    AABB target = new AABB(2, 0, 0, 4, 1, 1);
    Vec3d halfExtents = new Vec3d(0, 0.5, 0);

    // Without expansion, origin.y=1.2 is outside [0, 1]. With expansion, box.y is [-0.5, 1.5].
    double t = CollisionMath.computeSweptIntersectionT(origin, direction, halfExtents, target);

    assertTrue(t >= 0.0 && t <= 1.0, "Should hit with expanded half-extents, got: " + t);
  }

  // --- rayIntersectsAABB: hitting ---

  @Test
  void rayIntersectsAABB_directHit_returnsTrue() {
    Vec3d origin = new Vec3d(-5, 0.5, 0.5);
    Vec3d direction = new Vec3d(1, 0, 0);
    AABB box = new AABB(2, 0, 0, 4, 1, 1);

    assertTrue(CollisionMath.rayIntersectsAABB(origin, direction, box));
  }

  @Test
  void rayIntersectsAABB_originInsideBox_returnsTrue() {
    Vec3d origin = new Vec3d(3, 0.5, 0.5);
    Vec3d direction = new Vec3d(1, 0, 0);
    AABB box = new AABB(2, 0, 0, 4, 1, 1);

    assertTrue(CollisionMath.rayIntersectsAABB(origin, direction, box));
  }

  @Test
  void rayIntersectsAABB_diagonalRayHitsBox_returnsTrue() {
    Vec3d origin = new Vec3d(0, 0, 0);
    Vec3d direction = new Vec3d(1, 1, 1);
    AABB box = new AABB(4, 4, 4, 6, 6, 6);

    assertTrue(CollisionMath.rayIntersectsAABB(origin, direction, box));
  }

  // --- rayIntersectsAABB: missing ---

  @Test
  void rayIntersectsAABB_misses_returnsFalse() {
    Vec3d origin = new Vec3d(-5, 5, 0.5);
    Vec3d direction = new Vec3d(1, 0, 0);
    AABB box = new AABB(2, 0, 0, 4, 1, 1);

    assertFalse(CollisionMath.rayIntersectsAABB(origin, direction, box));
  }

  @Test
  void rayIntersectsAABB_parallelToBoxFace_misses() {
    // Ray parallel to X axis but offset on Y beyond the box
    Vec3d origin = new Vec3d(3, 2, 0.5);
    Vec3d direction = new Vec3d(1, 0, 0);
    AABB box = new AABB(2, 0, 0, 4, 1, 1);

    assertFalse(CollisionMath.rayIntersectsAABB(origin, direction, box));
  }

  @Test
  void rayIntersectsAABB_oppositeDirection_returnsFalse() {
    // Ray points away from the box
    Vec3d origin = new Vec3d(-5, 0.5, 0.5);
    Vec3d direction = new Vec3d(-1, 0, 0);
    AABB box = new AABB(2, 0, 0, 4, 1, 1);

    assertFalse(CollisionMath.rayIntersectsAABB(origin, direction, box));
  }
}
