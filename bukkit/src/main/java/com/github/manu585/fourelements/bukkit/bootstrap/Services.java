package com.github.manu585.fourelements.bukkit.bootstrap;

import com.github.manu585.fourelements.api.FourElementsProvider;
import com.github.manu585.fourelements.bukkit.api.FourElementsProviderImpl;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.manager.bendermode.BendingModeCombination;
import com.github.manu585.fourelements.bukkit.manager.bendermode.SimpleInput;
import com.github.manu585.fourelements.core.registry.OnlineBenderRegistry;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import com.github.manu585.fourelements.storage.bender.SqlBenderRepository;
import com.github.manu585.fourelements.storage.sql.Database;
import java.util.List;
import org.bukkit.Input;
import org.jspecify.annotations.NonNull;

/**
 * The plugin's long-lived objects, wired once and immutable afterwards.
 *
 * <p>This is the only place that knows which implementation backs which service,
 * new repositories and managers get added here.
 */
record Services(BenderRepository benderRepository, BenderManager benderManager, BendingModeCombination bendingModeCombination, FourElementsProvider provider) {

  static @NonNull Services wire(Database database) {
    BenderRepository benderRepository = new SqlBenderRepository(database);
    BenderManager benderManager = new BenderManager(new OnlineBenderRegistry(), benderRepository);
    // Tap sneak twice: sneak, let go, sneak
    Input sneak = SimpleInput.of(false, false, false, false, false, true, false);
    Input idle = SimpleInput.of(false, false, false, false, false, false, false);
    BendingModeCombination bendingModeCombination = new BendingModeCombination(List.of(sneak, idle, sneak), benderManager);
    FourElementsProviderImpl fourElementsProvider = new FourElementsProviderImpl(benderManager);

    return new Services(benderRepository, benderManager, bendingModeCombination, fourElementsProvider);
  }

}
