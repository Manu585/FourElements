package com.github.manu585.fourelements.core.geometry;

/**
 * An immutable 3D vector with double precision.
 *
 * @param x the x component
 * @param y the y component
 * @param z the z component
 */
public record Vec3d(double x, double y, double z) {

  /**
   * The zero vector.
   */
  public static final Vec3d ZERO = new Vec3d(0, 0, 0);

  /**
   * Adds another vector to this vector.
   *
   * @param other the vector to add
   * @return a new vector representing the sum
   */
  public Vec3d add(Vec3d other) {
    return new Vec3d(x + other.x, y + other.y, z + other.z);
  }

  /**
   * Subtracts another vector from this vector.
   *
   * @param other the vector to subtract
   * @return a new vector representing the difference
   */
  public Vec3d subtract(Vec3d other) {
    return new Vec3d(x - other.x, y - other.y, z - other.z);
  }

  /**
   * Multiplies this vector by a scalar.
   *
   * @param scalar the scalar multiplier
   * @return a new scaled vector
   */
  public Vec3d multiply(double scalar) {
    return new Vec3d(x * scalar, y * scalar, z * scalar);
  }

  /**
   * Returns the length (magnitude) of this vector.
   *
   * @return the length
   */
  public double length() {
    return Math.sqrt(lengthSquared());
  }

  /**
   * Returns the squared length of this vector. Useful for comparisons to avoid sqrt.
   *
   * @return the squared length
   */
  public double lengthSquared() {
    return x * x + y * y + z * z;
  }

  /**
   * Returns a unit vector in the same direction as this vector.
   * If this is the zero vector, returns the zero vector.
   *
   * @return the normalized vector
   */
  public Vec3d normalize() {
    double len = length();
    if (len == 0) {
      return ZERO;
    }
    return new Vec3d(x / len, y / len, z / len);
  }

  /**
   * Returns the Euclidean distance from this vector to another.
   *
   * @param other the other vector
   * @return the distance
   */
  public double distanceTo(Vec3d other) {
    return Math.sqrt(distanceSquaredTo(other));
  }

  /**
   * Returns the squared Euclidean distance from this vector to another.
   *
   * @param other the other vector
   * @return the squared distance
   */
  public double distanceSquaredTo(Vec3d other) {
    double dx = x - other.x;
    double dy = y - other.y;
    double dz = z - other.z;
    return dx * dx + dy * dy + dz * dz;
  }

}
