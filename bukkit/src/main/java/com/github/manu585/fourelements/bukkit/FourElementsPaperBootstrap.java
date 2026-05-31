package com.github.manu585.fourelements.bukkit;

import com.github.manu585.fourelements.bukkit.commands.assembler.FourElementsCommands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.List;

public final class FourElementsPaperBootstrap implements PluginBootstrap {

  // Command registration / trunk - branches building
  @Override
  public void bootstrap(BootstrapContext context) {
    context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(FourElementsCommands.build(), "Commands for the Four Elements plugin", List.of("fe", "bending", "b")));
  }

}
