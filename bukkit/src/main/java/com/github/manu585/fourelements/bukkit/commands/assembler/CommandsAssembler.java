package com.github.manu585.fourelements.bukkit.commands.assembler;

import com.github.manu585.fourelements.bukkit.commands.FourElementsCommand;
import com.github.manu585.fourelements.bukkit.commands.WhoCommand;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.List;

public final class CommandsAssembler {

  private final BenderManager benderManager;

  public CommandsAssembler(BenderManager benderManager) {
    this.benderManager = benderManager;
  }

  public LiteralCommandNode<CommandSourceStack> build() {
    LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("elements");
    for (FourElementsCommand command : List.of(new WhoCommand(benderManager))) {
      root.then(command.branch());
    }
    return root.build();
  }

}
