package com.github.manu585.fourelements.bukkit.bootstrap;

import com.github.manu585.fourelements.api.FourElementsProvider;
import com.github.manu585.fourelements.bukkit.api.FourElementsProviderImpl;
import com.github.manu585.fourelements.bukkit.database.DatabaseManager;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.manager.bendermode.BendingModeCombination;
import com.github.manu585.fourelements.bukkit.manager.bendermode.SimpleInput;
import com.github.manu585.fourelements.bukkit.repository.MySqlBenderRepository;
import com.github.manu585.fourelements.core.registry.OnlineBenderRegistry;
import com.github.manu585.fourelements.core.repository.BenderRepository;
import java.util.List;
import org.jspecify.annotations.NonNull;

/**
 * The plugin's long-lived objects, wired once and immutable afterwards.
 *
 * <p>This is the only place that knows which implementation backs which service,
 * new repositories and managers get added here.
 */
record Services(BenderRepository benderRepository, BenderManager benderManager, BendingModeCombination bendingModeCombination, FourElementsProvider provider) {

  static @NonNull Services wire(DatabaseManager database) {
    BenderRepository benderRepository = new MySqlBenderRepository(database);
    BenderManager benderManager = new BenderManager(new OnlineBenderRegistry(), benderRepository);
    BendingModeCombination bendingModeCombination = new BendingModeCombination(List.of(SimpleInput.of(true, false, false, false, false, false, false)), benderManager);

    return new Services(benderRepository, benderManager, bendingModeCombination, new FourElementsProviderImpl(benderManager));
  }

}
