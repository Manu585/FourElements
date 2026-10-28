package com.github.manu585.fourelements.core.tick;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TickSchedulerTest {

  private final List<Throwable> errors = new ArrayList<>();
  private final TickScheduler scheduler = new TickScheduler((_, t) -> errors.add(t));

  private void tick(int times) {
    for (int i = 0; i < times; i++) {
      scheduler.tick();
    }
  }

  @Test
  void intervalOne_runsEveryTick() {
    AtomicInteger runs = new AtomicInteger();
    scheduler.every(1, runs::incrementAndGet);

    tick(10);

    assertEquals(10, runs.get());
  }

  @Test
  void interval_runsOncePerInterval() {
    AtomicInteger runs = new AtomicInteger();
    scheduler.every(20, runs::incrementAndGet);

    tick(20);
    assertEquals(1, runs.get());
    tick(60);

    assertEquals(4, runs.get());
  }

  @Test
  void sameInterval_isSpreadOverDifferentTicks() {
    AtomicInteger runs = new AtomicInteger();
    for (int i = 0; i < 4; i++) {
      scheduler.every(4, runs::incrementAndGet);
    }

    for (int i = 0; i < 4; i++) {
      int before = runs.get();
      scheduler.tick();
      assertEquals(1, runs.get() - before);
    }
  }

  @Test
  void cancel_stopsFurtherRuns() {
    AtomicInteger runs = new AtomicInteger();
    Registration registration = scheduler.every(1, runs::incrementAndGet);

    tick(3);
    registration.cancel();
    tick(3);

    assertEquals(3, runs.get());
  }

  @Test
  void cancelFromInsideAnotherTickable_skipsItInTheSameTick() {
    AtomicInteger runs = new AtomicInteger();
    Registration[] victim = new Registration[1];
    scheduler.every(1, () -> victim[0].cancel());
    victim[0] = scheduler.every(1, runs::incrementAndGet);

    tick(5);

    assertEquals(0, runs.get());
  }

  @Test
  void failingTickable_isReportedAndOthersStillRun() {
    AtomicInteger runs = new AtomicInteger();
    scheduler.every(1, () -> {
      throw new IllegalStateException("boom");
    });
    scheduler.every(1, runs::incrementAndGet);

    tick(2);

    assertEquals(2, runs.get());
    assertEquals(2, errors.size());
  }

  @Test
  void scope_closeCancelsEverythingRegisteredThroughIt() {
    AtomicInteger runs = new AtomicInteger();
    TickScope scope = new TickScope(scheduler);
    scope.every(1, runs::incrementAndGet);
    scope.every(5, runs::incrementAndGet);

    tick(5);
    int beforeClose = runs.get();
    scope.close();
    tick(10);

    assertEquals(beforeClose, runs.get());
    assertThrows(IllegalStateException.class, () -> scope.every(1, runs::incrementAndGet));
  }

  @Test
  void clear_dropsEverything() {
    AtomicInteger runs = new AtomicInteger();
    scheduler.every(1, runs::incrementAndGet);

    scheduler.clear();
    tick(3);

    assertEquals(0, runs.get());
  }

  @Test
  void invalidInterval_isRejected() {
    assertThrows(IllegalArgumentException.class, () -> scheduler.every(0, () -> {}));
  }

}
