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

  protected String permission() {
    return null;
  }

  protected abstract void execute(CommandContext<CommandSourceStack> context);

  public LiteralArgumentBuilder<CommandSourceStack> branch() {
    return base().executes(this::run);
  }

  protected final LiteralArgumentBuilder<CommandSourceStack> base() {
    LiteralArgumentBuilder<CommandSourceStack> literal = Commands.literal(name);
    if (permission() != null) {
      literal.requires(src -> src.getSender().hasPermission(permission()));
    }
    return literal;
  }

  protected final int run(CommandContext<CommandSourceStack> context) {
    execute(context);
    return Command.SINGLE_SUCCESS;
  }

}
