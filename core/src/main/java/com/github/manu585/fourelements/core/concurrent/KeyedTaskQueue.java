package com.github.manu585.fourelements.core.concurrent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Runs asynchronous tasks one at a time <i>per key</i>, in submission order.
 *
 * <p>Tasks with different keys never wait on each other. A task starts only after the
 * future returned by the previous task for the same key has completed, successfully or
 * not, so a failing task never blocks the ones queued behind it.
 *
 * <p>The queue holds no thread of its own: a task runs on the submitting thread if its key
 * is idle, otherwise on whichever thread completed the previous task. Bookkeeping for a
 * key is dropped as soon as its queue drains.
 *
 * @param <K> key type (e.g. a player UUID)
 */
public final class KeyedTaskQueue<K> {

  // Completion marker of the most recently submitted task per key
  private final Map<K, CompletableFuture<Void>> tails = new ConcurrentHashMap<>();

  /**
   * Enqueue a task behind everything previously submitted for {@code key}.
   *
   * @param key  tasks sharing a key are serialized
   * @param task starts the asynchronous work; an exception thrown here fails the result
   * @param <R>  result type
   * @return a future mirroring the future the task produced
   */
  public <R> CompletableFuture<R> submit(K key, Supplier<CompletableFuture<R>> task) {
    CompletableFuture<R> result = new CompletableFuture<>();
    CompletableFuture<Void> tail = new CompletableFuture<>();

    // Atomic swap, every task observes exactly one predecessor
    CompletableFuture<Void> previous = tails.put(key, tail);

    Runnable run = () -> {
      try {
        task.get().whenComplete((value, error) -> {
          if (error != null) {
            result.completeExceptionally(error);
          } else {
            result.complete(value);
          }
          finish(key, tail);
        });
      } catch (Throwable error) {
        result.completeExceptionally(error);
        finish(key, tail);
      }
    };

    if (previous == null) {
      run.run();
    } else {
      previous.whenComplete((ignored, ignoredError) -> run.run());
    }
    return result;
  }

  private void finish(K key, CompletableFuture<Void> tail) {
    // Only clear the slot if no newer task has queued up behind this one
    tails.remove(key, tail);
    tail.complete(null);
  }

}
