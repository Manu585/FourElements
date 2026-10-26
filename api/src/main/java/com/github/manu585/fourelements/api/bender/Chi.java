package com.github.manu585.fourelements.api.bender;

/**
 * Read-only view of how much chi a bender has left.
 *
 * <p>It is a live view: the values change as the bender spends and regains chi, so read it
 * again instead of keeping a number around. Reading is safe from any thread.
 */
public interface Chi {

  /**
   * @return the chi left, between {@code 0} and {@link #max()}
   */
  double current();

  /**
   * @return the most chi the bender can have right now
   */
  double max();

  /**
   * @return {@link #current()} as a share of {@link #max()}, between {@code 0.0} (empty) and {@code 1.0} (full)
   */
  default double fraction() {
    double max = max();
    return max <= 0 ? 0 : current() / max;
  }

}
