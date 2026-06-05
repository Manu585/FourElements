package com.github.manu585.fourelements.core.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AABBTest {

  private static final double DELTA = 1e-9;

  // --- contains ---

  @Test
  void contains_pointInside_returnsTrue() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    assertTrue(box.contains(new Vec3d(5, 5, 5)));
  }

  @Test
  void contains_pointOutside_returnsFalse() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    assertFalse(box.contains(new Vec3d(15, 5, 5)));
  }

  @Test
  void contains_pointOutsideAllAxes_returnsFalse() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    assertFalse(box.contains(new Vec3d(-1, -1, -1)));
  }

  @Test
  void contains_pointOnEdge_returnsTrue() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    assertTrue(box.contains(new Vec3d(0, 0, 0)), "Min corner should be included");
    assertTrue(box.contains(new Vec3d(10, 10, 10)), "Max corner should be included");
    assertTrue(box.contains(new Vec3d(10, 5, 5)), "Face point should be included");
    assertTrue(box.contains(new Vec3d(0, 10, 0)), "Edge point should be included");
  }

  // --- intersects ---

  @Test
  void intersects_overlappingBoxes_returnsTrue() {
    AABB a = new AABB(0, 0, 0, 10, 10, 10);
    AABB b = new AABB(5, 5, 5, 15, 15, 15);
    assertTrue(a.intersects(b));
    assertTrue(b.intersects(a));
  }

  @Test
  void intersects_nonOverlapping_returnsFalse() {
    AABB a = new AABB(0, 0, 0, 10, 10, 10);
    AABB b = new AABB(20, 20, 20, 30, 30, 30);
    assertFalse(a.intersects(b));
    assertFalse(b.intersects(a));
  }

  @Test
  void intersects_touchingFaces_returnsTrue() {
    AABB a = new AABB(0, 0, 0, 10, 10, 10);
    AABB b = new AABB(10, 0, 0, 20, 10, 10);
    assertTrue(a.intersects(b), "Touching faces should count as intersecting");
  }

  @Test
  void intersects_containedBox_returnsTrue() {
    AABB outer = new AABB(0, 0, 0, 20, 20, 20);
    AABB inner = new AABB(5, 5, 5, 10, 10, 10);
    assertTrue(outer.intersects(inner));
    assertTrue(inner.intersects(outer));
  }

  // --- expand(double) ---

  @Test
  void expandDouble_increasesSizeOnAllSides() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    AABB expanded = box.expand(2.0);

    assertEquals(-2.0, expanded.minX(), DELTA);
    assertEquals(-2.0, expanded.minY(), DELTA);
    assertEquals(-2.0, expanded.minZ(), DELTA);
    assertEquals(12.0, expanded.maxX(), DELTA);
    assertEquals(12.0, expanded.maxY(), DELTA);
    assertEquals(12.0, expanded.maxZ(), DELTA);
  }

  @Test
  void expandDouble_negativeAmountShrinks() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    AABB shrunk = box.expand(-1.0);

    assertEquals(1.0, shrunk.minX(), DELTA);
    assertEquals(1.0, shrunk.minY(), DELTA);
    assertEquals(1.0, shrunk.minZ(), DELTA);
    assertEquals(9.0, shrunk.maxX(), DELTA);
    assertEquals(9.0, shrunk.maxY(), DELTA);
    assertEquals(9.0, shrunk.maxZ(), DELTA);
  }

  // --- expand(Vec3d) ---

  @Test
  void expandVec3d_positiveDirection_expandsMaxBounds() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    AABB expanded = box.expand(new Vec3d(5, 0, 0));

    assertEquals(0.0, expanded.minX(), DELTA);
    assertEquals(15.0, expanded.maxX(), DELTA);
    // Y and Z unchanged
    assertEquals(0.0, expanded.minY(), DELTA);
    assertEquals(10.0, expanded.maxY(), DELTA);
    assertEquals(0.0, expanded.minZ(), DELTA);
    assertEquals(10.0, expanded.maxZ(), DELTA);
  }

  @Test
  void expandVec3d_negativeDirection_expandsMinBounds() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    AABB expanded = box.expand(new Vec3d(-5, -3, 0));

    assertEquals(-5.0, expanded.minX(), DELTA);
    assertEquals(10.0, expanded.maxX(), DELTA);
    assertEquals(-3.0, expanded.minY(), DELTA);
    assertEquals(10.0, expanded.maxY(), DELTA);
    assertEquals(0.0, expanded.minZ(), DELTA);
    assertEquals(10.0, expanded.maxZ(), DELTA);
  }

  @Test
  void expandVec3d_mixedDirections() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    AABB expanded = box.expand(new Vec3d(5, -3, 2));

    assertEquals(0.0, expanded.minX(), DELTA);
    assertEquals(15.0, expanded.maxX(), DELTA);
    assertEquals(-3.0, expanded.minY(), DELTA);
    assertEquals(10.0, expanded.maxY(), DELTA);
    assertEquals(0.0, expanded.minZ(), DELTA);
    assertEquals(12.0, expanded.maxZ(), DELTA);
  }

  // --- center ---

  @Test
  void center_returnsMiddlePoint() {
    AABB box = new AABB(0, 0, 0, 10, 10, 10);
    Vec3d center = box.center();

    assertEquals(5.0, center.x(), DELTA);
    assertEquals(5.0, center.y(), DELTA);
    assertEquals(5.0, center.z(), DELTA);
  }

  @Test
  void center_asymmetricBox() {
    AABB box = new AABB(2, 4, 6, 10, 20, 30);
    Vec3d center = box.center();

    assertEquals(6.0, center.x(), DELTA);
    assertEquals(12.0, center.y(), DELTA);
    assertEquals(18.0, center.z(), DELTA);
  }

  // --- of() factory ---

  @Test
  void of_correctlyDeterminesMinMax() {
    // Pass corners in swapped order
    AABB box = AABB.of(new Vec3d(10, 20, 30), new Vec3d(1, 2, 3));

    assertEquals(1.0, box.minX(), DELTA);
    assertEquals(2.0, box.minY(), DELTA);
    assertEquals(3.0, box.minZ(), DELTA);
    assertEquals(10.0, box.maxX(), DELTA);
    assertEquals(20.0, box.maxY(), DELTA);
    assertEquals(30.0, box.maxZ(), DELTA);
  }

  @Test
  void of_normalOrder() {
    AABB box = AABB.of(new Vec3d(1, 2, 3), new Vec3d(10, 20, 30));

    assertEquals(1.0, box.minX(), DELTA);
    assertEquals(2.0, box.minY(), DELTA);
    assertEquals(3.0, box.minZ(), DELTA);
    assertEquals(10.0, box.maxX(), DELTA);
    assertEquals(20.0, box.maxY(), DELTA);
    assertEquals(30.0, box.maxZ(), DELTA);
  }

  @Test
  void of_mixedComponents() {
    // Some components swapped, some not
    AABB box = AABB.of(new Vec3d(10, 2, 30), new Vec3d(1, 20, 3));

    assertEquals(1.0, box.minX(), DELTA);
    assertEquals(2.0, box.minY(), DELTA);
    assertEquals(3.0, box.minZ(), DELTA);
    assertEquals(10.0, box.maxX(), DELTA);
    assertEquals(20.0, box.maxY(), DELTA);
    assertEquals(30.0, box.maxZ(), DELTA);
  }
}
