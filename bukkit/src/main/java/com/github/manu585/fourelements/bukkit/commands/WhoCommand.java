package com.github.manu585.fourelements.bukkit.commands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class WhoCommand extends FourElementsCommand {

  public WhoCommand() {
    super("who");
  }

  @Override
  protected void execute(CommandContext<CommandSourceStack> context) {
    context.getSource().getSender().sendRichMessage("<gray>Who! :D");
  }

}
