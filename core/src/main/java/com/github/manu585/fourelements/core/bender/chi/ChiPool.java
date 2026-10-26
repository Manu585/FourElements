package com.github.manu585.fourelements.core.bender.chi;

import com.github.manu585.fourelements.api.bender.Chi;

/**
 * The chi of one online bender. Session-only: it starts full when the bender joins and is
 * never persisted. Only the plain bookkeeping lives here, when and how much to spend or
 * regain is decided by whoever calls it.
 *
 * <p>Reading is safe from any thread. Spending and restoring are atomic, but are meant to be
 * called from the main thread like the rest of the game logic.
 */
public final class ChiPool implements Chi {

  public static final double DEFAULT_MAX = 100;

  private volatile double max;
  private volatile double current;

  public ChiPool(double max) {
    requirePositive(max);
    this.max = max;
    this.current = max;
  }

  @Override
  public double current() {
    return current;
  }

  @Override
  public double max() {
    return max;
  }

  /**
   * Use up chi, but only if there is enough.
   *
   * @param amount chi to spend, not negative
   * @return <code>true</code> if it was spent, <code>false</code> if there was not enough (nothing is taken)
   */
  public synchronized boolean spend(double amount) {
    requireNotNegative(amount);
    if (amount > current) {
      return false;
    }
    current -= amount;
    return true;
  }

  /**
   * Give chi back, up to the maximum.
   *
   * @param amount chi to regain, not negative
   */
  public synchronized void restore(double amount) {
    requireNotNegative(amount);
    current = Math.min(max, current + amount);
  }

  /**
   * Change the maximum, e.g. when the bender levels up. Chi above a lower maximum is cut off.
   *
   * @param newMax the new maximum, greater than zero
   */
  public synchronized void max(double newMax) {
    requirePositive(newMax);
    max = newMax;
    current = Math.min(current, newMax);
  }

  private static void requirePositive(double value) {
    if (!(value > 0)) {
      throw new IllegalArgumentException("Chi maximum must be greater than 0, was " + value);
    }
  }

  private static void requireNotNegative(double amount) {
    if (!(amount >= 0)) {
      throw new IllegalArgumentException("Chi amount must not be negative, was " + amount);
    }
  }

}
