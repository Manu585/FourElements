package com.github.manu585.fourelements.core.repository;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerRepository {

  CompletableFuture<Void> savePlayer(UUID uuid, String username);

}
