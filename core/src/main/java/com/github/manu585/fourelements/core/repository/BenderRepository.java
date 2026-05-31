package com.github.manu585.fourelements.core.repository;

import com.github.manu585.fourelements.api.bender.Bender;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BenderRepository {

  CompletableFuture<Void> savePlayer(Bender bender);

}
