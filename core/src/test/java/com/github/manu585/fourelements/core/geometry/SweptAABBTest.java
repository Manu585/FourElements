package com.github.manu585.fourelements.core.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SweptAABBTest {

  private static final double DELTA = 1e-9;

  // --- sweep ---

  @Test
  void sweep_positiveDisplacement_expandsMaxBounds() {
    AABB box = new AABB(0, 0, 0, 1, 1, 1);
    Vec3d displacement = new Vec3d(5, 0, 0);
    AABB swept = SweptAABB.sweep(box, displacement);

    assertEquals(0.0, swept.minX(), DELTA);
    assertEquals(0.0, swept.minY(), DELTA);
    assertEquals(0.0, swept.minZ(), DELTA);
    assertEquals(6.0, swept.maxX(), DELTA);
    assertEquals(1.0, swept.maxY(), DELTA);
    assertEquals(1.0, swept.maxZ(), DELTA);
  }

  @Test
  void sweep_negativeDisplacement_expandsMinBounds() {
    AABB box = new AABB(5, 5, 5, 6, 6, 6);
    Vec3d displacement = new Vec3d(-3, -2, -1);
    AABB swept = SweptAABB.sweep(box, displacement);

    assertEquals(2.0, swept.minX(), DELTA);
    assertEquals(3.0, swept.minY(), DELTA);
    assertEquals(4.0, swept.minZ(), DELTA);
    assertEquals(6.0, swept.maxX(), DELTA);
    assertEquals(6.0, swept.maxY(), DELTA);
    assertEquals(6.0, swept.maxZ(), DELTA);
  }

  @Test
  void sweep_zeroDisplacement_returnsOriginalBox() {
    AABB box = new AABB(0, 0, 0, 1, 1, 1);
    AABB swept = SweptAABB.sweep(box, Vec3d.ZERO);

    assertEquals(box.minX(), swept.minX(), DELTA);
    assertEquals(box.minY(), swept.minY(), DELTA);
    assertEquals(box.minZ(), swept.minZ(), DELTA);
    assertEquals(box.maxX(), swept.maxX(), DELTA);
    assertEquals(box.maxY(), swept.maxY(), DELTA);
    assertEquals(box.maxZ(), swept.maxZ(), DELTA);
  }

  // --- sweepIntersects ---

  @Test
  void sweepIntersects_movingBoxHitsStaticBox_returnsTrue() {
    // Moving box starts at x=0..1, moves right by 5 units
    AABB movingBox = new AABB(0, 0, 0, 1, 1, 1);
    Vec3d displacement = new Vec3d(5, 0, 0);
    // Static box is at x=3..4
    AABB staticBox = new AABB(3, 0, 0, 4, 1, 1);

    assertTrue(SweptAABB.sweepIntersects(movingBox, displacement, staticBox));
  }

  @Test
  void sweepIntersects_movingBoxMissesStaticBox_returnsFalse() {
    // Moving box moves along X axis
    AABB movingBox = new AABB(0, 0, 0, 1, 1, 1);
    Vec3d displacement = new Vec3d(5, 0, 0);
    // Static box is off to the side on Y axis
    AABB staticBox = new AABB(3, 10, 0, 4, 11, 1);

    assertFalse(SweptAABB.sweepIntersects(movingBox, displacement, staticBox));
  }

  @Test
  void sweepIntersects_fastMovingBox_multipleBlockMovement_returnsTrue() {
    // Box moves 50 units (multiple blocks) along X
    AABB movingBox = new AABB(0, 0, 0, 1, 1, 1);
    Vec3d displacement = new Vec3d(50, 0, 0);
    // Static box is far away at x=30..31
    AABB staticBox = new AABB(30, 0, 0, 31, 1, 1);

    assertTrue(SweptAABB.sweepIntersects(movingBox, displacement, staticBox));
  }

  @Test
  void sweepIntersects_movingBoxStartsInsideStaticBox_returnsTrue() {
    // Moving box overlaps static box at start
    AABB movingBox = new AABB(0, 0, 0, 1, 1, 1);
    Vec3d displacement = new Vec3d(5, 0, 0);
    AABB staticBox = new AABB(-1, -1, -1, 2, 2, 2);

    assertTrue(SweptAABB.sweepIntersects(movingBox, displacement, staticBox));
  }

  @Test
  void sweepIntersects_boxMovesAwayFromTarget_returnsFalse() {
    // Box at origin moves to the left, static box is to the right
    AABB movingBox = new AABB(0, 0, 0, 1, 1, 1);
    Vec3d displacement = new Vec3d(-10, 0, 0);
    AABB staticBox = new AABB(5, 0, 0, 6, 1, 1);

    assertFalse(SweptAABB.sweepIntersects(movingBox, displacement, staticBox));
  }

  @Test
  void sweepIntersects_diagonalMovementHitsBox_returnsTrue() {
    AABB movingBox = new AABB(0, 0, 0, 1, 1, 1);
    Vec3d displacement = new Vec3d(5, 5, 5);
    // Static box is along the diagonal path
    AABB staticBox = new AABB(3, 3, 3, 4, 4, 4);

    assertTrue(SweptAABB.sweepIntersects(movingBox, displacement, staticBox));
  }
}
