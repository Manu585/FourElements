package com.github.manu585.fourelements.core.bender.chi;

import com.github.manu585.fourelements.api.bender.Chi;
import java.util.function.Consumer;

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

  private volatile Consumer<? super Chi> onDeficit = _ -> {};

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
  public boolean spend(double amount) {
    requireNotNegative(amount);
    boolean leftFull;
    synchronized (this) {
      if (amount > current) {
        return false;
      }
      boolean wasFull = current >= max;
      current -= amount;
      leftFull = wasFull && current < max;
    }
    // Only accept callback if spending chi while being full
    // since then the replenish chi will start
    if (leftFull) {
      onDeficit.accept(this);
    }
    return true;
  }

  /**
   * Get told when the pool stops being full, e.g. to start giving chi back. It is not called
   * again while chi is still missing, only after the pool was full once more.
   */
  public void onDeficit(Consumer<? super Chi> listener) {
    this.onDeficit = listener;
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
  public void max(double newMax) {
    requirePositive(newMax);
    boolean leftFull;
    synchronized (this) {
      boolean wasFull = current >= max;
      max = newMax;
      current = Math.min(current, newMax);
      leftFull = wasFull && current < max;
    }
    if (leftFull) {
      onDeficit.accept(this);
    }
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
