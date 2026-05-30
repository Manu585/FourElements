package com.github.manu585.fourelements.api;

import java.util.UUID;

/**
 * Primary service interface exposed by the IceBoating plugin.
 *
 * <p>Obtain an instance via {@link FourElementsAPI#get()}.</p>
 */
public interface FourElementsProvider {

  /**
   * Checks whether the given player is currently in an active race.
   *
   * @param playerUuid the UUID of the player
   * @return {@code true} if the player is racing
   */
  boolean isRacing(UUID playerUuid);

}
