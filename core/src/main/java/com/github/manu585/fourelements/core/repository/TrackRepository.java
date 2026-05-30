package com.github.manu585.fourelements.core.repository;

import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;

public interface TrackRepository {

  CompletableFuture<OptionalInt> getTrackId(String trackName);

  CompletableFuture<Integer> saveTrack(String name, String world, int checkpoints);

  CompletableFuture<Void> deleteTrack(int trackId);

  CompletableFuture<Void> updateCheckpointCount(int trackId, int checkpoints);

}
