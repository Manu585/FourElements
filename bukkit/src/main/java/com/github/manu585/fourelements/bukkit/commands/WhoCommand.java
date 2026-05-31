package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.registry.Element;
import com.github.manu585.fourelements.bukkit.commands.system.FourElementsCommand;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class WhoCommand extends FourElementsCommand {

  private final BenderManager benderManager;

  public WhoCommand(BenderManager benderManager) {
    super("who");
    this.benderManager = benderManager;
  }

  @Override
  protected void execute(CommandContext<CommandSourceStack> context) {
    UUID uuid = Objects.requireNonNull(context.getSource().getExecutor()).getUniqueId();
    Bender bender = benderManager.getBender(uuid);

    Element element = bender.elements().getFirst();

    context.getSource().getSender().sendMessage(
            Component.text("You are an", NamedTextColor.YELLOW)
                    .appendSpace()
                    .append(Component.text(element.displayName(), element.textColor()))
                    .appendSpace()
                    .append(Component.text("Bender",  NamedTextColor.YELLOW)));
  }

}
