package com.github.manu585.fourelements.storage.sql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The threads database work runs on, so it never blocks the server thread.
 */
final class DatabaseExecutorPool {

  private final ExecutorService executor;

  DatabaseExecutorPool(int poolSize) {
    if (poolSize <= 0) {
      throw new IllegalArgumentException("Pool size must be greater than 0!");
    }
    this.executor = Executors.newFixedThreadPool(poolSize, daemonThreadFactory());
  }

  ExecutorService getExecutor() {
    return executor;
  }

  void shutdown() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  private static ThreadFactory daemonThreadFactory() {
    AtomicInteger threadNumber = new AtomicInteger(1);
    return r -> {
      Thread thread = new Thread(r, "FourElements-DB-" + threadNumber.getAndIncrement());
      thread.setDaemon(true);
      return thread;
    };
  }

}
