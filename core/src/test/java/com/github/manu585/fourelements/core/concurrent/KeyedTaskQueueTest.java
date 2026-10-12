package com.github.manu585.fourelements.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class KeyedTaskQueueTest {

  @Test
  void sameKey_runsInSubmissionOrder_oneAtATime() {
    KeyedTaskQueue<String> queue = new KeyedTaskQueue<>();
    List<String> log = new ArrayList<>();
    CompletableFuture<Void> gate = new CompletableFuture<>();

    CompletableFuture<String> first = queue.submit("a", () -> {
      log.add("first-start");
      return gate.thenApply(v -> "one");
    });
    CompletableFuture<String> second = queue.submit("a", () -> {
      log.add("second-start");
      return CompletableFuture.completedFuture("two");
    });

    assertEquals(List.of("first-start"), log);
    assertFalse(second.isDone());

    gate.complete(null);

    assertEquals("one", first.join());
    assertEquals("two", second.join());
    assertEquals(List.of("first-start", "second-start"), log);
  }

  @Test
  void differentKeys_doNotBlockEachOther() {
    KeyedTaskQueue<String> queue = new KeyedTaskQueue<>();
    queue.submit("a", CompletableFuture::new);

    CompletableFuture<String> other = queue.submit("b", () -> CompletableFuture.completedFuture("ok"));

    assertTrue(other.isDone());
  }

  @Test
  void failedTask_doesNotBlockFollowers() {
    KeyedTaskQueue<String> queue = new KeyedTaskQueue<>();

    CompletableFuture<String> failing = queue.submit("a", () -> CompletableFuture.failedFuture(new IllegalStateException("boom")));
    CompletableFuture<String> throwing = queue.submit("a", () -> {
      throw new IllegalArgumentException("sync boom");
    });
    CompletableFuture<String> after = queue.submit("a", () -> CompletableFuture.completedFuture("fine"));

    assertTrue(failing.isCompletedExceptionally());
    assertTrue(throwing.isCompletedExceptionally());
    assertEquals("fine", after.join());
  }

}
