package com.github.manu585.fourelements.api;

import com.github.manu585.fourelements.api.bender.Bender;
import java.util.UUID;

/**
 * Primary service interface exposed by the IceBoating plugin.
 *
 * <p>Obtain an instance via {@link FourElementsAPI#get()}.</p>
 */
public interface FourElementsProvider {

  /**
   * Receive a {@link Bender} from given UUID
   *
   * @param uuid UUID of bender to get.
   * @return Bender or null
   */
  Bender getBender(UUID uuid);

}
