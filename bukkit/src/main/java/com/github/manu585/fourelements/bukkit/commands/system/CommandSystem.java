package com.github.manu585.fourelements.bukkit.commands.system;

import com.github.manu585.fourelements.bukkit.commands.FourElementsCommand;
import com.github.manu585.fourelements.core.system.PluginSystem;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.List;
import org.bukkit.plugin.Plugin;

public final class CommandSystem implements PluginSystem {

  private final Plugin plugin;
  private final List<FourElementsCommand> commands;

  public CommandSystem(Plugin plugin, List<FourElementsCommand> commands) {
    this.plugin = plugin;
    this.commands = commands;
  }

  @Override
  public void enable() {
    plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("elements");
      for (FourElementsCommand command : commands) {
        root.then(command.build());
      }
      event.registrar().register(root.build(), "Four elements command", List.of("b", "bending", "fe"));
    });
  }

}
