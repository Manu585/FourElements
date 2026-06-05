package com.github.manu585.fourelements.bukkit.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public abstract class FourElementsCommand {

  private final String name;

  protected FourElementsCommand(String name) {
    this.name = name;
  }

  protected abstract void execute(CommandContext<CommandSourceStack> context);

  public LiteralArgumentBuilder<CommandSourceStack> branch() {
    return Commands.literal(name).executes(this::run);
  }

  // TODO: Implement permission system
  protected final LiteralArgumentBuilder<CommandSourceStack> base() {
    return Commands.literal(name);
  }

  protected final int run(CommandContext<CommandSourceStack> context) {
    execute(context);
    return Command.SINGLE_SUCCESS;
  }

}
