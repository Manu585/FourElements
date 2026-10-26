package com.github.manu585.fourelements.api;

import com.github.manu585.fourelements.api.bender.OnlineBender;
import java.util.Optional;
import java.util.UUID;

/**
 * Primary service interface exposed by the FourElements plugin.
 *
 * <p>Obtain an instance via {@link FourElementsApi#get()}.</p>
 */
public interface FourElementsProvider {

  /**
   * Get the bender of a player who is currently online.
   *
   * @param uuid UUID of the player
   * @return the bender, or empty if that player is not online
   */
  Optional<OnlineBender> getOnlineBender(UUID uuid);

}
