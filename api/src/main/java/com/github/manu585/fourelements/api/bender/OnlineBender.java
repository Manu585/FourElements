package com.github.manu585.fourelements.api.bender;

/**
 * A {@link Bender} whose player is online: everything a {@link Bender} knows, plus what only
 * exists while they are playing and is gone when they leave.
 */
public interface OnlineBender extends Bender {

  /**
   * @return how much chi this bender has left
   */
  Chi chi();

}
