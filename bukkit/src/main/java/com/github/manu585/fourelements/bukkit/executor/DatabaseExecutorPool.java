package com.github.manu585.fourelements.bukkit.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

@Getter
public final class DatabaseExecutorPool {

  private final ExecutorService executor;

  public DatabaseExecutorPool(int poolSize) {
    this.executor = Executors.newFixedThreadPool(poolSize, daemonThreadFactory());
  }

  public void shutdown() {
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
