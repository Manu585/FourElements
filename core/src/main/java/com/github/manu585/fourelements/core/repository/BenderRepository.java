package com.github.manu585.fourelements.core.repository;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.core.bender.BenderPlayer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BenderRepository {

  /**
   * Load a bender's persistent data.
   *
   * @param uuid bender UUID
   * @return a future completing with the {@link BenderPlayer}, or {@code null} if no such bender exists. The future completes exceptionally if the load fails
   */
  CompletableFuture<BenderPlayer> getBender(UUID uuid);

  /**
   * Persist a bender and its full element set (upsert).
   *
   * @param bender bender to save
   * @return a future completing when the write is done
   */
  CompletableFuture<Void> saveBender(Bender bender);

}
